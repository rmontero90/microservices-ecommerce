package com.ecommerce.products_service.dataloader;

import com.ecommerce.products_service.model.Product;
import com.ecommerce.products_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestDataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {

//        Product product = Product.builder().
//                name("Honor Magic 8 Pro")
//                .description("Smartphone con AI")
//                .price(BigDecimal.valueOf(1200))
//                .build();
//
//        productRepository.save(product);
    }
}
