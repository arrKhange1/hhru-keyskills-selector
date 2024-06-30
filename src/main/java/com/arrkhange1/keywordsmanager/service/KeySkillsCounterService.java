package com.arrkhange1.keywordsmanager.service;

import com.arrkhange1.keywordsmanager.api.VacancyApiService;
import com.arrkhange1.keywordsmanager.model.VacanciesItem;
import com.arrkhange1.keywordsmanager.model.Vacancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeySkillsCounterService {
    @Autowired
    private CacheRepository<String, Object> cacheJSONRepository;

    @Autowired
    private VacancyApiService vacancyApiService;

    public Map<String, Integer> getKeySkillsCounter(List<VacanciesItem> vacanciesItems) {
        Map<String, Integer> keySkillsCounter = new HashMap<>();

        vacanciesItems.forEach(vacanciesItem -> {
            Vacancy vacancy = (Vacancy) cacheJSONRepository.get(vacanciesItem.id());
            if (vacancy == null) {
                vacancy = vacancyApiService.getVacancy(vacanciesItem.id());
            }
            if (vacancy != null) {
                cacheJSONRepository.set(vacanciesItem.id(), vacancy);
                vacancy.key_skills().forEach(keyword -> {
                    if (keySkillsCounter.containsKey(keyword.name())) {
                        keySkillsCounter.replace(keyword.name(), keySkillsCounter.get(keyword.name()) + 1);
                    } else {
                        keySkillsCounter.put(keyword.name(), 1);
                    }
                });
            }
        });
        return keySkillsCounter;
    }
}
