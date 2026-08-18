package com.ecommerce.products_service.mapper;

import com.ecommerce.products_service.dto.ProductRequestDTO;
import com.ecommerce.products_service.dto.ProductResponseDTO;
import com.ecommerce.products_service.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel =  "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductRequestDTO requestDTO);

    ProductResponseDTO toResponseDTO(Product product);

    @Mapping(target = "id", ignore = true)
    void updateProductFromRequest(ProductRequestDTO productRequestDTO, @MappingTarget Product product);
}
