package edu.finalyearproject.imsresourceserver.controllers;


import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class ProductController
{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Logger log = LoggerFactory.getLogger(ProductController.class);

    /**
     * Returns all product records in the database
     * @return List<Product> - List of Product objects
     */
    @GetMapping("/products")
    public List<Product> getProducts()
    {
        log.info("Retrieving all products from database..");
        List<Product> all = productRepository.findAll();

        return all;
    }

    /**
     * GET method to return the names of all products in the database.
     * @return List<String> - List of product names.
     */
    @GetMapping("/products/all-names")
    public List<String> getProductNames()
    {
        List<Product> products = productRepository.findAll();
        return products.stream().map(Product::getName).collect(Collectors.toList());
    }

    /**
     * Returns product details based on a provided name value.
     * @param name - The name of the product.
     * @return Product - Product POJO relating to database record.
     */
    @GetMapping("/products/name/{name}")
    public Product getProductByName(@PathVariable String name)
    {
        return productRepository.findByname(name);
    }

    /**
     * Returns product details based on a provided sku value.
     * @param sku - Integer representing the sku of a product
     * @return Product - Product POJO relating to database record
     */
    @GetMapping("/products/sku/{sku}")
    public Product getProductBySku(@PathVariable Integer sku)
    {
        return productRepository.findBysku(sku);
    }

    /**
     * Returns all products for a given supplier.
     * @param name - the name of the supplier to find the products of
     * @return List<Product> - List of all products provided by the supplier.
     */
    @GetMapping("/products/supplier/{name}")
    public List<Product> getProductsForSupplier(@PathVariable String name)
    {
        Supplier supplier = supplierRepository.findByname(name);
        return productRepository.findBysupplier(supplier);
    }

    /**
     * Creates a new Product record and adds it to the database.
     * @param product - The product details to add to the database.
     */
    @PostMapping("/products/{name}")
    public void addProduct(@RequestBody Product product)
    {
        productRepository.save(product);
    }

    @PostMapping("/products/sku/{id}")
    public void removeProduct(@PathVariable Integer id)
    {
        productRepository.deleteById(id);
    }
}
