package com.prolegacy.atom2024integration.dto;

import com.prolegacy.atom2024integration.entities.ids.ProductId;
import lombok.Data;

@Data
public class ProductDto {
    ProductId id;
    String code;
    String caption;
}
