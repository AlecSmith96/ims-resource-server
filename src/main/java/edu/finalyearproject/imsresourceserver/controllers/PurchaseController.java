package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import edu.finalyearproject.imsresourceserver.reports.ReportBuilder;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.PurchaseRepository;
import edu.finalyearproject.imsresourceserver.repositories.SupplierRepository;
import edu.finalyearproject.imsresourceserver.requests.PurchaseOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PurchaseController
{
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ReportBuilder reportBuilder;

    private Logger log = LoggerFactory.getLogger(PurchaseController.class);

    @GetMapping("/purchases/all")
    public List<Purchase> getPurchases()
    {
        return purchaseRepository.findAll();
    }

    @GetMapping("/purchases/product/{product_id}")
    public List<Purchase> getPurchasesForProduct(@PathVariable int product_id) throws Exception
    {
        log.info("Searching for purchase orders by product...");
        Optional<Product> product = productRepository.findById(product_id);
        if (product.isPresent())
        {
            return purchaseRepository.findByproducts(product.get());
        }
        throw new Exception("Unrecognised Product");
    }

    /**
     * POST method for setting a purchase order arrival date to todays date.
     * @param id - the id(primary key) of the purchase order to set to delivered.
     * @return void
     */
    @PostMapping("/purchase/delivered/{id}")
    public void setOrderToDelivered(@PathVariable int id)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(dtf.format(now));

        Purchase purchase = purchaseRepository.findByid(id);
        purchase.setArrival_date(date);

        // UPDATE product quantities

        purchaseRepository.save(purchase);
    }

    /**
     * POST method to create a new purchase order and return HTML invoice for it.
     * @param orderRequest - form data containing supplier name and list of product names.
     * @return HttpEntity - HTTP response containing invoice in body and necessary headers.
     */
    @PostMapping("/purchase/create")
    public HttpEntity<String> createPurchaseOrder(@RequestBody PurchaseOrderRequest orderRequest)
    {
        Purchase purchase = createPurchaseRecord(orderRequest);
        purchaseRepository.save(purchase);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html");
        headers.add("Content-Disposition", "attachment; purchase_invoice.html");

        return ResponseEntity.ok().headers(headers).body(generatePurchaseInvoice(purchase));
    }

    private Purchase createPurchaseRecord(@RequestBody PurchaseOrderRequest orderRequest)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(dtf.format(now));
        Supplier supplier = supplierRepository.findByname(orderRequest.getSupplier());
        Set<Product> products = orderRequest.getProducts().stream().map(productItem ->
                productRepository.findByname(productItem.getValue())).collect(Collectors.toSet());

        return new Purchase(supplier, date, products);
    }

    private String generatePurchaseInvoice(Purchase purchase)
    {
        String status = purchase.getArrival_date().equals("null") ? "PENDING" : "DELIVERED";
        return reportBuilder.withContext()
                    .withProductList("products", purchase.getProducts())
                    .withString("id", purchase.getId().toString())
                    .withString("supplier", purchase.getSupplier().getName())
                    .withString("purchase_date", purchase.getPurchase_date())
                    .withString("status", status)
                    .withString("arrival_date", purchase.getArrival_date())
                    .buildReport("supplier-invoice");
    }
}
