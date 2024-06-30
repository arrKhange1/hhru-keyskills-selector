package com.arrkhange1.keywordsmanager.service;

import com.arrkhange1.keywordsmanager.api.VacancyApiService;
import com.arrkhange1.keywordsmanager.model.Vacancies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public List<String> getKeywords(String jobRequest) {
        List<String> keySkillsForJobRequest = (List<String>) cacheJSONRepository.get(jobRequest);
        if (keySkillsForJobRequest != null) {
            return keySkillsForJobRequest;
        }

        Vacancies vacanciesFromZeroPage = vacancyApiService.getVacancies(jobRequest, "name", "relevance");
        Map<String, Integer> keySkillsCounter =
                keySkillsCounterService.getKeySkillsCounter(vacanciesFromZeroPage.items());

        List<String> readyKeySkills = prioritizedKeySkillsService.getKeySkills(keySkillsCounter);
        cacheJSONRepository.setWithExpirationTime(jobRequest, readyKeySkills, 1, TimeUnit.DAYS);
        return readyKeySkills;
    }
}
