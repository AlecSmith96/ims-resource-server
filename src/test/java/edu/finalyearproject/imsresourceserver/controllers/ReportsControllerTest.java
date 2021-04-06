package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Customer;
import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.reports.ReportBuilder;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.PurchaseRepository;
import edu.finalyearproject.imsresourceserver.services.EmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportsControllerTest
{
    private static final List<Order> ORDERS = getOrders();

    @InjectMocks
    private ReportsController target;

    @Mock
    private ReportBuilder reportBuilder;

    @Mock
    private EmailService emailService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    private Fixture fixture;

    @Test
    public void generateOrderSummary_reportIsCorrect()
    {
        fixture.givenManagerEmailIsSet();
        fixture.givenOrderRepositoryReturnsOrders();
    }

    private static List<Order> getOrders()
    {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, new Customer(), Date.valueOf("2020-02-02"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        orders.add(new Order(2, new Customer(), Date.valueOf("2020-02-02"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        orders.add(new Order(3, new Customer(), Date.valueOf("2020-02-02"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        orders.add(new Order(4, new Customer(), Date.valueOf("2020-02-02"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        return orders;
    }

    private class Fixture
    {
        void givenManagerEmailIsSet()
        {
            ReflectionTestUtils.setField(target, "managerEmail", "rcsmith.alec@gmail.com");
        }

        void givenOrderRepositoryReturnsOrders()
        {

        }
    }
}
