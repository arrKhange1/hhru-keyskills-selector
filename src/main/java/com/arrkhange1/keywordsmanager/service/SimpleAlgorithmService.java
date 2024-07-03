package com.arrkhange1.keywordsmanager.service;

import com.arrkhange1.keywordsmanager.api.VacancyApiService;
import com.arrkhange1.keywordsmanager.model.Vacancies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Primary
@Service
public class SimpleAlgorithmService implements AlgorithmService {

    private final Logger logger = LoggerFactory.getLogger(SimpleAlgorithmService.class);

    @Autowired
    private VacancyApiService vacancyApiService;

    @Autowired
    private RedisJSONCacheRepository cacheJSONRepository;

    @Autowired
    private KeySkillsCounterService keySkillsCounterService;

    @Autowired
    private PrioritizedKeySkillsService prioritizedKeySkillsService;
    
    private final ConcurrentHashMap<String, CompletableFuture<List<String>>> taskCache = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, List<String>> taskCache = new ConcurrentHashMap<>();

    public List<String> getKeywords(String jobRequest) {
        List<String> keySkillsForJobRequest = (List<String>) cacheJSONRepository.get(jobRequest);
        if (keySkillsForJobRequest != null) {
            return keySkillsForJobRequest;
        }
        
        Callable<List<String>> task = () -> {
        	Vacancies vacanciesFromZeroPage = vacancyApiService.getVacancies(jobRequest, "name", "relevance");
            Map<String, Integer> keySkillsCounter =
                    keySkillsCounterService.getKeySkillsCounter(vacanciesFromZeroPage.items());

            List<String> readyKeySkills = prioritizedKeySkillsService.getKeySkills(keySkillsCounter);
            cacheJSONRepository.setWithExpirationTime(jobRequest, readyKeySkills, 1, TimeUnit.DAYS);
            return readyKeySkills;
        };
        
        CompletableFuture<List<String>> future = taskCache.computeIfAbsent(jobRequest, key -> {
        	var compFuture = CompletableFuture.supplyAsync(() -> {
    			try {
    				return task.call();
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			return keySkillsForJobRequest;
    		});
        	logger.info("Comp. Future: " + compFuture.hashCode());
        	return compFuture;
        	
        });
        
        try {
            return future.get();
        } catch(Exception error) {
        	System.out.println("Exec error");
        	return null;
        } finally {
            taskCache.remove(jobRequest);
        }

    }
    
//    public List<String> getKeywords(String jobRequest) {
//        List<String> keySkillsForJobRequest = (List<String>) cacheJSONRepository.get(jobRequest);
//        if (keySkillsForJobRequest != null) {
//            return keySkillsForJobRequest;
//        }
//        
//        Callable<List<String>> task = () -> {
//        	Vacancies vacanciesFromZeroPage = vacancyApiService.getVacancies(jobRequest, "name", "relevance");
//            Map<String, Integer> keySkillsCounter =
//                    keySkillsCounterService.getKeySkillsCounter(vacanciesFromZeroPage.items());
//
//            List<String> readyKeySkills = prioritizedKeySkillsService.getKeySkills(keySkillsCounter);
//            cacheJSONRepository.setWithExpirationTime(jobRequest, readyKeySkills, 1, TimeUnit.DAYS);
//            return readyKeySkills;
//        };
//        
//        List<String> future = taskCache.computeIfAbsent(jobRequest, key -> {
//			try {
//				System.out.println("Hello from Without Future");
//				return task.call();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return keySkillsForJobRequest;
//		});
//        
//        try {
//            return future;
//        } catch(Exception error) {
//        	System.out.println("Exec error");
//        	return null;
//        } finally {
//            taskCache.remove(jobRequest);
//        }
//
//    }
    
}
