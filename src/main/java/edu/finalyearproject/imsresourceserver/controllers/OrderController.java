/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.*;
import edu.finalyearproject.imsresourceserver.repositories.CustomerRepository;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.requests.OrderRequest;
import edu.finalyearproject.imsresourceserver.requests.ProductRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OrderController
{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Logger log = LoggerFactory.getLogger(OrderController.class);

    /**
     * GET method for returning all orders in the order repository.
     * @return List<Order> - List containing POJOs of all order records.
     */
    @GetMapping("/orders/all")
    public List<Order> getOrders()
    {
        log.info("Retrieving all orders from database...");
        List<Order> all = orderRepository.findAll();
        Collections.sort(all, Collections.reverseOrder());
        return all;
    }

    /**
     * POST method for setting an orders arrival date to todays date.
     * @param id - the id(primary key) of the order to set to delivered.
     * @return void
     */
    @PostMapping("/order/delivered/{id}")
    public void setOrderToDelivered(@PathVariable int id)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(dtf.format(now));

        Order order = orderRepository.findByid(id);
        order.setArrival_date(date);
        orderRepository.save(order);
    }

    // NOT USED CURRENTLY //////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Method that returns all orders in format that can be used in the table of the 'Customer Orders' page.
     * @return List<OrderPageOrders> - All orders in correct format for order table
     */
    @GetMapping("/orders/homepage")
    public List<OrderPageOrder> getOrdersForHomepage()
    {
        List<Order> all = orderRepository.findAll();
        List<OrderPageOrder> ordersForCustomerOrdersPage = new ArrayList<>();
        for (Order order : all)
        {
            OrderPageOrder newOrder = formatOrder(order);
            ordersForCustomerOrdersPage.add(newOrder);
        }

        return ordersForCustomerOrdersPage;
    }

    private OrderPageOrder formatOrder(Order order)
    {
        OrderPageOrder newOrder = new OrderPageOrder();
        newOrder.setId(order.getId().toString());
        newOrder.setCustomer(order.getCustomer().getTitle()+" "+
                order.getCustomer().getFirst_name()+" "+
                order.getCustomer().getLast_name());
        newOrder.setOrder_date(order.getOrder_date().toString());
        newOrder.setArrival_date(order.getArrival_date() == null ? "null" : order.getArrival_date().toString());

        return newOrder;
    }

    /**
     * GET method for returning all orders containing a specific product.
     * @param product_id - the id(primary key) of the product.
     * @return List<Order> - List of Orders that contain that product.
     * @throws Exception - If the product isn't found in the product repository.
     */
    @GetMapping("/orders/product/{product_id}")
    public List<Order> getOrdersForProduct(@PathVariable int product_id) throws Exception
    {
        log.info("Searching for orders by product...");
        Optional<Product> product = productRepository.findById(product_id);
        if (product.isPresent())
        {
            return orderRepository.findByproducts(product.get());
        }
        throw new Exception("Unrecognised Product");
    }

    @GetMapping("/customer/{customer_id}")
    public List<Order> getOrdersForCustomer(@PathVariable  int customer_id)
    {
        Optional<Customer> customer = customerRepository.findById(customer_id);
        List<Order> allOrders = orderRepository.findAll();
        if (customer.isPresent())
        {
            return allOrders.stream().filter(order -> order.getCustomer().equals(customer.get()))
                                                                         .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @PostMapping("/order/create")
    public void createNewOrder(@RequestBody OrderRequest orderRequest)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(dtf.format(now));
        Optional<Customer> customer = customerRepository.findById(orderRequest.getCustomer_id());
        Set<Product> products = new HashSet<>();
        for (ProductRequest productRequest : orderRequest.getProducts())
        {
            Product product = productRepository.findBysku(Integer.valueOf(productRequest.getSku()));
            for (int i = 0 ; i < productRequest.getQuantity() ; i++)
            {
                products.add(product);
            }
        }


        Order order = new Order();
        order.setCustomer(customer.get());
        order.setProducts(products);
        order.setOrder_date(date);

        // UPDATE product quantities

        orderRepository.save(order);
    }
}


