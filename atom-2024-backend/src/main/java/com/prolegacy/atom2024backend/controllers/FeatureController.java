package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.dto.FeatureDto;
import com.prolegacy.atom2024backend.readers.FeatureReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("features")
@TypescriptEndpoint
public class FeatureController {

    @Autowired
    private FeatureReader featureReader;

    @GetMapping
    public List<FeatureDto> getFeatures() {
        return featureReader.getFeatures();
    }
}
