package com.prolegacy.atom2024integration.entities;

import com.prolegacy.atom2024integration.entities.ids.ProductId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(schema = "dictionaries")
@Data
@Getter
public class Product {
    @Id
    ProductId id;
    @Column(name = "s_code")
    String code;
    @Column(name = "s_caption")
    String caption;
}
