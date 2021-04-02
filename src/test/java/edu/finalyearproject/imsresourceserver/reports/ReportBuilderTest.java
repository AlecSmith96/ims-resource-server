package edu.finalyearproject.imsresourceserver.reports;

import edu.finalyearproject.imsresourceserver.controllers.ProductControllerTest;
import edu.finalyearproject.imsresourceserver.models.*;
import edu.finalyearproject.imsresourceserver.requests.StockMovement;
import edu.finalyearproject.imsresourceserver.requests.WasteItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ReportBuilderTest
{
    public static final String THE_STRING = "The String";
    @InjectMocks
    private ReportBuilder target;

    @Mock
    private TemplateEngine templateEngine;

    private ReportBuilder withContextResult;
    private ReportBuilder withOrdersListResult;
    private ReportBuilder withPurchasesListResult;
    private ReportBuilder withProductListResult;
    private ReportBuilder withStockMovementResult;
    private ReportBuilder withWasteItemsResult;
    private ReportBuilder withStringResult;

    private List<Order> orders = getOrders();
    private List<Purchase> purchases = getPurchases();
    private Set<Product> products = getProducts();
    private List<StockMovement> stockMovements = getStockMovements();
    private List<WasteItem> wasteItems = getWasteItems();
    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void withContext_setsContextAndLocaleCorrectly()
    {
        fixture.whenWithContextIsCalled();
        fixture.thenAssertNewContextAndLocale();
    }

    @Test
    public void withOrdersList_addsListToContextCorrectly()
    {
        fixture.whenWithOrdersListIsCalled();
        fixture.thenAssertOrdersAddedToContext();
    }

    @Test
    public void withPurchasesList_addsListToContextCorrectly()
    {
        fixture.whenWithPurchasesListIsCalled();
        fixture.thenAssertPurchasesAddedToContext();
    }

    @Test
    public void withProductList_addsListToContextCorrectly()
    {
        fixture.whenWithProductListIsCalled();
        fixture.thenAssertProductsAddedToContext();
    }

    @Test
    public void withStockMovementList_addListToContextCorrectly()
    {
        fixture.whenWithStockMovementListIsCalled();
        fixture.thenAssertStockMovementsAddedToContext();
    }

    @Test
    public void withWasteItemList_addListToContextCorrectly()
    {
        fixture.whenWithWasteItemListIsCalled();
        fixture.thenAssertWasteItemsAddedToContext();
    }

    @Test
    public void withString_addListToContextCorrectly()
    {
        fixture.whenWithStringIsCalled();
        fixture.thenAssertStringAddedToContext();
    }

    private List<Order> getOrders()
    {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, new Customer(), Date.valueOf("2020-02-03"), null, Set.of(new Product()), (float) 2.0));
        orders.add(new Order(2, new Customer(), Date.valueOf("2020-02-03"), null, Set.of(new Product()), (float) 2.0));
        orders.add(new Order(3, new Customer(), Date.valueOf("2020-02-03"), null, Set.of(new Product()), (float) 2.0));
        return orders;
    }

    private List<Purchase> getPurchases()
    {
        List<Purchase> purchases = new ArrayList<>();
        purchases.add(new Purchase(new Supplier(), Date.valueOf("2020-02-03"), Set.of(new Product())));
        purchases.add(new Purchase(new Supplier(), Date.valueOf("2020-02-03"), Set.of(new Product())));
        purchases.add(new Purchase(new Supplier(), Date.valueOf("2020-02-03"), Set.of(new Product())));
        return purchases;
    }

    private Set<Product> getProducts()
    {
        Set<Product> products = new HashSet<>();
        products.add(new Product(1, "product1", 11111111, (float) 0.5, 10, 5, 100, false, new Supplier()));
        products.add(new Product(2, "product2", 22222222, (float) 0.5, 10, 5, 100, false, new Supplier()));
        products.add(new Product(3, "product3", 33333333, (float) 0.5, 10, 5, 100, false, new Supplier()));
        return products;
    }

    private List<StockMovement> getStockMovements()
    {
        List<StockMovement> stockMovements = new ArrayList<>();
        stockMovements.add(new StockMovement(1, false, Date.valueOf("2020-02-03"), "John Doe", new Product(), 1));
        stockMovements.add(new StockMovement(2, false, Date.valueOf("2020-02-03"), "John Doe", new Product(), 1));
        stockMovements.add(new StockMovement(3, false, Date.valueOf("2020-02-03"), "John Doe", new Product(), 1));
        return stockMovements;
    }

    private List<WasteItem> getWasteItems()
    {
        List<WasteItem> wasteItems = new ArrayList<>();
        wasteItems.add(new WasteItem(11111111, "reason", 1, "item"));
        wasteItems.add(new WasteItem(11111111, "reason", 1, "item"));
        wasteItems.add(new WasteItem(11111111, "reason", 1, "item"));
        return wasteItems;
    }

    private class Fixture
    {

        void whenWithContextIsCalled()
        {
            withContextResult = target.withContext();
        }

        void whenWithOrdersListIsCalled()
        {
            withOrdersListResult = target.withContext().withOrdersList("orders", orders);
        }

        void whenWithPurchasesListIsCalled()
        {
            withPurchasesListResult = target.withContext().withPurchasesList("purchases", purchases);
        }

        void whenWithProductListIsCalled()
        {
            withProductListResult = target.withContext().withProductList("products", products);
        }

        void whenWithStockMovementListIsCalled()
        {
            withStockMovementResult = target.withContext().withStockMovementList("stockMovements", stockMovements);
        }

        void whenWithWasteItemListIsCalled()
        {
            withWasteItemsResult = target.withContext().withWasteItemList("wasteItems", wasteItems);
        }

        void whenWithStringIsCalled()
        {
            withStringResult = target.withContext().withString("theString", THE_STRING);
        }

        void thenAssertNewContextAndLocale()
        {
            assertThat(withContextResult.getContext(), instanceOf(Context.class));
        }
        void thenAssertOrdersAddedToContext()
        {
            assertTrue(orders.equals(withOrdersListResult.getContext().getVariable("orders")));
        }

        void thenAssertPurchasesAddedToContext()
        {
            assertTrue(purchases.equals(withPurchasesListResult.getContext().getVariable("purchases")));
        }

        void thenAssertProductsAddedToContext()
        {
            assertTrue(products.equals(withProductListResult.getContext().getVariable("products")));
        }

        void thenAssertStockMovementsAddedToContext()
        {
            assertTrue(stockMovements.equals(withStockMovementResult.getContext().getVariable("stockMovements")));
        }

        void thenAssertWasteItemsAddedToContext()
        {
            assertTrue(wasteItems.equals(withWasteItemsResult.getContext().getVariable("wasteItems")));
        }

        void thenAssertStringAddedToContext()
        {
            assertTrue(THE_STRING.equals(withStringResult.getContext().getVariable("theString")));
        }
    }
}
