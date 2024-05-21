package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.ProductDto;
import com.prolegacy.atom2024backend.entities.QProduct;
import com.prolegacy.atom2024backend.entities.ids.ProductId;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ProductReader {
    private static final QProduct product = QProduct.product;

    @Autowired
    private JPAQueryFactory queryFactory;

    public List<ProductDto> getProducts() {
        return baseQuery().fetch();
    }

    public List<ProductDto> getProductsForStand(StandId standId) {
        return baseQuery()
                .where(product.standId.eq(standId))
                .fetch();
    }

    public ProductDto getProduct(ProductId id) {
        return baseQuery()
                .where(product.globalId.eq(id))
                .fetchFirst();
    }

    private JPAQuery<ProductDto> baseQuery() {
        return queryFactory.from(product)
                .selectDto(ProductDto.class);
    }
}
