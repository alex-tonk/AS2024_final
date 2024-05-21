package com.prolegacy.atom2024integration.readers;

import com.prolegacy.atom2024integration.common.query.query.JPAQuery;
import com.prolegacy.atom2024integration.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024integration.dto.ThermoDto;
import com.prolegacy.atom2024integration.entities.QPersonal;
import com.prolegacy.atom2024integration.entities.QProduct;
import com.prolegacy.atom2024integration.entities.QThermo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class ThermoReader {
    private static final QThermo thermo = QThermo.thermo;
    private static final QPersonal registeredBy = new QPersonal("registrator");
    private static final QPersonal executionBy = new QPersonal("executor");
    private static final QProduct product = QProduct.product;

    @Autowired
    private JPAQueryFactory queryFactory;

    public ThermoDto getThermo(Long thermoId) {
        return baseQuery().where(thermo.id.eq(thermoId)).fetchFirst();
    }

    private JPAQuery<ThermoDto> baseQuery() {
        return queryFactory.from(thermo)
                .leftJoin(registeredBy).on(registeredBy.id.eq(thermo.registeredBy))
                .leftJoin(executionBy).on(executionBy.id.eq(thermo.executionBy))
                .leftJoin(product).on(product.id.eq(thermo.productId))
                .selectDto(ThermoDto.class);
    }
}
