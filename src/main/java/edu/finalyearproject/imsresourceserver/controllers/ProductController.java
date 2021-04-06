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
import edu.finalyearproject.imsresourceserver.requests.ProductAmountRequest;
import edu.finalyearproject.imsresourceserver.requests.ProductThresholdRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for all requests relating to Product records.
 */
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
     * @param name - the name of the supplier to find the products of.
     * @return List<Product> - List of all products provided by the supplier.
     */
    @GetMapping("/products/supplier/{name}")
    public List<Product> getProductsForSupplier(@PathVariable String name)
    {
        Supplier supplier = supplierRepository.findByname(name);
        return productRepository.findBysupplier(supplier);
    }

    /**
     * POST method to remove a product record.
     * @param id - the id of the Product.
     */
    @PostMapping("/products/sku/{id}")
    public void removeProduct(@PathVariable Integer id)
    {
        productRepository.deleteById(id);
    }

    /**
     * GET method to retrieve a Product record.
     * @param id - the id of the Product.
     * @return Product - The Product object.
     */
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

    /**
     * POST method to add Product record to the database.
     * @param productRequest - Object containing products details.
     * @return - the new Product object.
     */
    @PostMapping("/product/add")
    public Product addProduct(@RequestBody NewProductRequest productRequest)
    {
        log.info("Adding new product to database: "+ productRequest.getName());
        Supplier supplier = supplierRepository.findByname(productRequest.getSupplierName());
        Product newProduct = new Product(productRequest.getName(), Integer.valueOf(productRequest.getSku()), productRequest.getPrice(),
                productRequest.getInventoryOnHand(), productRequest.getReorderThreshold(),
                productRequest.getReorderQuantity(), supplier);
        productRepository.save(newProduct);

        return newProduct;
    }

    /**
     * POST method to set a product record as suspended.
     * @param id - the id of the Product.
     * @return Product - the Product that has been suspended.
     */
    @PostMapping("/product/suspend/{id}")
    public Product suspendProduct(@PathVariable Integer id)
    {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent())
        {
            Product product = productOptional.get();
            product.setSuspended(true);
            productRepository.save(product);
            return product;
        }

        return new Product();
    }

    /**
     * POST method to set a product record as not suspended.
     * @param id - the id of the Product.
     * @return Product - the Product that has been reinstated.
     */
    @PostMapping("/product/reinstate/{id}")
    public Product reinstateProduct(@PathVariable Integer id)
    {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent())
        {
            Product product = productOptional.get();
            product.setSuspended(false);
            productRepository.save(product);
            return product;
        }

        return new Product();
    }

    /**
     * Get method to return any Products that have less than or equal to 20 stock left before hitting reorder point.
     * @return List<Product> - List of Products low on stock.
     */
    @GetMapping("/products/low-stock")
    public List<Product> getProductsLowOnStock()
    {
        log.info("Retrieving products low on stock...");
        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream().filter(product -> product.getInventory_on_hand() <=
                                                    (product.getReorder_threshold() +20)).collect(Collectors.toList());
    }

    /**
     * POST method to update the reorder_threshold for a Product.
     * @param id - the id of the product.
     * @param request - wrapper class containing new threshold.
     * @return Product - the Product with the updated threshold.
     */
    @PostMapping("/product/update/reorder-threshold/{id}")
    public Product updateReorderThreshold(@PathVariable int id, @RequestBody ProductThresholdRequest request)
    {
        log.info("Updating reorder threshold for product "+id+"...");
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent())
        {
            Product theProduct = product.get();
            theProduct.setReorder_threshold(request.getNewThreshold());
            productRepository.save(theProduct);
            return theProduct;
        }

        return new Product();
    }

    /**
     * POST method for updating the reorder_amount for a Product.
     * @param id - the id of the Product.
     * @param request - wrapper class containing new amount.
     * @return Product - the Product with the updated reorder_amount.
     */
    @PostMapping("/product/update/reorder-amount/{id}")
    public Product updateReorderAmount(@PathVariable int id, @RequestBody ProductAmountRequest request)
    {
        log.info("Updating reorder amount for product "+id+"...");
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent())
        {
            Product theProduct = product.get();
            theProduct.setReorder_quantity(request.getNewAmount());
            productRepository.save(theProduct);
            return theProduct;
        }

        return new Product();
    }

    /**
     * GET method for calculating and returning the average daily sales for a Product.
     * @param id - the id of the Product.
     * @return float - the average daily sales for the product.
     */
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

    // add together total sales for orders in range
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

    // calculate the date two weeks before today's date
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
