package com.arrkhange1.keywordsmanager.service;

import com.arrkhange1.keywordsmanager.api.VacancyApiService;
import com.arrkhange1.keywordsmanager.model.Vacancies;
import com.arrkhange1.keywordsmanager.model.VacanciesItem;
import com.arrkhange1.keywordsmanager.model.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;


@Primary
@Service
public class SimpleAlgorithmService implements AlgorithmService {

    private final Logger logger = LoggerFactory.getLogger(SimpleAlgorithmService.class);

    private final Integer KEY_SKILLS_AMOUNT = 50;

    Map<String, Integer> keySkillsCounter = new HashMap<>();

    PriorityQueue<Map.Entry<String, Integer>> prioritizedKeySkills = new PriorityQueue<>(KEY_SKILLS_AMOUNT + 1, Comparator.comparingInt(Map.Entry::getValue));

    List<VacanciesItem> vacancies = new ArrayList<>();

    @Autowired
    private VacancyApiService vacancyApiService;

    @Autowired
    private RedisJSONCacheRepository cacheJSONRepository;

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

    private void fillKeySkillsCounter(String jobRequest) {
        Vacancies vacanciesFromZeroPage = vacancyApiService.getVacancies(jobRequest, "name", "relevance");

        vacancies.addAll(vacanciesFromZeroPage.items());

        vacancies.forEach(vacanciesItem -> {
            Vacancy vacancy = vacancyApiService.getVacancy(vacanciesItem.id());
            if (vacancy != null) {
                vacancy.key_skills().forEach(keyword -> {
                    if (keySkillsCounter.containsKey(keyword.name())) {
                        keySkillsCounter.replace(keyword.name(), keySkillsCounter.get(keyword.name()) + 1);
                    } else {
                        keySkillsCounter.put(keyword.name(), 1);
                    }
                });
            }
        });
    }

    public List<String> getKeywords(String jobRequest) {
        List<String> keySkillsForJobRequest = (List<String>) cacheJSONRepository.get(jobRequest);
        if (keySkillsForJobRequest != null) {
            return keySkillsForJobRequest;
        }

        fillKeySkillsCounter(jobRequest);

        logger.info(keySkillsCounter.size() + "");

        fillPrioritizedSkills(prioritizedKeySkills, keySkillsCounter);
        var keySkills = getKeySkills(prioritizedKeySkills);
        cacheJSONRepository.set(jobRequest, keySkills);
        return keySkills;
    }
}
