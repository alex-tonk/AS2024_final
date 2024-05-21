package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.ProductId;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductDto {
    public static ProductDto EMPTY = new ProductDto(new ProductId(-1L), -1L, null, null, null);

    private ProductId globalId;
    private Long id;
    private StandId standId;
    private String code;
    private String caption;
}
