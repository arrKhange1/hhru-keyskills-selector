package com.arrkhange1.keywordsmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrioritizedKeySkillsService {
    private static final Logger logger = LoggerFactory.getLogger(PrioritizedKeySkillsService.class);
    private final Integer KEY_SKILLS_AMOUNT = 50;

    private PriorityQueue<Map.Entry<String, Integer>> fillPrioritizedSkills(Map<String, Integer> keywordsCounter) {
        PriorityQueue<Map.Entry<String, Integer>> prioritizedKeySkills =
            new PriorityQueue<>(KEY_SKILLS_AMOUNT + 1, Comparator.comparingInt(Map.Entry::getValue));

        for (var entry : keywordsCounter.entrySet()) {
            prioritizedKeySkills.add(entry);
            if (prioritizedKeySkills.size() > KEY_SKILLS_AMOUNT) {
                prioritizedKeySkills.poll();
            }
        }
        return prioritizedKeySkills;
    }

    private List<String> mapPrioritizedKeySkillsToList(PriorityQueue<Map.Entry<String, Integer>> prioritizedKeySkills) {
        return prioritizedKeySkills.stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<String> getKeySkills(Map<String, Integer> keywordsCounter) {
        PriorityQueue<Map.Entry<String, Integer>> prioritizedKeySkillsQueue
                = fillPrioritizedSkills(keywordsCounter);
        return mapPrioritizedKeySkillsToList(prioritizedKeySkillsQueue);
    }
}
