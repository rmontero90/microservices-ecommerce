package com.ecommerce.products_service.service;

import com.ecommerce.products_service.dto.ProductRequestDTO;
import com.ecommerce.products_service.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(String id);
    ProductResponseDTO updateProductById(String id, ProductRequestDTO requestDTO);
    void deleteProductById(String id);
}
