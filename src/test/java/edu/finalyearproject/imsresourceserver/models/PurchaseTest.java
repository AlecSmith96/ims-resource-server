/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseTest
{
    @InjectMocks
    private Purchase target;

    private String getPurchaseDateResult;
    private String getArrivalDateResult;

    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void getPurchaseDate_returnsFormattedDateString()
    {
        fixture.givenPurchaseIsCreated();
        fixture.whenGetPurchaseDateIsCalled();
        fixture.thenAssertDateStringIsCorrect();
    }

    @Test
    public void getArrivalDate_returnsNullString()
    {
        fixture.givenPurchaseIsCreated();
        fixture.whenGetArrivalDateIsCalled();
        fixture.thenAssertArrivalDateIs("null");
    }

    @Test
    public void getArrivalDate_returnsFormattedDateString()
    {
        fixture.givenPurchaseIsCreated();
        fixture.givenArrivalDateIsSet();
        fixture.whenGetArrivalDateIsCalled();
        fixture.thenAssertArrivalDateIs("13-02-2020");
    }

    private class Fixture
    {
        void givenPurchaseIsCreated()
        {
            target = new Purchase(1, new Supplier(), Date.valueOf("2020-02-03"), null, Set.of(new Product()));
        }

        void givenArrivalDateIsSet()
        {
            target.setArrival_date(Date.valueOf("2020-02-13"));
        }

        void whenGetPurchaseDateIsCalled()
        {
            getPurchaseDateResult = target.getPurchase_date();
        }

        void whenGetArrivalDateIsCalled()
        {
            getArrivalDateResult = target.getArrival_date();
        }

        void thenAssertDateStringIsCorrect()
        {
            assertEquals("03-02-2020", getPurchaseDateResult);
        }

        void thenAssertArrivalDateIs(String date)
        {
            assertEquals(date, getArrivalDateResult);
        }
    }
}
