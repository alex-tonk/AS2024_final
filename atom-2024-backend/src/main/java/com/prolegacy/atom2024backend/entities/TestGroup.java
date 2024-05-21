package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.dto.TestDto;
import com.prolegacy.atom2024backend.dto.TestGroupDto;
import com.prolegacy.atom2024backend.entities.ids.TestGroupId;
import com.prolegacy.atom2024backend.entities.ids.TestId;
import com.prolegacy.atom2024backend.exceptions.TestNotFoundException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
public class TestGroup {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private TestGroupId id;

    private String comment;
    @Setter(AccessLevel.PUBLIC)
    private Instant startDate;
    @Setter(AccessLevel.PUBLIC)
    private Instant endDate;
    @ManyToOne
    private User startUser;
    @OneToMany(mappedBy = "testGroup", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Test> tests = new ArrayList<>();

    public TestGroup(TestGroupDto dto, User startUser) {
        this.comment = dto.getComment();
        this.startUser = startUser;
    }

    public Test addTest(TestDto testDto, StandEndpoint standEndpoint, Product product) {
        Test test = new Test(standEndpoint, this, product, testDto);
        this.tests.add(test);
        return test;
    }

    public Test getTest(TestId testId) {
        return this.tests.stream().filter(t -> t.getId().equals(testId))
                .findFirst()
                .orElseThrow(TestNotFoundException::new);
    }
}
