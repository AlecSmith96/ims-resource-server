package edu.finalyearproject.imsresourceserver.controllers;

import com.lowagie.text.DocumentException;
import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.reports.ReportBuilder;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

    @GetMapping("/reports/order-summary")
    public String generateOrderSummary() throws IOException, DocumentException
    {
        log.info("Generating Order Summary Report...");
        List<Order> orders = orderRepository.findAll();
        String html = reportBuilder.withContext()
                            .withOrdersList("orders", orders)
                            .buildReport("order-summary");

        emailService.sendEmailWithAttachment("rcsmith.alec@gmail.com", "order summary", "Test email", html, "order-summary");

        return html;
    }
}
