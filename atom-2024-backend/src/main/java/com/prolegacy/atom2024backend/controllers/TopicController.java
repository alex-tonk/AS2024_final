package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.dto.TopicDto;
import com.prolegacy.atom2024backend.readers.TopicReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("topics")
@TypescriptEndpoint
public class TopicController {

    @Autowired
    private TopicReader topicReader;

    @GetMapping
    public List<TopicDto> getTopics() {
        return topicReader.getTopics();
    }
}
