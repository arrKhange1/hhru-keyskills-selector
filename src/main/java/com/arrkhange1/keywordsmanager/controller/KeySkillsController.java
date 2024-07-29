package com.arrkhange1.keywordsmanager.controller;

import com.arrkhange1.keywordsmanager.service.AlgorithmExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/keyskills")
public class KeySkillsController {
	
    @Autowired
    AlgorithmExecutor algorithmExecutor;

    @GetMapping("")
    public List<String> getKeySkills(@RequestParam("topic") String jobRequest) {
        return algorithmExecutor.execute(jobRequest);
    }
}
