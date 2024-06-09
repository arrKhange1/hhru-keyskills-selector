package com.arrkhange1.keywordsmanager.service;

import com.arrkhange1.keywordsmanager.model.Vacancies;
import com.arrkhange1.keywordsmanager.model.VacanciesItem;
import com.arrkhange1.keywordsmanager.model.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Primary
@Service
public class SimpleAlgorithmService implements AlgorithmService {

    private final Logger logger = LoggerFactory.getLogger(SimpleAlgorithmService.class);

    public List<String> getKeywords(String jobRequest) {

        String baseUrl = "https://api.hh.ru/vacancies";
        RestClient customClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new RequestInterceptor())
                .build();

        Vacancies vacancies = customClient.get()
                .uri("?text={text}&search_field={search_field}", jobRequest, "name")
                .retrieve()
                .body(Vacancies.class);



        logger.info(vacancies.toString());

        VacanciesItem firstVacancy = vacancies.items().getFirst();

        Vacancy vacancy = customClient.get()
                .uri("/{vacancy_id}", firstVacancy.id())
                .retrieve()
                .body(Vacancy.class);

        logger.info(vacancy.toString());

        return List.of(jobRequest);
    }
}
