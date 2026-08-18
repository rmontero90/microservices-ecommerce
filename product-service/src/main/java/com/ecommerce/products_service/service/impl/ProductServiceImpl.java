package com.ecommerce.products_service.service.impl;

import com.ecommerce.products_service.dto.ProductRequestDTO;
import com.ecommerce.products_service.dto.ProductResponseDTO;
import com.ecommerce.products_service.exception.ResourceNotFoundException;
import com.ecommerce.products_service.mapper.ProductMapper;
import com.ecommerce.products_service.model.Product;
import com.ecommerce.products_service.repository.ProductRepository;
import com.ecommerce.products_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {

        Product product = mapper.toProduct(requestDTO);
        Product savedProduct = repository.save(product);
        log.info("Created product {} with id {}", savedProduct.getName(), savedProduct.getId());
        return mapper.toResponseDTO(savedProduct);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(String id) {
        Product product = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id)
        );
        return mapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO updateProductById(String id, ProductRequestDTO productRequestDTO) {
        Product product = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id)
        );
        mapper.updateProductFromRequest(productRequestDTO, product);
        Product updatedProduct = repository.save(product);
        log.info("Updated product {} with id {}", updatedProduct.getName(), updatedProduct.getId());
        return mapper.toResponseDTO(updatedProduct);
    }

    @Override
    public void deleteProductById(String id) {
        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        };
        repository.deleteById(id);
        log.info("Deleted product with id {}", id);
    }
}
