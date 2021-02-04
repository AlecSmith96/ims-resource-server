package edu.finalyearproject.imsresourceserver.controllers;

import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import edu.finalyearproject.imsresourceserver.repositories.ProductRepository;
import edu.finalyearproject.imsresourceserver.repositories.PurchaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class PurchaseController
{
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

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
}
