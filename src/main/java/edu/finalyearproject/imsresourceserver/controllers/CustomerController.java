package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Customer;
import edu.finalyearproject.imsresourceserver.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController
{
    @Autowired
    private CustomerRepository customerRepository;

    private Logger log = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping("/customers/all")
    public List<Customer> getCustomers()
    {
        return customerRepository.findAll();
    }
}
