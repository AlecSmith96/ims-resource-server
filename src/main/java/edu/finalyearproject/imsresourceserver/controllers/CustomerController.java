/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Customer;
import edu.finalyearproject.imsresourceserver.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest Controller for all requests relating to Customer records.
 */
@RestController
public class CustomerController
{
    @Autowired
    private CustomerRepository customerRepository;

    private Logger log = LoggerFactory.getLogger(CustomerController.class);

    /**
     * GET method for returning all customer records.
     * @return List<Customer> - List of Customer objects.
     */
    @GetMapping("/customers/all")
    public List<Customer> getCustomers()
    {
        return customerRepository.findAll();
    }

    /**
     * GET method for returning the Customer record relating to passed id parameter.
     * @param id - the id of the Customer requested.
     * @return Customer - the Customer object.
     */
    @GetMapping("/customers/{id}")
    public Customer getCustomer(@PathVariable Integer id)
    {
        log.info("Fetching customer with id: "+id);
        Customer customer = customerRepository.findByid(id);

        return customer;
    }
}
