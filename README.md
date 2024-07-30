# HeadHunter KeySkills Selector
Проект был сделан с целью автоматизации сбора наиболее используемых ключевых навыков по желаемой должности для резюме соискателя. Проект поможет соискателю подобрать такие ключевые навыки, чтобы всегда быть в ТОПе у рекрутеров

## Описание проекта

Пользователь вводит желаемую должность, и в качестве результата выдаются наиболее используемые работодателями ключевые навыки <br>

Пользователь ищет навыки по запросу "java":
<img width="890" alt="image" src="https://github.com/user-attachments/assets/0b9d33ea-ff7b-497c-8b06-b234793e77bb">

В качестве ответа пользователь получает 50 ключевых навыков на запрос "java" **в приоритетном порядке**:
```
[
    "Java",
    "SQL",
    "Spring Framework",
    "PostgreSQL",
    "ООП",
    "Spring Boot",
    "Java SE",
    "Spring",
    "JavaScript",
    "React",
    "Git",
    "HTML",
    "Kafka",
    "TeamCity",
    "Angular",
    "Databases",
    "Docker",
    "REST",
    "ORACLE",
    "Hibernate ORM",
    "Hibernate",
    "Gradle",
    "Spring Web MVC",
    "RabbitMQ",
    "MongoDB",
    "Kubernetes",
    "JUnit",
    "REST API",
    "Apache Maven",
    "Linux",
    "Maven",
    "Java EE",
    "Kotlin",
    "Внимательность",
    "Spring Cloud",
    "Gitlab",
    "Scrum",
    "Agile",
    "MS SQL",
    "Работа в команде",
    "CI/CD",
    "Java 11",
    "Apache Kafka",
    "SOLID",
    "Transact-SQL",
    "Аналитическое мышление",
    "Java core",
    "MSSQL",
    "Работа с большим объемом информации",
    "Apache Tomcat"
]
```
## Оптимизация одновременных запросов 
Пусть ключевые навыки собираются 100 пользователями из 100 вакансий по запросу "java". Тогда каждый пользователь будет одновременно с другими делать запросы на hh.ru
за вакансиями, и получится 100*100 = 10000 запросов.
Во-первых, это нагружает интеграционное API и интернет-трафик сервера
Во-вторых, замедляет работу отдельных пользователей

Оптимизация: Пока кто-то начал выполнение подбора навыков по запросу "java", остальные будут ожидать результата.
Таким образом, в случае выше:
* Запросы сервера сокращены с 10000 до 100
* Запрос пользователей отрабатывают за одинаковое время независимо от количества одновременных обращений: 100, 1000 или 2000

Ниже продемонстрировано, что время запроса пользователя не меняется (с учетом погрешности) с изменением количества одновременных пользователей (графа Samples): <br>
<img width="1108" alt="image" src="https://github.com/user-attachments/assets/5439f9d2-0811-4f76-a694-c61039244d0c"> <br>
<img width="1108" alt="image" src="https://github.com/user-attachments/assets/8394cd5f-c1bc-420b-8c53-2aab3850f966"> <br>
<img width="1108" alt="image" src="https://github.com/user-attachments/assets/c18ec734-be8a-4370-b5eb-23267a982499">

## Оптимизация подбора ключевых навыков по уже запрашиваемым словам
Пусть кто-то уже получил результат по запросу "java". Это значит, что остальным по данному запросу нужно выдавать закэшированные ключевые навыки

Кэширование навыков по уже запрашиваемым словам осуществляется в Redis. Кэшу на запрос устанавливается время жизни 1 день:
```
List<String> readyKeySkills = algorithmService.getKeySkills(jobRequest);
cacheJSONRepository.setWithExpirationTime(jobRequest, readyKeySkills, 1, TimeUnit.DAYS);
```

Получение ключевых навыков для запроса из кэша:
```
List<String> keySkillsForJobRequest = (List<String>) cacheJSONRepository.get(jobRequest);
if (keySkillsForJobRequest != null) {
    return keySkillsForJobRequest;
}
```

Если в кэше нет запрошенного слова, то в процессе подбора навыков для каждой вакансии будут закэшированны ее ключевые навыки. Так если у двух разных запросов будут совпадения в вакансиях, то подбор навыков ускорится за счет наличия вакансии в кэше:
```
Vacancy vacancy = (Vacancy) cacheJSONRepository.get(vacanciesItem.id());
if (vacancy == null) {
    vacancy = vacancyApiService.getVacancy(vacanciesItem.id());
}
if (vacancy != null) {
    cacheJSONRepository.set(vacanciesItem.id(), vacancy);
    ...
}
```

# Технологии

## Backend

* Java (Spring Boot, Spring Framework, Spring Data Redis)
* Redis

## Frontend

* Postman
* JMeter
