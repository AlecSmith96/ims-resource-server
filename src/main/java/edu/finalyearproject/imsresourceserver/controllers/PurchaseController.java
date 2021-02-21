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
import edu.finalyearproject.imsresourceserver.requests.PurchaseOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    private ReportsController reportsController;

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
     * POST method for setting a purchase order arrival date to todays date and updates product quantities.
     * @param id - the id(primary key) of the purchase order to set to delivered.
     * @return void
     */
    @PostMapping("/purchase/delivered/{id}")
    public void setOrderToDelivered(@PathVariable int id)
    {
        log.info("Setting purchase order " + id + " to delivered..");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.valueOf(dtf.format(now));

        Purchase purchase = purchaseRepository.findByid(id);
        purchase.setArrival_date(date);

        // UPDATE product quantities
        Set<Product> products = purchase.getProducts();
        products.stream().forEach(product -> {
            product.setInventory_on_hand(product.getInventory_on_hand() + product.getReorder_quantity());
            productRepository.save(product);
        });

        purchaseRepository.save(purchase);
    }

    /**
     * POST method to create a new purchase order and return HTML invoice for it.
     * @param orderRequest - form data containing supplier name and list of product names.
     * @return HttpEntity - HTTP response containing invoice in body and necessary headers.
     */
    @PostMapping("/purchase/create")
    public ResponseEntity<String> createPurchaseOrder(@RequestBody PurchaseOrderRequest orderRequest)
    {
        log.info("Creating purchase order for supplier: "+orderRequest.getSupplier());
        Purchase purchase = createPurchaseRecord(orderRequest);
        purchaseRepository.save(purchase);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html");
//        headers.add("Content-Disposition", "attachment; purchase_invoice.html");

        return ResponseEntity.ok().headers(headers).body(reportsController.generatePurchaseInvoice(purchase));
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

}
