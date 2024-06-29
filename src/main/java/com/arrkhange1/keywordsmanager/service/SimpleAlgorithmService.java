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

    private final Integer KEY_SKILLS_AMOUNT = 50;

    PriorityQueue<Map.Entry<String, Integer>> prioritizedKeySkills = new PriorityQueue<>(KEY_SKILLS_AMOUNT + 1, Comparator.comparingInt(Map.Entry::getValue));

    @Autowired
    private VacancyApiService vacancyApiService;

    @Autowired
    private RedisJSONCacheRepository cacheJSONRepository;

    @Autowired
    private KeySkillsCounterService keySkillsCounterService;

    private void fillPrioritizedSkills(PriorityQueue<Map.Entry<String, Integer>> prioritizedKeywords, Map<String, Integer> keywordsCounter) {
        for (var entry : keywordsCounter.entrySet()) {
            prioritizedKeywords.add(entry);
            if (prioritizedKeywords.size() > KEY_SKILLS_AMOUNT) {
                prioritizedKeywords.poll();
            }
        }
    }

    private List<String> getKeySkills(PriorityQueue<Map.Entry<String, Integer>> prioritizedKeywords) {
        List<String> skills = new ArrayList<>(KEY_SKILLS_AMOUNT);
        while (!prioritizedKeywords.isEmpty()) {
            var keyword = prioritizedKeywords.poll();
            logger.info(keyword.toString());
            skills.add(keyword.getKey());
        }
        Collections.reverse(skills);
        return skills;
    }

    public List<String> getKeywords(String jobRequest) {
        List<String> keySkillsForJobRequest = (List<String>) cacheJSONRepository.get(jobRequest);
        if (keySkillsForJobRequest != null) {
            return keySkillsForJobRequest;
        }

        Vacancies vacanciesFromZeroPage = vacancyApiService.getVacancies(jobRequest, "name", "relevance");
        keySkillsCounterService.fillKeySkillsCounter(vacanciesFromZeroPage.items());

        fillPrioritizedSkills(prioritizedKeySkills, keySkillsCounterService.getKeySkillsCounter());
        var keySkills = getKeySkills(prioritizedKeySkills);
        cacheJSONRepository.setWithExpirationTime(jobRequest, keySkills, 1, TimeUnit.DAYS);
        return keySkills;
    }
}
