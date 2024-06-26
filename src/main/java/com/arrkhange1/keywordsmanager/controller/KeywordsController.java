package com.arrkhange1.keywordsmanager.controller;

import com.arrkhange1.keywordsmanager.model.Keyword;
import com.arrkhange1.keywordsmanager.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/keyword")
public class KeywordsController {

    @Autowired
    AlgorithmService algorithmService;

    @GetMapping("")
    public List<String> getKeywords(@RequestParam("topic") String jobRequest) {
        return algorithmService.getKeywords(jobRequest);
    }
}
