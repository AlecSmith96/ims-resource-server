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
import edu.finalyearproject.imsresourceserver.requests.ProductIds;
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
import java.util.*;
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
        List<Purchase> all = purchaseRepository.findAll();
        Collections.sort(all, Collections.reverseOrder());
        return all;
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
        Date date = getDate();

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
     * POST method to reorder a purchase order with the corresponding id.
     * @param id - the id of the purchase order to be reordered.
     */
    @PostMapping("/purchase/reorder/{id}")
    public void reorderPurchaseOrder(@PathVariable Integer id)
    {
        log.info("Reording products from supplier order #"+id);
        Optional<Purchase> purchase = purchaseRepository.findById(id);
        if (purchase.isPresent())
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();
            Set<Product> products = new HashSet<>(purchase.get().getProducts());
            Purchase newPurchase = new Purchase(purchase.get().getSupplier(), Date.valueOf(dtf.format(now)), products);

            purchaseRepository.save(newPurchase);
        }
    }

    /**
     * POST method to create a new purchase order and return HTML invoice for it.
     * @param productIds - form data containing supplier name and list of product names.
     * @return HttpEntity - HTTP response containing invoice in body and necessary headers.
     */
    @PostMapping("/purchase/create")
    public void createPurchaseOrder(@RequestBody ProductIds productIds)
    {
        Map<Supplier, Set<Product>> productsBySupplier = new HashMap<>();
        for (int id : productIds.getIds())
        {
            Optional<Product> productOp = productRepository.findById(id);
            addProductToMap(productsBySupplier, productOp);
        }
        createPurchaseOrdersAndInvoices(productsBySupplier);

        log.info("All purchase orders created...");
    }

    private void addProductToMap(Map<Supplier, Set<Product>> productsBySupplier, Optional<Product> productOp)
    {
        if (productOp.isPresent())
        {
            Product product = productOp.get();
            if (!productsBySupplier.containsKey(product.getSupplier()))
            {
                productsBySupplier.put(product.getSupplier(), new HashSet<>());
                productsBySupplier.get(product.getSupplier()).add(product);
            } else
            {
                productsBySupplier.get(product.getSupplier()).add(product);
            }
        }
    }

    private void createPurchaseOrdersAndInvoices(Map<Supplier, Set<Product>> productsBySupplier)
    {
        Date date = getDate();
        for (Supplier supplier : productsBySupplier.keySet())
        {
            Purchase purchase = new Purchase(supplier, date, productsBySupplier.get(supplier));
            purchaseRepository.save(purchase);
            reportsController.generatePurchaseInvoice(purchase);
        }
    }

    private Date getDate()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return Date.valueOf(dtf.format(now));
    }
}
