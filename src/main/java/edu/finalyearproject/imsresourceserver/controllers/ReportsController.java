/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.controllers;

import com.lowagie.text.DocumentException;
import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import edu.finalyearproject.imsresourceserver.reports.ReportBuilder;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ReportsController
{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReportBuilder reportBuilder;

    private Logger log = LoggerFactory.getLogger(PurchaseController.class);

    // NEEDS TO INCLUDE DATE FROM AND DATE TO SO ONLY ORDERS FROM WITHIN THOSE DATES ARE RETURNED
    @GetMapping("/reports/order-summary")
    public String generateOrderSummary() throws IOException, DocumentException
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();

        log.info("Generating Order Summary Report...");
        List<Order> orders = orderRepository.findAll();
        String html = reportBuilder.withContext()
                            .withOrdersList("orders", orders)
                            .withString("todaysDate", dtf.format(now))
                            .buildReport("order-summary");

        emailService.sendEmailWithAttachment("rcsmith.alec@gmail.com", "order summary", "Here is your Order Summary Report for "+dtf.format(now), html, "order-summary", dtf.format(now));

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
        return reportBuilder.withContext()
                .withProductList("products", purchase.getProducts())
                .withString("id", purchase.getId().toString())
                .withString("supplier", purchase.getSupplier().getName())
                .withString("purchase_date", purchase.getPurchase_date())
                .withString("status", status)
                .withString("arrival_date", purchase.getArrival_date())
                .buildReport("supplier-invoice");
    }
}
