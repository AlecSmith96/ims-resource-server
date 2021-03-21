/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.controllers;

import com.lowagie.text.DocumentException;
import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import edu.finalyearproject.imsresourceserver.reports.ReportBuilder;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.repositories.PurchaseRepository;
import edu.finalyearproject.imsresourceserver.requests.StockMovement;
import edu.finalyearproject.imsresourceserver.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReportsController
{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReportBuilder reportBuilder;

    @Value("${manager.email}")
    private String managerEmail;

    private Logger log = LoggerFactory.getLogger(PurchaseController.class);

    /**
     * GET method for generating an order summary report containing customer orders within a given time period.
     * @param startDate - the earliest date an order could have been made on.
     * @param endDate - the latest date an order could have been made on.
     * @return String - HTML report.
     */
    @GetMapping("/reports/order-summary/{startDate}/{endDate}")
    public String generateOrderSummary(@PathVariable String startDate, @PathVariable String endDate)
    {
        log.info("Generating Order Summary Report for orders within " + startDate +" and "+endDate);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<Order> orders = getCustomerOrdersInRange(Date.valueOf(startDate),  Date.valueOf(endDate));
        Collections.sort(orders);

        String html = reportBuilder.withContext()
                            .withOrdersList("orders", orders)
                            .withString("todaysDate", dtf.format(now))
                            .withString("startDate", dateFormatter.format(Date.valueOf(startDate)))
                            .withString("endDate", dateFormatter.format(Date.valueOf(endDate)))
                            .buildReport("order-summary");

        emailService.sendEmailWithAttachment(managerEmail, "order summary", "Here is your Order Summary Report for "+dtf.format(now), html, "order-summary", dtf.format(now));

        return html;
    }

    /**
     * GET method for generating a purchase order summary report containing supplier orders within a given time period.
     * @param startDate - the earliest date an order could have been made on.
     * @param endDate - the latest date an order could have been made on.
     * @return String - HTML report.
     */
    @GetMapping("/reports/purchase-summary/{startDate}/{endDate}")
    public String generatePurchaseSummary(@PathVariable String startDate, @PathVariable String endDate)
    {
        log.info("Generating Purchase Summary Report for supplier orders within " + startDate + " and "+endDate);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<Purchase> supplierOrders = getSupplierOrdersInRange(Date.valueOf(startDate), Date.valueOf(endDate));
        Collections.sort(supplierOrders);

        String html = reportBuilder.withContext()
                .withPurchasesList("purchases", supplierOrders)
                .withString("todaysDate", dtf.format(now))
                .withString("startDate", dateFormatter.format(Date.valueOf(startDate)))
                .withString("endDate", dateFormatter.format(Date.valueOf(endDate)))
                .buildReport("purchase-summary");

        emailService.sendEmailWithAttachment(managerEmail, "purchase order summary", "Here is your Purchase Order Summary Report for "+dtf.format(now), html, "order-summary", dtf.format(now));

        return html;
    }

    @GetMapping("/reports/order-invoice/{id}")
    public String generateOrderInvoice(@PathVariable int id)
    {
        log.info("Generating invoice for order #"+id);
        Order order = orderRepository.findByid(id);
        String status = order.getArrival_date().equals("null") ? "PENDING" : "DELIVERED";

        String html = reportBuilder.withContext()
                            .withString("orderNumber", String.valueOf(id))
                            .withString("order_date", order.getOrder_date())
                            .withString("status", status)
                            .withString("houseNum", String.valueOf(order.getCustomer().getAddress().getHouse_number()))
                            .withString("line1", order.getCustomer().getAddress().getLine_1())
                            .withString("city", order.getCustomer().getAddress().getCity())
                            .withString("county", order.getCustomer().getAddress().getCounty())
                            .withString("postCode", order.getCustomer().getAddress().getPost_code())
                            .withProductList("products", order.getProducts())
                            .withString("totalCost", order.getTotalCost())
                            .buildReport("order-invoice");

        emailService.sendEmailWithAttachment(order.getCustomer().getEmail(), "Order Confirmation", "Here is your invoice for your order on  "+order.getOrder_date(), html, "order-invoice", order.getOrder_date());

        return html;
    }

    @GetMapping("/reports/supplier-invoice")
    public String generatePurchaseInvoice(@RequestBody Purchase purchase)
    {
        String status = purchase.getArrival_date().equals("null") ? "PENDING" : "DELIVERED";
        String html = reportBuilder.withContext()
                .withProductList("products", purchase.getProducts())
                .withString("id", purchase.getId().toString())
                .withString("supplier", purchase.getSupplier().getName())
                .withString("purchase_date", purchase.getPurchase_date())
                .withString("status", status)
                .withString("arrival_date", purchase.getArrival_date())
                .buildReport("supplier-invoice");

        emailService.sendEmailWithAttachment(managerEmail, "Purchase Order #"+purchase.getId()+" Confirmation",
                "Here is your invoice for your Supplier Purchase Order on  "+purchase.getPurchase_date(), html,
                "purchase-order-"+purchase.getId(), purchase.getPurchase_date());

        return html;
    }

    /**
     * GET method for generating a stock movement report containing movements for customer and supplier orders within a
     * given time period.
     * @param startDate - the earliest date a stock movement can be on.
     * @param endDate - the latest date a stock movement can be on.
     * @return String - HTML report.
     */
    @GetMapping("/reports/stock-movement/{startDate}/{endDate}")
    public String generateStockMovementReport(@PathVariable String startDate, @PathVariable String endDate)
    {
        log.info("Generating Stock Movement Report for dates "+startDate+" to "+endDate);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Date startDateObject = Date.valueOf(startDate);
        Date endDateObject = Date.valueOf(endDate);
        String startDateString = dateFormatter.format(startDateObject);
        String endDateString = dateFormatter.format(endDateObject);
        String now = dateFormatter.format(Date.from(Instant.now()));

        // get all stock movements in range
        List<Order> customerOrders = getCustomerOrdersInRange(startDateObject,  endDateObject);
        List<Purchase> supplierOrders = getSupplierOrdersInRange(startDateObject, endDateObject);
        List<StockMovement> stockMovements = getAllStockMovements(customerOrders, supplierOrders);
        Collections.sort(stockMovements);       // sort by date of movement

        String html = reportBuilder.withContext()
                .withString("startDate", dateFormatter.format(startDateObject))
                .withString("endDate", dateFormatter.format(endDateObject))
                .withStockMovementList("stockMovements", stockMovements)
                .buildReport("stock-movement");

        emailService.sendEmailWithAttachment(managerEmail, "Stock Movement Report "+ startDateString +" to "+ endDateString,
                "Here is your Stock Movement Report for your requested time period.", html,
                "stock-movements-"+startDateString + "_"+endDateString, now);

        return html;
    }

    // parse string representation of date into Date object for comparison
    private Date createDateObject(String stringDate)
    {
        String[] array = stringDate.split("-");
        return Date.valueOf(array[2]+"-"+array[1]+"-"+array[0]);
    }

    // Returns list of customer orders within given time frame
    private List<Order> getCustomerOrdersInRange(Date startDate, Date endDate)
    {
        List<Order> customerOrders = orderRepository.findAll();

        return customerOrders.stream().filter(order -> {
            Date orderDate = createDateObject(order.getOrder_date());
            return orderDate.after(startDate) && orderDate.before(endDate);
        }).collect(Collectors.toList());
    }

    // Returns list of supplier orders within given time frame
    private List<Purchase> getSupplierOrdersInRange(Date startDate, Date endDate)
    {
        List<Purchase> purchaseOrders = purchaseRepository.findAll();

        return purchaseOrders.stream().filter(order -> {
            Date orderDate = createDateObject(order.getPurchase_date());
            return orderDate.after(startDate) && orderDate.before(endDate);
        }).collect(Collectors.toList());
    }

    // Returns all Stock movements for all orders
    private List<StockMovement> getAllStockMovements(List<Order> orders, List<Purchase> purchases)
    {
        List<StockMovement> stockMovements = new ArrayList<>();
        getStockMovementsForCustomerOrders(orders, stockMovements);
        getStockMovementsForSupplierOrders(purchases, stockMovements);

        return stockMovements;
    }

    // adds stock movements for supplier orders to list
    private void getStockMovementsForSupplierOrders(List<Purchase> purchases, List<StockMovement> stockMovements)
    {
        for (Purchase purchase : purchases)
        {
            for (Product product : purchase.getProducts())
            {
                StockMovement stockMovement = new StockMovement(purchase.getId(), true,
                        createDateObject(purchase.getPurchase_date()), purchase.getSupplier().getName(), product,
                        product.getReorder_quantity());
                stockMovements.add(stockMovement);
            }
        }
    }

    // adds stock movements for customer orders to list
    private void getStockMovementsForCustomerOrders(List<Order> orders, List<StockMovement> stockMovements)
    {
        for (Order order : orders)
        {
            for (Product product : order.getProducts())
            {
                StockMovement stockMovement = new StockMovement(order.getId(), false, createDateObject(order.getOrder_date()),
                        order.getCustomer().getTitle()+" "+order.getCustomer().getFirst_name()+ " "+
                                order.getCustomer().getLast_name(), product, 1);
                stockMovements.add(stockMovement);
            }
        }
    }
}
