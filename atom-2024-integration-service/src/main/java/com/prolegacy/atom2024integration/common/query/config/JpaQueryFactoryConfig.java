package com.prolegacy.atom2024integration.common.query.config;

import com.prolegacy.atom2024integration.common.query.query.JPAQueryFactory;
import com.querydsl.jpa.JPQLTemplates;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaQueryFactoryConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }
}
