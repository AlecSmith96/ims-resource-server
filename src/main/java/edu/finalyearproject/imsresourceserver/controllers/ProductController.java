package edu.finalyearproject.imsresourceserver.controllers;


import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController

public class ProductController
{
    @Autowired
    private ProductRepository productRepository;

    private Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/products")
    public List<Product> getProducts()
    {
        log.info("Entered getProducts method");
        List<Product> all = productRepository.findAll();

        return all;
    }

    @GetMapping("/products/{name}")
    public Product getProduct(@PathVariable String name)
    {
        return productRepository.findByName(name);
    }
}
