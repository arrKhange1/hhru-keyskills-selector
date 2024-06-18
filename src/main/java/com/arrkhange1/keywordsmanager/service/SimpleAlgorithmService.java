package com.arrkhange1.keywordsmanager.service;

import com.arrkhange1.keywordsmanager.model.Vacancies;
import com.arrkhange1.keywordsmanager.model.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;


@Primary
@Service
public class SimpleAlgorithmService implements AlgorithmService {

    private final Logger logger = LoggerFactory.getLogger(SimpleAlgorithmService.class);

    Map<String, Integer> keywordsCounter = new HashMap<>();

    PriorityQueue<Map.Entry<String, Integer>> prioritizedKeywords = new PriorityQueue<>(51, (k1, k2)
            -> k2.getValue() - k1.getValue());

    public List<String> getKeywords(String jobRequest) {

    // hash map
//        all elements for O(N)
//        sort hash map for getting first 50 for O(nlogn)

        // tree map
//        all elements for O(nlogn)
//        take first 50 for O(1)

        // linked hash map
//        all elements for O(n)
//        O(nlogn)

        // priority queue
//        all elements for O(nlogn)
//        take first O(klogk) - constant

//        if (!keywordsCounter.isEmpty()) {
//
//            for (var entry : keywordsCounter.entrySet()) {
//                if (prioritizedKeywords.size() > 50) {
//                    prioritizedKeywords.poll();
//                }
//                else {
//                    prioritizedKeywords.add(entry);
//                }
//            }
//            if (prioritizedKeywords.size() > 50) {
//                prioritizedKeywords.poll();
//            }
//
//            while (!prioritizedKeywords.isEmpty()) {
//                logger.info(prioritizedKeywords.poll().toString());
//            }
//
//            return null;
//        }

        String baseUrl = "https://api.hh.ru/vacancies";
        RestClient customClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new RequestInterceptor())
                .build();

        Vacancies vacancies = customClient.get()
                .uri("?text={text}&search_field={search_field}", jobRequest, "name")
                .retrieve()
                .body(Vacancies.class);

        logger.info(vacancies + " Кол-во вакансий: " + vacancies.items().size());

        vacancies.items().forEach(vacanciesItem -> {
            Vacancy vacancy = customClient.get()
                    .uri("/{vacancy_id}", vacanciesItem.id())
                    .retrieve()
                    .body(Vacancy.class);
            vacancy.key_skills().forEach(keyword -> {
                if (keywordsCounter.containsKey(keyword.name())) {
                    keywordsCounter.replace(keyword.name(), keywordsCounter.get(keyword.name())+1);
                }
                else {
                    keywordsCounter.put(keyword.name(), 1);
                }
            });
        });

        logger.info(keywordsCounter + " Кол-во ключ. слов: " + keywordsCounter.size());


        return List.of(jobRequest);
    }
}
