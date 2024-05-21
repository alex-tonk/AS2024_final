package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.Product;
import com.prolegacy.atom2024backend.entities.ids.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, ProductId> {
}
