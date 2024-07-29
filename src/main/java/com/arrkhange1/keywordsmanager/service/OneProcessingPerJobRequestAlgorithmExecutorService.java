package com.arrkhange1.keywordsmanager.service;

import com.arrkhange1.keywordsmanager.repository.RedisJSONCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Primary
@Service
public class OneProcessingPerJobRequestAlgorithmExecutorService implements AlgorithmExecutor {

    private final ConcurrentHashMap<String, CompletableFuture<List<String>>> taskCache = new ConcurrentHashMap<>();

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private RedisJSONCacheRepository cacheJSONRepository;

    public List<String> execute(String jobRequest) {
        List<String> keySkillsForJobRequest = (List<String>) cacheJSONRepository.get(jobRequest);
        if (keySkillsForJobRequest != null) {
            return keySkillsForJobRequest;
        }

        CompletableFuture<List<String>> future = taskCache.computeIfAbsent(jobRequest, key -> CompletableFuture.supplyAsync(() -> {
            try {
                List<String> readyKeySkills = algorithmService.getKeySkills(jobRequest);
                cacheJSONRepository.setWithExpirationTime(jobRequest, readyKeySkills, 1, TimeUnit.DAYS);
                return readyKeySkills;
            } catch (Exception error) {
                System.out.println("Error with receiving ready key skills");
            }
            return null;
        }));

        try {
            return future.get();
        } catch(Exception error) {
            System.out.println("Error with getting the result from the future");
            return null;
        } finally {
            taskCache.remove(jobRequest);
        }
    }
}
