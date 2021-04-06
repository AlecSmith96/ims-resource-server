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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest
{
    private static final Order ORDER = new Order(1, new Customer(), Date.valueOf("2020-02-02"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0);
    private static final Product PRODUCT = new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier());
    private static final Product PRODUCT2 = new Product(2, "product2", 22222222, (float) 2.0, 10, 5, 10,  false,  new Supplier());
    private static final Product PRODUCT3 = new Product(3, "product3", 33333333, (float) 2.0, 10, 5, 10,  false,  new Supplier());
    private static final Customer CUSTOMER = new Customer(1, "Mr", "John", null, "Doe", "john@email.com", "01234567890", new Address(), Set.of(ORDER));

    @InjectMocks
    private OrderController target;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    private List<Order> orders = getOrders();

    private List<Order> getOrdersResult;
    private Order setOrderToDeliveredResult;
    private List<Order> getOrdersForProductResult;
    private List<Order> getOrdersForCustomerResult;
    private Order createNewOrderResult;

    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void getOrders_returnsSortedOrders()
    {
        fixture.givenOrderRepositoryReturnsOrders();
        fixture.whenGetOrdersIsCalled();
        fixture.thenAssertOrdersAreSortedCorrectly();
    }

    @Test
    public void setOrderToDelivered_returnsDeliveredOrder()
    {
        fixture.givenOrderRepositoryReturnsAnOrder();
        fixture.whenSetOrderToDeliveredIsCalled();
        fixture.thenAssertOrderIsDelivered();
    }

    @Test
    public void getOrdersForProduct_returnsProducts()
    {
        fixture.givenProductRepositoryReturnOptionalWithProduct();
        fixture.givenOrderRepositoryReturnsOrdersForProduct();
        fixture.whenGetOrdersForProductIsCalled();
        fixture.thenAssertOrdersAreReturned();
    }

    @Test
    public void getOrdersForProduct_returnsEmptyArrayList()
    {
        fixture.givenProductRepositoryReturnsEmptyOptional();
        fixture.whenGetOrdersForProductIsCalled();
        fixture.thenAssertEmptyArrayListIsReturend(getOrdersForProductResult);
    }

    @Test
    public void getOrdersForCustomer_returnsOrders()
    {
        fixture.givenCustomerRepositoryReturnsOptionalWithCustomer();
        fixture.givenOrderRepositoryReturnsOrders();
        fixture.whenGetOrdersForCustomerIsCalled();
        fixture.thenAssertCorrectOrdersAreReturned();
    }

    @Test
    public void getOrdersForCustomer_returnsEmptyArrayList()
    {
        fixture.givenCustomerRepositoryReturnsEmptyOptional();
        fixture.whenGetOrdersForCustomerIsCalled();
        fixture.thenAssertEmptyArrayListIsReturend(getOrdersForCustomerResult);
    }

    @Test
    public void createNewOrder_orderCreatedCorrectly()
    {
        fixture.givenCustomerRepositoryReturnsOptionalWithCustomer();
        fixture.givenProductRepositoryReturnsProduct();
        fixture.whenCreateNewOrderIsCalled();
        fixture.thenAssertOrderIsCorrect();
    }

    private List<Order> getOrders()
    {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, CUSTOMER, Date.valueOf("2020-02-02"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        orders.add(new Order(2, new Customer(), Date.valueOf("2020-03-18"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        orders.add(new Order(3, CUSTOMER, Date.valueOf("2020-03-20"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        orders.add(new Order(4, CUSTOMER, Date.valueOf("2020-02-16"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        orders.add(new Order(5, new Customer(), Date.valueOf("2020-02-08"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0));
        return orders;
    }

    private class Fixture
    {
        void givenOrderRepositoryReturnsOrders()
        {
            when(orderRepository.findAll()).thenReturn(orders);
        }

        void givenOrderRepositoryReturnsAnOrder()
        {
            when(orderRepository.findByid(1)).thenReturn(ORDER);
        }

        void givenProductRepositoryReturnOptionalWithProduct()
        {
            when(productRepository.findById(1)).thenReturn(Optional.of(PRODUCT));
        }

        void givenOrderRepositoryReturnsOrdersForProduct()
        {
            when(orderRepository.findByproducts(PRODUCT)).thenReturn(orders);
        }

        void givenProductRepositoryReturnsEmptyOptional()
        {
            when(productRepository.findById(1)).thenReturn(Optional.empty());
        }

        void givenCustomerRepositoryReturnsOptionalWithCustomer()
        {
            when(customerRepository.findById(1)).thenReturn(Optional.of(CUSTOMER));
        }

        void givenCustomerRepositoryReturnsEmptyOptional()
        {
            when(customerRepository.findById(1)).thenReturn(Optional.empty());
        }

        void givenProductRepositoryReturnsProduct()
        {
            when(productRepository.findBysku(11111111)).thenReturn(PRODUCT);
            when(productRepository.findBysku(22222222)).thenReturn(PRODUCT2);
            when(productRepository.findBysku(33333333)).thenReturn(PRODUCT3);
        }

        void whenGetOrdersIsCalled()
        {
            getOrdersResult = target.getOrders();
        }

        void whenSetOrderToDeliveredIsCalled()
        {
            setOrderToDeliveredResult = target.setOrderToDelivered(1);
        }

        void whenGetOrdersForProductIsCalled()
        {
            getOrdersForProductResult = target.getOrdersForProduct(1);
        }

        void whenGetOrdersForCustomerIsCalled()
        {
            getOrdersForCustomerResult = target.getOrdersForCustomer(1);
        }

        void whenCreateNewOrderIsCalled()
        {
            ProductRequest productRequest1 = new ProductRequest("11111111", 1);
            ProductRequest productRequest2 = new ProductRequest("22222222", 1);
            ProductRequest productRequest3 = new ProductRequest("33333333", 1);
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setCustomer_id(1);
            orderRequest.setProducts(new ProductRequest[]{productRequest1, productRequest2, productRequest3});

            createNewOrderResult = target.createNewOrder(orderRequest);
        }

        void thenAssertOrdersAreSortedCorrectly()
        {
            assertEquals(5, getOrdersResult.size());
            assertEquals(new Order(3, CUSTOMER, Date.valueOf("2020-03-20"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0), getOrdersResult.get(0));
            assertEquals(new Order(2, new Customer(), Date.valueOf("2020-03-18"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0), getOrdersResult.get(1));
            assertEquals(new Order(4, CUSTOMER, Date.valueOf("2020-02-16"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0), getOrdersResult.get(2));
            assertEquals(new Order(5, new Customer(), Date.valueOf("2020-02-08"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0), getOrdersResult.get(3));
            assertEquals(new Order(1, CUSTOMER, Date.valueOf("2020-02-02"), null, Set.of(new Product(1, "product", 111111111, (float) 2.0, 10, 5, 10,  false,  new Supplier())), (float) 2.0), getOrdersResult.get(4));
        }

        void thenAssertOrderIsDelivered()
        {
            assertNotEquals("null", setOrderToDeliveredResult.getArrival_date());
        }

        void thenAssertOrdersAreReturned()
        {
            assertEquals(5, getOrdersForProductResult.size());
            assertTrue(getOrdersForProductResult.get(0).getProducts().contains(PRODUCT));
            assertTrue(getOrdersForProductResult.get(1).getProducts().contains(PRODUCT));
            assertTrue(getOrdersForProductResult.get(2).getProducts().contains(PRODUCT));
            assertTrue(getOrdersForProductResult.get(3).getProducts().contains(PRODUCT));
            assertTrue(getOrdersForProductResult.get(4).getProducts().contains(PRODUCT));
        }

        void thenAssertEmptyArrayListIsReturend(List<Order> orders)
        {
            assertEquals(0, orders.size());
        }

        void thenAssertCorrectOrdersAreReturned()
        {
            assertEquals(3, getOrdersForCustomerResult.size());
        }

        void thenAssertOrderIsCorrect()
        {
            assertEquals(3, createNewOrderResult.getProducts().size());
            assertEquals(CUSTOMER, createNewOrderResult.getCustomer());
        }
    }
}
