package com.maxi.analyser.service;

import com.maxi.analyser.entity.Product;
import com.maxi.analyser.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product saveProduct(Product product) {
        return repo.save(product);
    }
    public Optional<Product> getProductById(Long id) {
        return repo.findById(id);
    }

}
