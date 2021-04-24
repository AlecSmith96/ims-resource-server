/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.PurchaseRepository;
import edu.finalyearproject.imsresourceserver.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for all requests relating to Supplier records.
 */
@RestController
public class SupplierController
{
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * GET method for returning all Supplier orders in the database.
     * @return List<Supplier> - List of all Supplier orders.
     */
    @GetMapping("/suppliers/all")
    public List<Supplier> getSuppliers()
    {
        return supplierRepository.findAll();
    }

    /**
     * Get method for returning the names of all Suppliers in the database.
     * @return List<String> - List of all supplier names
     */
    @GetMapping("/suppliers/all-names")
    public List<String> getAllSupplierNames()
    {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream().map(Supplier::getName).collect(Collectors.toList());
    }

    /**
     * GET method for returning a Supplier order with the corresponding id.
     * @param id - the id of the Supplier record.
     * @return Supplier - the Supplier object.
     */
    @GetMapping("/supplier/{id}")
    public Supplier getSupplier(@PathVariable int id)
    {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if (supplier.isPresent())
        {
            return supplier.get();
        }

        return new Supplier();
    }

    /**
     * GET method to return all purchase orders for supplier with corresponding id.
     * @param id - the id of the Supplier record.
     * @return List<Purchase> - List of Purchase orders for the supplier.
     */
    @GetMapping("/supplier/orders/{id}")
    public List<Purchase> getPurchaseOrdersForSupplier(@PathVariable int id)
    {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if (supplier.isPresent())
        {
            return purchaseRepository.findBysupplier(supplier.get());
        }

        return new ArrayList<>();
    }

    /**
     * GET method to return the Supplier for a specific product.
     * @param id - the id of the Product.
     * @return Supplier - the Supplier for the product.
     */
    @GetMapping("/supplier/product/{id}")
    public Supplier getSupplierForProduct(@PathVariable int id)
    {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent())
        {
            return product.get().getSupplier();
        }

        return new Supplier();
    }

    /**
     * GET method for returning all products for a given supplier.
     * @param id - the id of the supplier.
     * @return List<Product> - all products for this supplier.
     */
    @GetMapping("/supplier/products/{id}")
    public List<Product> getProductsForSupplier(@PathVariable int id)
    {
        Optional<Supplier> byId = supplierRepository.findById(id);
        if (byId.isPresent())
        {
            List<Product> products = productRepository.findBysupplier(byId.get());
            return products;
        }

        return new ArrayList<>();
    }
}
