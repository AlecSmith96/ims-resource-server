package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.PurchaseRepository;
import edu.finalyearproject.imsresourceserver.requests.ProductIds;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseControllerTest
{
    private static final Supplier SUPPLIER = new Supplier(1, "supplier1", (float) 2.0);
    private static final Supplier SUPPLIER_2 = new Supplier(2, "supplier1", (float) 2.0);
    private static final Supplier SUPPLIER_3 = new Supplier(3, "supplier1", (float) 2.0);
    private static final Product PRODUCT = new Product(1, "product1", 11111111, (float) 5.0, 10, 5, 2, false, SUPPLIER);
    private static final Product PRODUCT_2 = new Product(2, "product2", 22222222, (float) 5.0, 10, 5, 2, false, SUPPLIER_2);
    private static final Product PRODUCT_3 = new Product(3, "product3", 33333333, (float) 5.0, 10, 5, 2, false, SUPPLIER_2);
    private static final Product PRODUCT_4 = new Product(4, "product3", 44444444, (float) 5.0, 10, 5, 2, false, SUPPLIER_3);
    private static final Product PRODUCT_5 = new Product(5, "product3", 55555555, (float) 5.0, 10, 5, 2, false, SUPPLIER_3);
    private static final Purchase PURCHASE = new Purchase(1, new Supplier(), Date.valueOf("2020-02-03"), null, Set.of(PRODUCT, PRODUCT_2));
    private static final List<Purchase> PURCHASES = getPurchases();
    private static final ProductIds PRODUCT_IDS = getIds();

    @InjectMocks
    private PurchaseController target;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReportsController reportsController;

    private List<Purchase> getPurchasesResult;
    private List<Purchase> getPurchasesForProductResult;
    private Purchase setOrderToDeliveredResult;
    private Purchase reorderPurchaseOrderResult;
    private Map<Supplier, Set<Product>> createPurchaseOrderResult;

    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void getPurchases_returnsSortedPurchases()
    {
        fixture.givenPurchaseRepositoryReturnsPurchases();
        fixture.whenGetPurchasesIsCalled();
        fixture.thenAssertPurchasesAreSortedCorrectly();
    }

    @Test
    public void getPurchasesForProduct_returnsPurchasesCorrectly()
    {
        fixture.givenProductRepositoryReturnsOptionalWithProduct();
        fixture.givenPurchaseRepositoryReturnsPurchasesForProduct();
        fixture.whenGetPurchasesForProductIsCalled();
        fixture.thenAssertPurchasesAreReturned();
    }

    @Test
    public void getPurchasesForProduct_returnsEmptyArrayList()
    {
        fixture.givenProductRepositoryReturnsEmptyOptional();
        fixture.whenGetPurchasesForProductIsCalled();
        fixture.thenAssertEmptyArrayListIsReturned();
    }

    @Test
    public void setOrderToDelivered_setsPurchaseToDelivered()
    {
        fixture.givenProductRepositoryReturnsProduct();
        fixture.whenSetOrderToDeliveredIsCalled();
        fixture.thenAssertPurchaseIsDelivered();
    }

    @Test
    public void reorderPurchaseOrder_reordersPurchaseSuccessfully()
    {
        fixture.givenPurchaseRepositoryReturnsOptionalWithPurchase();
        fixture.whenReorderPurchaseOrderIsCalled();
        fixture.thenAssertNewPurchaseOrderIsCorrect();
    }

    @Test
    public void reorderPurchaseOrder_noPurchaseOrderFound()
    {
        fixture.givenPurchaseRepositoryReturnsEmptyOptional();
        fixture.whenReorderPurchaseOrderIsCalled();
        fixture.thenAssertEmptyPurchaseOrderReturned();
    }

    @Test
    public void createPurchaseOrder_multipleSupplierOrders()
    {
        fixture.givenProductRepositoryReturnsMultipleProductOptionals();
        fixture.whenCreatePurchaseOrderIsCalled();
        fixture.thenAssertProductsBySupplierMapIsCorrect();
    }

    private static List<Purchase> getPurchases()
    {
        List<Purchase> purchases = new ArrayList<>();
        purchases.add(new Purchase(1, new Supplier(), Date.valueOf("2020-02-03"), null, Set.of(PRODUCT)));
        purchases.add(new Purchase(2, new Supplier(), Date.valueOf("2020-02-16"), null, Set.of(PRODUCT)));
        purchases.add(new Purchase(3, new Supplier(), Date.valueOf("2020-03-23"), null, Set.of(PRODUCT)));
        purchases.add(new Purchase(4, new Supplier(), Date.valueOf("2020-03-10"), null, Set.of(PRODUCT)));
        purchases.add(new Purchase(5, new Supplier(), Date.valueOf("2020-04-01"), null, Set.of(PRODUCT)));
        return purchases;
    }

    private static ProductIds getIds()
    {
        ProductIds productIds = new ProductIds();
        productIds.setIds(new ArrayList<>());
        productIds.getIds().add(1);
        productIds.getIds().add(2);
        productIds.getIds().add(3);
        productIds.getIds().add(4);
        productIds.getIds().add(5);
        return productIds;
    }

    private class Fixture
    {
        void givenPurchaseRepositoryReturnsPurchases()
        {
            when(purchaseRepository.findAll()).thenReturn(PURCHASES);
        }

        void givenProductRepositoryReturnsOptionalWithProduct()
        {
            when(productRepository.findById(1)).thenReturn(Optional.of(PRODUCT));
        }

        void givenPurchaseRepositoryReturnsPurchasesForProduct()
        {
            when(purchaseRepository.findByproducts(PRODUCT)).thenReturn(PURCHASES);
        }

        void givenProductRepositoryReturnsEmptyOptional()
        {
            when(productRepository.findById(1)).thenReturn(Optional.empty());
        }

        void givenProductRepositoryReturnsProduct()
        {
            when(purchaseRepository.findByid(1)).thenReturn(PURCHASE);
        }

        void givenPurchaseRepositoryReturnsOptionalWithPurchase()
        {
            when(purchaseRepository.findById(1)).thenReturn(Optional.of(PURCHASE));
        }

        void givenPurchaseRepositoryReturnsEmptyOptional()
        {
            when(purchaseRepository.findById(1)).thenReturn(Optional.empty());
        }

        void givenProductRepositoryReturnsMultipleProductOptionals()
        {
            when(productRepository.findById(1)).thenReturn(Optional.of(PRODUCT));
            when(productRepository.findById(2)).thenReturn(Optional.of(PRODUCT_2));
            when(productRepository.findById(3)).thenReturn(Optional.of(PRODUCT_3));
            when(productRepository.findById(4)).thenReturn(Optional.of(PRODUCT_4));
            when(productRepository.findById(5)).thenReturn(Optional.of(PRODUCT_5));
        }

        void whenGetPurchasesIsCalled()
        {
            getPurchasesResult = target.getPurchases();
        }

        void whenGetPurchasesForProductIsCalled()
        {
            getPurchasesForProductResult = target.getPurchasesForProduct(1);
        }

        void whenSetOrderToDeliveredIsCalled()
        {
            setOrderToDeliveredResult = target.setOrderToDelivered(1);
        }

        void whenReorderPurchaseOrderIsCalled()
        {
            reorderPurchaseOrderResult = target.reorderPurchaseOrder(1);
        }
        void whenCreatePurchaseOrderIsCalled()
        {
            createPurchaseOrderResult = target.createPurchaseOrder(PRODUCT_IDS);
        }

        void thenAssertPurchasesAreSortedCorrectly()
        {
            assertEquals(5, getPurchasesResult.size());
            assertEquals("01-04-2020", getPurchasesResult.get(0).getPurchase_date());
            assertEquals("23-03-2020", getPurchasesResult.get(1).getPurchase_date());
            assertEquals("10-03-2020", getPurchasesResult.get(2).getPurchase_date());
            assertEquals("16-02-2020", getPurchasesResult.get(3).getPurchase_date());
            assertEquals("03-02-2020", getPurchasesResult.get(4).getPurchase_date());
        }

        void thenAssertPurchasesAreReturned()
        {
            assertEquals(5, getPurchasesForProductResult.size());
        }

        void thenAssertEmptyArrayListIsReturned()
        {
            assertEquals(0, getPurchasesForProductResult.size());
        }

        void thenAssertPurchaseIsDelivered()
        {
            assertNotEquals("null", setOrderToDeliveredResult.getArrival_date());
        }

        void thenAssertNewPurchaseOrderIsCorrect()
        {
            assertEquals(2, reorderPurchaseOrderResult.getProducts().size());
        }

        void thenAssertEmptyPurchaseOrderReturned()
        {
            assertEquals(null, reorderPurchaseOrderResult.getId());
        }

        void thenAssertProductsBySupplierMapIsCorrect()
        {
            assertEquals(3, createPurchaseOrderResult.keySet().size());
            assertEquals(Set.of(PRODUCT), createPurchaseOrderResult.get(SUPPLIER));
            assertEquals(Set.of(PRODUCT_2, PRODUCT_3), createPurchaseOrderResult.get(SUPPLIER_2));
            assertEquals(Set.of(PRODUCT_4, PRODUCT_5), createPurchaseOrderResult.get(SUPPLIER_3));
        }
    }
}
