package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.ProductDto;
import com.prolegacy.atom2024backend.entities.ids.ProductId;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "id", "standId" }) })
public class Product {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private ProductId globalId;

    Long id;
    StandId standId;

    private String code;
    private String caption;

    public Product(ProductDto dto) {
        this.id = dto.getId();
        this.standId = dto.getStandId();
        this.code = dto.getCode();
        this.caption = dto.getCaption();
    }
}
