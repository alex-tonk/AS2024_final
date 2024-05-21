package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.dto.ProductDto;
import com.prolegacy.atom2024backend.dto.StandDto;
import com.prolegacy.atom2024backend.entities.ids.ProductId;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import com.prolegacy.atom2024backend.readers.ProductReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("products")
@TypescriptEndpoint
public class ProductController {

    @Autowired
    private ProductReader productReader;

    @GetMapping
    public List<ProductDto> getProducts() {
        return productReader.getProducts();
    }

    @GetMapping("{id}")
    public ProductDto getProduct(@PathVariable ProductId id) {
        return productReader.getProduct(id);
    }
}
