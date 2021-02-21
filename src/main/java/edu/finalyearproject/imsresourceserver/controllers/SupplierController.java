/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SupplierController
{
    @Autowired
    private SupplierRepository supplierRepository;

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
}
