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
    private final String BASE_URL = "https://api.hh.ru/vacancies";
    private final Integer NUMBER_OF_VACANCIES = 10;

    @Autowired
    ClientHttpRequestInterceptor requestInterceptor;

    private RestClient vacanciesRestClient;

    @PostConstruct
    public void init() {
        vacanciesRestClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .requestInterceptor(requestInterceptor)
                .build();
    }

    public Vacancies getVacancies(String query, String searchField, String orderBy) {
        int numberOfVacancies = NUMBER_OF_VACANCIES;
        while(numberOfVacancies != 0) {
            try {
                return vacanciesRestClient.get()
                        .uri("?text={text}&search_field={search_field}&per_page={per_page}&order_by={order_by}", query, searchField, numberOfVacancies, orderBy)
                        .retrieve()
                        .body(Vacancies.class);
            } catch (RuntimeException error) {
                numberOfVacancies = numberOfVacancies / 2;
            }
        }
        throw new RuntimeException("Can't get vacancies at the current moment");
    }

    public Vacancy getVacancy(String vacancyId) {
        try {
            return vacanciesRestClient.get()
                    .uri("/{vacancy_id}", vacancyId)
                    .retrieve()
                    .body(Vacancy.class);
        } catch(RuntimeException error) {
            return null;
        }
    }
}
