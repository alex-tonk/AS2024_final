package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.dto.TestDto;
import com.prolegacy.atom2024backend.dto.TestGroupDto;
import com.prolegacy.atom2024backend.dto.enums.TestGroupFilterEnum;
import com.prolegacy.atom2024backend.entities.ids.TestGroupId;
import com.prolegacy.atom2024backend.entities.ids.TestId;
import com.prolegacy.atom2024backend.readers.TestGroupReader;
import com.prolegacy.atom2024backend.readers.TestReader;
import com.prolegacy.atom2024backend.services.TestGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("test-groups")
@TypescriptEndpoint
public class TestGroupController {
    @Autowired
    private TestGroupReader testGroupReader;
    @Autowired
    private TestReader testReader;
    @Autowired
    private TestGroupService testGroupService;

    @GetMapping
    public List<TestGroupDto> getTestGroups(@RequestParam(required = false, defaultValue = "TestGroupFilterEnum.ALL") TestGroupFilterEnum filterEnum) {
        return testGroupReader.getTestGroups(filterEnum);
    }

    @GetMapping("{id}")
    public TestGroupDto getTestGroup(@PathVariable TestGroupId id) {
        return testGroupReader.getTestGroup(id);
    }

    @GetMapping("{id}/tests")
    public List<TestDto> getTests(@PathVariable TestGroupId id) {
        return testReader.getGroupTests(id);
    }

    @GetMapping("{id}/tests/{testId}")
    public TestDto getTest(@PathVariable TestGroupId id,
                           @PathVariable TestId testId) {
        return testReader.getGroupTest(id, testId);
    }

    @PostMapping
    public TestGroupDto createTestGroup(@RequestBody TestGroupDto testGroupDto) {
        return testGroupService.createTestGroup(testGroupDto);
    }

    @PutMapping
    public TestGroupDto copyTestGroup(@RequestBody TestGroupDto testGroupDto) {
        return testGroupService.createTestGroup(testGroupDto);
    }

    @DeleteMapping("{id}")
    public TestGroupDto cancelTest(@PathVariable TestGroupId id) {
        return testGroupService.cancelTest(id);
    }
}
