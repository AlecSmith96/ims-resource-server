/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.PurchaseRepository;
import edu.finalyearproject.imsresourceserver.repositories.SupplierRepository;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SupplierControllerTest
{
    private static final List<Supplier> SUPPLIERS = getSuppliers();
    private static final Supplier SUPPLIER = new Supplier(1, "supplier1", (float) 1);
    private static final List<Purchase> PURCHASES = getPurchases();
    private static final Product PRODUCT = new Product("product1", 11111111, (float) 5.0, 10, 5, 2, SUPPLIER);

    @InjectMocks
    private SupplierController target;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ProductRepository productRepository;

    private List<Supplier> getSuppliersResult;
    private List<String> getAllSupplierNamesResult;
    private Supplier getSupplierResult;
    private List<Purchase> getPurchaseOrdersForSupplierResult;
    private Supplier getSupplierForProductResult;
    private List<Product> getProductsForSupplierResult;

    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void getSuppliers_returnsAllSuppliers()
    {
        fixture.givenSupplierRepositoryReturnsSuppliers();
        fixture.whenGetSuppliersIsCalled();
        fixture.thenAssertSuppliersAreReturned();
    }

    @Test
    public void getAllSupplierNames_returnsAllNames()
    {
        fixture.givenSupplierRepositoryReturnsSuppliers();
        fixture.whenGetAllSupplierNamesIsCalled();
        fixture.thenAssertNamesAreCorrect();
    }

    @Test
    public void getSupplier_returnsCorrectSupplier()
    {
        fixture.givenSupplierRepositoryReturnsOptionalWithSupplier();
        fixture.whenGetSupplierIsCalled();
        fixture.thenAssertSupplierIsReturned();
    }

    @Test
    public void getSupplier_returnsEmptySupplierObject()
    {
        fixture.givenSupplierRepositoryReturnsEmptyOptional();
        fixture.whenGetSupplierIsCalled();
        fixture.thenAssertEmptySupplierIsReturned(getSupplierResult);
    }

    @Test
    public void getPurchaseOrdersForSupplier_returnsCorrectPurchaseOrders()
    {
        fixture.givenSupplierRepositoryReturnsOptionalWithSupplier();
        fixture.givenPurchaseRepositoryReturnsPurchaseOrders();
        fixture.whenGetPurchaseOrdersForSupplier();
        fixture.thenAssertPurchasesAreReturned();
    }

    @Test
    public void getPurchaseOrdersForSupplier_returnsEmptyArrayList()
    {
        fixture.givenSupplierRepositoryReturnsEmptyOptional();
        fixture.whenGetPurchaseOrdersForSupplier();
        fixture.thenAssertEmptyArrayListReturned();
    }

    @Test
    public void getSupplierForProduct_returnsCorrectSupplier()
    {
        fixture.givenProductRepositoryReturnsOptionalWithProduct();
        fixture.whenGetSupplierForProductIsCalled();
        fixture.thenAssertCorrectSupplierIsReturned();
    }

    @Test
    public void getSupplierForProduct_returnsEmptySupplier()
    {
        fixture.givenProductRepositoryReturnsEmptyOptional();
        fixture.whenGetSupplierForProductIsCalled();
        fixture.thenAssertEmptySupplierIsReturned(getSupplierForProductResult);
    }

    @Test
    public void getProductsForSupplier_returnsProducts()
    {
        fixture.givenSupplierRepositoryReturnsOptionalWithSupplier();
        fixture.givenProductRepositoryReturnsMultipleProducts();
        fixture.whenGetProductsForSupplierIsCalled();
        fixture.thenAssertProductsAreReturned();
    }

    @Test
    public void getProductsForSupplier_returnsEmptyArrayList()
    {
        fixture.givenSupplierRepositoryReturnsEmptyOptional();
        fixture.givenProductRepositoryReturnsEmptyOptional();
        fixture.whenGetProductsForSupplierIsCalled();
        fixture.thenAssertEmptyArrayListOfProductsIsReturned();
    }

    private static List<Supplier> getSuppliers()
    {
        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(new Supplier(1, "supplier1", (float) 1));
        suppliers.add(new Supplier(2, "supplier2", (float) 1));
        suppliers.add(new Supplier(3, "supplier3", (float) 1));
        suppliers.add(new Supplier(4, "supplier4", (float) 1));
        suppliers.add(new Supplier(5, "supplier5", (float) 1));
        return suppliers;
    }

    private static List<Purchase> getPurchases()
    {
        List<Purchase> purchases = new ArrayList<>();
        purchases.add(new Purchase(1, SUPPLIER, Date.valueOf("2020-02-03"), null, Set.of(new Product())));
        purchases.add(new Purchase(2, SUPPLIER, Date.valueOf("2020-02-04"), null, Set.of(new Product())));
        purchases.add(new Purchase(3, SUPPLIER, Date.valueOf("2020-02-05"), null, Set.of(new Product())));
        return purchases;
    }

    private class Fixture
    {
        void givenSupplierRepositoryReturnsSuppliers()
        {
            when(supplierRepository.findAll()).thenReturn(SUPPLIERS);
        }

        void givenSupplierRepositoryReturnsOptionalWithSupplier()
        {
            when(supplierRepository.findById(1)).thenReturn(Optional.of(SUPPLIER));
        }

        void givenSupplierRepositoryReturnsEmptyOptional()
        {
            when(supplierRepository.findById(1)).thenReturn(Optional.empty());
        }

        void givenPurchaseRepositoryReturnsPurchaseOrders()
        {
            when(purchaseRepository.findBysupplier(SUPPLIER)).thenReturn(PURCHASES);
        }

        void givenProductRepositoryReturnsOptionalWithProduct()
        {
            when(productRepository.findById(1)).thenReturn(Optional.of(PRODUCT));
        }

        void givenProductRepositoryReturnsEmptyOptional()
        {
            when(productRepository.findById(1)).thenReturn(Optional.empty());
        }

        void givenProductRepositoryReturnsMultipleProducts()
        {
            List<Product> products = new ArrayList<>();
            products.add(new Product("product1", 11111111, (float) 5.0, 10, 5, 2, SUPPLIER));
            products.add(new Product("product2", 22222222, (float) 5.0, 10, 5, 2, SUPPLIER));
            products.add(new Product("product3", 33333333, (float) 5.0, 10, 5, 2, SUPPLIER));
            when(productRepository.findBysupplier(SUPPLIER)).thenReturn(products);
        }

        void whenGetSuppliersIsCalled()
        {
            getSuppliersResult = target.getSuppliers();
        }

        void whenGetAllSupplierNamesIsCalled()
        {
            getAllSupplierNamesResult = target.getAllSupplierNames();
        }

        void whenGetSupplierIsCalled()
        {
            getSupplierResult = target.getSupplier(1);
        }

        void whenGetPurchaseOrdersForSupplier()
        {
            getPurchaseOrdersForSupplierResult = target.getPurchaseOrdersForSupplier(1);
        }

        void whenGetSupplierForProductIsCalled()
        {
            getSupplierForProductResult = target.getSupplierForProduct(1);
        }

        void whenGetProductsForSupplierIsCalled()
        {
            getProductsForSupplierResult = target.getProductsForSupplier(1);
        }

        void thenAssertSuppliersAreReturned()
        {
            assertEquals(5, getSuppliersResult.size());
        }

        void thenAssertNamesAreCorrect()
        {
            assertEquals("supplier1", getAllSupplierNamesResult.get(0));
            assertEquals("supplier2", getAllSupplierNamesResult.get(1));
            assertEquals("supplier3", getAllSupplierNamesResult.get(2));
            assertEquals("supplier4", getAllSupplierNamesResult.get(3));
            assertEquals("supplier5", getAllSupplierNamesResult.get(4));
        }

        void thenAssertSupplierIsReturned()
        {
            assertEquals(SUPPLIER, getSupplierResult);
        }

        void thenAssertEmptySupplierIsReturned(Supplier supplier)
        {
            assertEquals(new Supplier(), supplier);
        }

        void thenAssertPurchasesAreReturned()
        {
            assertEquals(3, getPurchaseOrdersForSupplierResult.size());
        }

        void thenAssertEmptyArrayListReturned()
        {
            assertEquals(0, getPurchaseOrdersForSupplierResult.size());
        }

        void thenAssertCorrectSupplierIsReturned()
        {
            assertEquals(SUPPLIER, getSupplierForProductResult);
        }

        void thenAssertProductsAreReturned()
        {
            assertEquals(3, getProductsForSupplierResult.size());
            assertEquals("product1", getProductsForSupplierResult.get(0).getName());
            assertEquals("product2", getProductsForSupplierResult.get(1).getName());
            assertEquals("product3", getProductsForSupplierResult.get(2).getName());
        }

        void thenAssertEmptyArrayListOfProductsIsReturned()
        {
            assertEquals(0, getProductsForSupplierResult.size());
        }
    }
}
