package com.arrkhange1.keywordsmanager.api;

import com.arrkhange1.keywordsmanager.model.Vacancies;
import com.arrkhange1.keywordsmanager.model.Vacancy;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class VacancyApiService {
    private final String baseUrl = "https://api.hh.ru/vacancies";

    @Autowired
    ClientHttpRequestInterceptor requestInterceptor;

    private RestClient vacanciesRestClient;

    @PostConstruct
    public void init() {
        vacanciesRestClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(requestInterceptor)
                .build();
    }

    public Vacancies getVacancies(String query, String searchField, Integer perPage, String orderBy, Integer page) {
        return vacanciesRestClient.get()
                .uri("?text={text}&search_field={search_field}&per_page={per_page}&order_by={order_by}", query, searchField, perPage, orderBy)
                .retrieve()
                .body(Vacancies.class);
    }

    public Vacancy getVacancy(String vacancyId) {
        return vacanciesRestClient.get()
                .uri("/{vacancy_id}", vacancyId)
                .retrieve()
                .body(Vacancy.class);
    }
}
