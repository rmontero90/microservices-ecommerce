package com.ecommerce.products_service.repository;

import com.ecommerce.products_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
