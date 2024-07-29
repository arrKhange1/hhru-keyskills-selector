package com.arrkhange1.keywordsmanager.service;

import com.arrkhange1.keywordsmanager.api.VacancyApiService;
import com.arrkhange1.keywordsmanager.model.Vacancies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Primary
@Service
public class SimpleAlgorithmService implements AlgorithmService {

    @Autowired
    private VacancyApiService vacancyApiService;

    @Autowired
    private KeySkillsCounterService keySkillsCounterService;

    @Autowired
    private PrioritizedKeySkillsService prioritizedKeySkillsService;

    public List<String> getKeySkills(String jobRequest) {
        Vacancies vacanciesFromZeroPage = vacancyApiService.getVacancies(jobRequest, "name", "relevance");
        Map<String, Integer> keySkillsCounter =
                keySkillsCounterService.getKeySkillsCounter(vacanciesFromZeroPage.items());
        return prioritizedKeySkillsService.getKeySkills(keySkillsCounter);
    }
}
