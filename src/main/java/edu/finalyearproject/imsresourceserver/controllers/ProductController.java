/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.SupplierRepository;
import edu.finalyearproject.imsresourceserver.requests.NewProductRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@RestController
public class ProductController
{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Integer id)
    {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent())
        {
            return  productOptional.get();
        }

        return new Product();
    }

    @PostMapping("/product/add")
    public void addProduct(@RequestBody NewProductRequest productRequest)
    {
        log.info("Adding new product to database: "+ productRequest.getName());
        Supplier supplier = supplierRepository.findByname(productRequest.getSupplierName());
        Product newProduct = new Product(productRequest.getName(), Integer.valueOf(productRequest.getSku()), productRequest.getPrice(),
                productRequest.getInventoryOnHand(), productRequest.getReorderThreshold(),
                productRequest.getReorderQuantity(), supplier);
        productRepository.save(newProduct);
    }

    @PostMapping("/product/suspend/{id}")
    public void suspendProduct(@PathVariable Integer id)
    {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent())
        {
            Product product = productOptional.get();
            product.setSuspended(true);
            productRepository.save(product);
        }
    }

    @PostMapping("/product/reinstate/{id}")
    public void reinstateProduct(@PathVariable Integer id)
    {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent())
        {
            Product product = productOptional.get();
            product.setSuspended(false);
            productRepository.save(product);
        }
    }

    @GetMapping("/product/adu/{id}")
    public float getAverageDailySales(@PathVariable int id)
    {
        log.info("Calculating ADU for product "+id+"...");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        Date today = Date.valueOf(dtf.format(now));
        Date twoWeeksAgo = getDateTwoWeeksBefore(today);
        List<Order> ordersInRange = getCustomerOrdersInRange(twoWeeksAgo, today);
        int totalSales = getTotalSales(id, ordersInRange);

        // if there is a remainder, +1 to value to account for integer division
        float ADU = (totalSales / 14) % 14 == 1 ? totalSales / 14 +1 : totalSales / 14;

        return (float) (totalSales / 14.0);
    }

    private int getTotalSales(@PathVariable int id, List<Order> ordersInRange)
    {
        int totalSales = 0;
        for (Order order : ordersInRange)
        {
            for (Product product : order.getProducts())
            {
                if (product.getId() == id)
                {
                    totalSales++;
                }
            }
        }
        return totalSales;
    }

    private Date getDateTwoWeeksBefore(Date today)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, -14); //2 weeks

        return new Date(calendar.getTime().getTime());
    }

    // Returns list of customer orders within given time frame
    private List<Order> getCustomerOrdersInRange(Date startDate, Date endDate)
    {
        List<Order> customerOrders = orderRepository.findAll();
        List<Order> ordersInRange = new ArrayList<>();
        for (Order order : customerOrders)
        {
            Date date = createDateObject(order.getOrder_date());
            if (date.after(startDate) && date.before(endDate))
            {
                ordersInRange.add(order);
            }
            if (date.equals(startDate) || date.equals(endDate))
            {
                ordersInRange.add(order);
            }
        }

        return ordersInRange;
    }

    // parse string representation of date into Date object for comparison
    private java.sql.Date createDateObject(String stringDate)
    {
        String[] array = stringDate.split("-");
        return java.sql.Date.valueOf(array[2]+"-"+array[1]+"-"+array[0]);
    }
}
