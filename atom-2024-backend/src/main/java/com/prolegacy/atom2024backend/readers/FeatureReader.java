package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.FeatureDto;
import com.prolegacy.atom2024backend.entities.QFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class FeatureReader {
    private static final QFeature feature = QFeature.feature;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<FeatureDto> getFeatures() {
        return baseQuery().fetch();
    }

    private JPAQuery<FeatureDto> baseQuery() {
        return queryFactory.from(feature)
                .selectDto(FeatureDto.class);
    }
}
