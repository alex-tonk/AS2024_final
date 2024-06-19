package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.dto.StudentRankingDto;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import com.prolegacy.atom2024backend.readers.StatisticsReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("statistics")
@TypescriptEndpoint
public class StatisticsController {

    @Autowired
    private StatisticsReader statisticsReader;

    @GetMapping("students")
    public List<StudentRankingDto> getStudentRankings(@RequestParam Boolean onSum,
                                                      @RequestParam Optional<TopicId> topicId) {
        return statisticsReader.getStudentRankings(onSum, topicId);
    }
}
