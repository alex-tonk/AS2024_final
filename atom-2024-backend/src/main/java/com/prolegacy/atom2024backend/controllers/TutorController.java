package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import com.prolegacy.atom2024backend.dto.TutorDto;
import com.prolegacy.atom2024backend.entities.ids.TutorId;
import com.prolegacy.atom2024backend.exceptions.TutorNotFoundException;
import com.prolegacy.atom2024backend.readers.TutorReader;
import com.prolegacy.atom2024backend.services.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("tutors")
@TypescriptEndpoint
public class TutorController {
    @Autowired
    private TutorReader tutorReader;
    @Autowired
    private TutorService tutorService;

    @GetMapping("{tutorId}")
    public TutorDto getTutor(@PathVariable TutorId tutorId) {
        return Optional.ofNullable(tutorReader.getTutor(tutorId))
                .orElseThrow(() -> new TutorNotFoundException(tutorId));
    }

    @GetMapping
    public List<TutorDto> getTutors() {
        return tutorReader.getTutors();
    }

    @PostMapping("search")
    public PageResponse<TutorDto> searchTutors(@RequestBody PageQuery pageQuery) {
        return tutorReader.searchTutors(pageQuery);
    }

    @PostMapping
    public TutorDto createTutor(@RequestBody TutorDto tutorDto) {
        return tutorService.createTutor(tutorDto);
    }

    @PutMapping("{tutorId}")
    public TutorDto updateTutor(@PathVariable TutorId tutorId, @RequestBody TutorDto tutorDto) {
        return tutorService.updateTutor(tutorId, tutorDto);
    }
}
