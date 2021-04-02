package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Address;
import edu.finalyearproject.imsresourceserver.models.Customer;
import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.repositories.CustomerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerControllerTest
{
    private static final Customer CUSTOMER = new Customer(1, "Mr", "John", null, "Doe", "john@email.com", "01234567890", new Address(), Set.of(new Order()));

    @InjectMocks
    private CustomerController target;

    @Mock
    private CustomerRepository customerRepository;

    private List<Customer> getCustomersResult;
    private Customer getCustomerResult;

    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void getCustomers_returnsAllCustomerRecords()
    {
        fixture.givenRespositoryReturnsCustomers();
        fixture.whenGetCustomersIsCalled();
        fixture.thenAssertAllCustomersReturned();
    }

    @Test
    public void getCustomer_returnsCorrectCustomer()
    {
        fixture.givenRespositoryReturnsCustomer();
        fixture.whenGetCustomerIsCalled();
        fixture.thenAssertCustomerIsCorrect();
    }

    private class Fixture
    {
        void givenRespositoryReturnsCustomers()
        {
            List<Customer> customers = new ArrayList<>();
            customers.add(new Customer(1, "Mr", "John", null, "Doe", "john@email.com", "01234567890", new Address(), Set.of(new Order())));
            customers.add(new Customer(2, "Mr", "John", null, "Doe", "john@email.com", "01234567890", new Address(), Set.of(new Order())));
            customers.add(new Customer(3, "Mr", "John", null, "Doe", "john@email.com", "01234567890", new Address(), Set.of(new Order())));
            customers.add(new Customer(4, "Mr", "John", null, "Doe", "john@email.com", "01234567890", new Address(), Set.of(new Order())));
            when(customerRepository.findAll()).thenReturn(customers);
        }

        void givenRespositoryReturnsCustomer()
        {
            when(customerRepository.findByid(1)).thenReturn(CUSTOMER);
        }

        void whenGetCustomersIsCalled()
        {
            getCustomersResult = target.getCustomers();
        }

        void whenGetCustomerIsCalled()
        {
            getCustomerResult = target.getCustomer(1);
        }

        void thenAssertAllCustomersReturned()
        {
            assertEquals(4, getCustomersResult.size());
        }

        void thenAssertCustomerIsCorrect()
        {
            assertEquals(getCustomerResult, CUSTOMER);
        }
    }
}
