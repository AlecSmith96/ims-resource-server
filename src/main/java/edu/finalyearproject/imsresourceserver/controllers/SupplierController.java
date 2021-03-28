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

@RestController
public class SupplierController
{
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

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
     * Get method to return all supplier purchase orders for supplier with corresponding id.
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
}
