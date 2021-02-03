package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class OrderController
{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Logger log = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/orders/all")
    public List<Order> getOrders()
    {
        return orderRepository.findAll();
    }

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

    @GetMapping("/customer/{id}")
    public List<Order> getOrdersForCustomer(int customer_id)
    {
        return orderRepository.findBycustomer(customer_id);
    }
}


