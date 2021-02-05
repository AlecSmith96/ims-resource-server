package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.reports.ReportBuilder;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportsController
{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReportBuilder reportBuilder;

    @GetMapping("/reports/order-summary")
    public String generateOrderSummary()
    {
        List<Order> orders = orderRepository.findAll();
        return reportBuilder.withContext()
                            .withOrdersList("orders", orders)
                            .buildReport("order-summary");
    }
}
