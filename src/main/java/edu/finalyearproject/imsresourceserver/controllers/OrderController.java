package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.OrderPageOrder;
import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        log.info("Retrieving all orders from database...");
        List<Order> all = orderRepository.findAll();
        return all;
    }

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


