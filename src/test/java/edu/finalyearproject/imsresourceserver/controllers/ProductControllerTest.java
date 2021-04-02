package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Customer;
import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.repositories.OrderRepository;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.SupplierRepository;
import edu.finalyearproject.imsresourceserver.requests.NewProductRequest;
import edu.finalyearproject.imsresourceserver.requests.ProductAmountRequest;
import edu.finalyearproject.imsresourceserver.requests.ProductThresholdRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductControllerTest
{
    private static final Supplier SUPPLIER_1 = new Supplier(1, "supplier1", (float) 2);
    private static final Product PRODUCT_1 = new Product("product1", 11111111, (float) 5.0, 10, 5, 2, SUPPLIER_1);
    private static final Product PRODUCT_TO_SUSPEND = new Product("product1", 11111111, (float) 5.0, 10, 5, 2, SUPPLIER_1);
    private static final String PRODUCT_1_NAME = "product1";
    private static final int SKU = 11111111;
    private static final String SUPPLIER_NAME = "supplier1";

    @InjectMocks
    private ProductController target;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private OrderRepository orderRepository;

    private List<Product> getProductsResult;
    private List<String> getProductNamesResult;
    private Product getProductByNameResult;
    private Product getProductBySkuResult;
    private List<Product> getProductsForSupplierResult;
    private Product getProductResult;
    private Product addProductResult;
    private Product suspendProductResult;
    private Product reinstateProductResult;
    private List<Product> getProductsLowOnStockResult;
    private Product updateReorderThresholdResult;
    private Product updateReorderAmountResult;
    private float getAverageDailySalesResult;

    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void getProducts_returnsAllProducts()
    {
        fixture.givenRepositoryContainsProducts();
        fixture.whenGetProductsIsCalled();
        fixture.thenAssertListIsCorrect();
    }

    @Test
    public void getProductNames_returnsCorrectProductNames()
    {
        fixture.givenRepositoryContainsProducts();
        fixture.whenGetProductNamesIsCalled();
        fixture.thenAssertListOfNamesIsCorrect();
    }

    @Test
    public  void getProductByName_returnsCorrectName()
    {
        fixture.givenRepositoryReturnsProductFromName();
        fixture.whenGetProductByNameIsCalled();
        fixture.thenAssertReturnedProductIsCorrect(getProductByNameResult);
    }

    @Test
    public void getProductBySku_returnsCorrectProduct()
    {
        fixture.givenRepositoryReturnsProductFromSku();
        fixture.whenGetProductBySkuIsCalled();
        fixture.thenAssertReturnedProductIsCorrect(getProductBySkuResult);
    }

    @Test
    public void getProductsForSupplier_returnsMultipleProducts()
    {
        fixture.givenCorrectSupplierIsReturned();
        fixture.givenRepositoryReturnsProductsForSupplier();
        fixture.whenGetProductsForSupplierIsCalled();
        fixture.thenAssertReturnedProductsAreCorrect();
    }

    @Test
    public void getProduct_returnsCorrectProduct()
    {
        fixture.givenRepositoryReturnsOptionalWithProduct();
        fixture.whenGetProductIsCalled();
        fixture.thenAssertProductIsCorrect();
    }

    @Test
    public void getProduct_returnsEmptyProduct()
    {
        fixture.givenRepositoryReturnsEmptyOptional();
        fixture.whenGetProductIsCalled();
        fixture.thenAssertEmptyProductIsReturned(getProductResult);
    }

    @Test
    public void addProduct_addsProductCorrectly()
    {
        fixture.givenCorrectSupplierIsReturned();
        fixture.givenRepositorySavesProduct();
        fixture.whenAddProductIsCalled();
        fixture.thenAssertReturnedProductIsCorrect(addProductResult);
    }

    @Test
    public void suspendProduct_productGetsSuspended()
    {
        fixture.givenProductToSuspendIsNotSuspended();
        fixture.givenRepositoryReturnsOptionalWithProductToSuspend();
        fixture.givenRepositorySavesProduct();
        fixture.whenSuspendProductIsCalled();
        fixture.thenAssertProductIsSuspended();
        fixture.finallyReinstateProduct();
    }

    @Test
    public void suspendProduct_noProductFound()
    {
        fixture.givenProductToSuspendIsSuspended();
        fixture.givenRepositoryReturnsEmptyOptional();
        fixture.whenSuspendProductIsCalled();
        fixture.thenAssertEmptyProductIsReturned(suspendProductResult);
        fixture.finallySuspendProduct();
    }

    @Test
    public void reinstateProduct_productGetsReinstated()
    {
        fixture.givenProductToSuspendIsSuspended();
        fixture.givenRepositoryReturnsOptionalWithProductToSuspend();
        fixture.whenReinstateProductIsCalled();
        fixture.thenAssertProductIsReinstated();
        fixture.finallySuspendProduct();
    }

    @Test
    public void reinstateProduct_noProductFound()
    {
        fixture.givenProductToSuspendIsNotSuspended();
        fixture.givenRepositoryReturnsEmptyOptional();
        fixture.whenReinstateProductIsCalled();
        fixture.thenAssertEmptyProductIsReturned(reinstateProductResult);
        fixture.finallySuspendProduct();
    }

    @Test
    public void getProductsLowOnStock_onlyProductsLowOnStockReturned()
    {
        fixture.givenRepositoryReturnsProductsLowOnStock();
        fixture.whenGetProductsLowOnStockIsCalled();
        fixture.thenAssertAllProductsAreLowOnStock();
    }

    @Test
    public void updateReorderThreshold_updatesSuccessfully()
    {
        fixture.givenRepositoryReturnsOptionalWithProduct();
        fixture.whenUpdateReorderThresholdIsCalled();
        fixture.thenAssertReorderThresholdUpdated();
        fixture.finallyResetReorderThreshold();
    }

    @Test
    public void updateReorderThreshold_noProductFound()
    {
        fixture.givenRepositoryReturnsEmptyOptional();
        fixture.whenUpdateReorderThresholdIsCalled();
        fixture.thenAssertEmptyProductIsReturned(updateReorderThresholdResult);
    }

    @Test
    public void updateReorderAmount_updatesSuccessfully()
    {
        fixture.givenRepositoryReturnsOptionalWithProduct();
        fixture.whenUpdateReorderAmountIsCalled();
        fixture.thenAssertReorderAmountUpdated();
        fixture.finallyResetReorderAmount();
    }

    @Test
    public void updateReorderAmount_noProductFound()
    {
        fixture.givenRepositoryReturnsEmptyOptional();
        fixture.whenUpdateReorderAmountIsCalled();
        fixture.thenAssertEmptyProductIsReturned(updateReorderAmountResult);
    }

    @Test
    public void getAverageDailySales_returnsCorrectValue()
    {
        fixture.givenOrderRepositoryReturnsAllOrders();
        fixture.whenGetAverageDailySalesIsCalled();
        fixture.thenAssertCorrectValue();
    }

    private class Fixture
    {
        void givenRepositoryContainsProducts()
        {
            List<Product> products = new ArrayList<>();
            products.add(new Product("product1", 11111111, (float) 0.20, 10, 5, 2, new Supplier()));
            products.add(new Product("product2", 22222222, (float) 0.20, 10, 5, 2, new Supplier()));
            products.add(new Product("product3", 33333333, (float) 0.20, 10, 5, 2, new Supplier()));
            when(productRepository.findAll()).thenReturn(products);
        }

        void givenRepositoryReturnsProductFromName()
        {
            when(productRepository.findByname(PRODUCT_1_NAME)).thenReturn(PRODUCT_1);
        }

        void givenRepositoryReturnsProductFromSku()
        {
            when(productRepository.findBysku(SKU)).thenReturn(PRODUCT_1);
        }

        void givenCorrectSupplierIsReturned()
        {
            when(supplierRepository.findByname(SUPPLIER_NAME)).thenReturn(SUPPLIER_1);
        }

        void givenRepositoryReturnsProductsForSupplier()
        {
            when(productRepository.findBysupplier(SUPPLIER_1)).thenReturn(List.of(PRODUCT_1));
        }

        void givenRepositoryReturnsOptionalWithProduct()
        {
            when(productRepository.findById(1)).thenReturn(Optional.of(PRODUCT_1));
        }

        void givenRepositoryReturnsEmptyOptional()
        {
            when(productRepository.findById(1)).thenReturn(Optional.empty());
        }

        void givenRepositorySavesProduct()
        {
            when(productRepository.save(PRODUCT_1)).thenReturn(PRODUCT_1);
        }

        void givenRepositoryReturnsOptionalWithProductToSuspend()
        {
            when(productRepository.findById(1)).thenReturn(Optional.of(PRODUCT_TO_SUSPEND));
        }

        void givenProductToSuspendIsNotSuspended()
        {
            PRODUCT_TO_SUSPEND.setSuspended(false);
        }

        void givenProductToSuspendIsSuspended()
        {
            PRODUCT_TO_SUSPEND.setSuspended(true);
        }

        void givenRepositoryReturnsProductsLowOnStock()
        {
            List<Product> products = new ArrayList<>();
            products.add(new Product("product1", 11111111, (float) 0.20, 10, 5, 2, new Supplier()));
            products.add(new Product("product2", 22222222, (float) 0.20, 100, 5, 2, new Supplier()));
            products.add(new Product("product3", 33333333, (float) 0.20, 20, 5, 2, new Supplier()));
            products.add(new Product("product4", 44444444, (float) 0.20, 100, 5, 2, new Supplier()));
            products.add(new Product("product5", 55555555, (float) 0.20, 100, 5, 2, new Supplier()));
            products.add(new Product("product6", 66666666, (float) 0.20, 30, 20, 2, new Supplier()));
            when(productRepository.findAll()).thenReturn(products);
        }

        void givenOrderRepositoryReturnsAllOrders()
        {
            List<Order> orders = new ArrayList<>();
            orders.add(new Order(1,new Customer(), Date.valueOf("2020-10-19"), null, Set.of(PRODUCT_1), (float)2.0));
            orders.add(new Order(2,new Customer(), Date.valueOf("2020-10-20"), null, Set.of(PRODUCT_1), (float)2.0));
            orders.add(new Order(3,new Customer(), Date.valueOf("2020-10-06"), null, Set.of(PRODUCT_1), (float)2.0));
            orders.add(new Order(4,new Customer(), Date.valueOf("2020-10-02"), null, Set.of(PRODUCT_1), (float)2.0));
            orders.add(new Order(5,new Customer(), Date.valueOf("2020-10-24"), null, Set.of(PRODUCT_1), (float)2.0));
            when(orderRepository.findAll()).thenReturn(orders);
        }

        void whenGetProductsIsCalled()
        {
            getProductsResult = target.getProducts();
        }

        void whenGetProductNamesIsCalled()
        {
            getProductNamesResult = target.getProductNames();
        }

        void whenGetProductByNameIsCalled()
        {
            getProductByNameResult = target.getProductByName(PRODUCT_1_NAME);
        }

        void whenGetProductBySkuIsCalled()
        {
            getProductBySkuResult = target.getProductBySku(SKU);
        }

        void whenGetProductsForSupplierIsCalled()
        {
            getProductsForSupplierResult = target.getProductsForSupplier(SUPPLIER_NAME);
        }

        void whenGetProductIsCalled()
        {
            getProductResult = target.getProduct(1);
        }

        void whenAddProductIsCalled()
        {
            NewProductRequest productRequest = new NewProductRequest("product1", "11111111", 10, 5, 5, 2, SUPPLIER_NAME);
            addProductResult = target.addProduct(productRequest);
        }

        void whenSuspendProductIsCalled()
        {
            suspendProductResult = target.suspendProduct(1);
        }

        void whenReinstateProductIsCalled()
        {
            reinstateProductResult = target.reinstateProduct(1);
        }

        void whenGetProductsLowOnStockIsCalled()
        {
            getProductsLowOnStockResult = target.getProductsLowOnStock();
        }

        void whenUpdateReorderThresholdIsCalled()
        {
            updateReorderThresholdResult = target.updateReorderThreshold(1, new ProductThresholdRequest(100));
        }

        void whenUpdateReorderAmountIsCalled()
        {
            updateReorderAmountResult = target.updateReorderAmount(1, new ProductAmountRequest(200));
        }

        void whenGetAverageDailySalesIsCalled()
        {
            getAverageDailySalesResult = target.getAverageDailySales(1);
        }

        void thenAssertListIsCorrect()
        {
            assertEquals(3, getProductsResult.size());
            assertSame("product1", getProductsResult.get(0).getName());
            assertSame("product2", getProductsResult.get(1).getName());
            assertSame("product3", getProductsResult.get(2).getName());
        }

        void thenAssertListOfNamesIsCorrect()
        {
            assertEquals(3, getProductNamesResult.size());
            assertSame("product1", getProductNamesResult.get(0));
            assertSame("product2", getProductNamesResult.get(1));
            assertSame("product3", getProductNamesResult.get(2));
        }

        void thenAssertReturnedProductIsCorrect(Product product)
        {
            assertEquals(product, new Product("product1", 11111111, (float) 5.00, 10, 5, 2, SUPPLIER_1));
        }

        void thenAssertReturnedProductsAreCorrect()
        {
            assertEquals(1, getProductsForSupplierResult.size());
            assertSame("product1", getProductsForSupplierResult.get(0).getName());
        }

        void thenAssertProductIsCorrect()
        {
            assertEquals(getProductResult, PRODUCT_1);
        }

        void thenAssertEmptyProductIsReturned(Product product)
        {
            assertEquals(product, new Product());
        }

        void thenAssertProductIsSuspended()
        {
            assertTrue(suspendProductResult.isSuspended());
        }

        void thenAssertProductIsReinstated()
        {
            assertFalse(reinstateProductResult.isSuspended());
        }

        void thenAssertAllProductsAreLowOnStock()
        {
            assertEquals(3, getProductsLowOnStockResult.size());
            assertEquals("product1", getProductsLowOnStockResult.get(0).getName());
            assertEquals("product3", getProductsLowOnStockResult.get(1).getName());
            assertEquals("product6", getProductsLowOnStockResult.get(2).getName());
        }

        void thenAssertReorderThresholdUpdated()
        {
            assertEquals(100, (int) updateReorderThresholdResult.getReorder_threshold());
        }

        void thenAssertReorderAmountUpdated()
        {
            assertEquals(200, (int) updateReorderAmountResult.getReorder_quantity());
        }

        void thenAssertCorrectValue()
        {
            assertEquals(0, getAverageDailySalesResult, 0);
        }

        void finallyReinstateProduct()
        {
            target.reinstateProduct(1);
        }

        void finallySuspendProduct()
        {
            target.suspendProduct(1);
        }

        void finallyResetReorderThreshold()
        {
            PRODUCT_1.setReorder_threshold(5);
        }

        void finallyResetReorderAmount()
        {
            PRODUCT_1.setReorder_quantity(2);
        }
    }
}
