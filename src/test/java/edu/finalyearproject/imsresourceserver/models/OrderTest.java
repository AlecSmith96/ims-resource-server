package edu.finalyearproject.imsresourceserver.models;

import org.checkerframework.checker.units.qual.C;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.util.Set;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderTest
{
    private Order target;

    private String getOrderDateResult;
    private String getArrivalDateResult;

    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void getOrderDate_returnsFormattedString()
    {
        fixture.givenOrderIsCreated();
        fixture.whenGetOrderDateIsCalled();
        fixture.thenAssertDateStringIsCorrect("03-02-2020");
    }

    @Test
    public void getArrivalDate_returnsNullString()
    {
        fixture.givenOrderIsCreated();
        fixture.whenGetArrivalDateIsCalled();
        fixture.thenAssertArrivalDateStringIsCorrect("null");
    }

    @Test
    public void getArrivalDate_returnsFormattedString()
    {
        fixture.givenOrderIsCreated();
        fixture.givenArrivalDateIsSet();
        fixture.whenGetArrivalDateIsCalled();
        fixture.thenAssertArrivalDateStringIsCorrect("08-02-2020");
    }

    private class Fixture
    {
        void givenOrderIsCreated()
        {
            target = new Order(1, new Customer(), Date.valueOf("2020-02-03"), null, Set.of(new Product()), (float) 2.0);
        }

        void givenArrivalDateIsSet()
        {
            target.setArrival_date(Date.valueOf("2020-02-08"));
        }

        void whenGetOrderDateIsCalled()
        {
            getOrderDateResult = target.getOrder_date();
        }

        void whenGetArrivalDateIsCalled()
        {
            getArrivalDateResult = target.getArrival_date();
        }

        void thenAssertDateStringIsCorrect(String date)
        {
            assertEquals(date, getOrderDateResult);
        }

        void thenAssertArrivalDateStringIsCorrect(String date)
        {
            assertEquals(date, getArrivalDateResult);
        }
    }
}
