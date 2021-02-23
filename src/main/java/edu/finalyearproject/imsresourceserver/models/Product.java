/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;

/**
 * Model Class for product records in the 'products' database table.
 */
@Entity
@Table(name = "products")
@Data
public class Product
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer sku;
    private Float price;
    private Integer inventory_on_hand;
    private Integer reorder_threshold;
    private Integer reorder_quantity;
    private boolean suspended;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="supplier_id", nullable=false)
    private Supplier supplier;

    public Product()
    {

    }

    /**
     * Custom Constructor for creating a new Product record. Unspecified values have default values in database.
     * @param name - The name of the product.
     * @param sku - the products sku (barcode number).
     * @param price - the price of the product.
     * @param inventory_on_hand - the starting amount of inventory of the product.
     * @param reorder_threshold - the amount at which more stock needs to be ordered for the product.
     * @param reorder_quantity - the amount of stock that gets ordered when more stock is ordered from supplier.
     * @param supplier - the supplier of the product.
     */
    public Product(String name, int sku, float price, int inventory_on_hand, int reorder_threshold,
                                                                                int reorder_quantity, Supplier supplier)
    {
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.inventory_on_hand = inventory_on_hand;
        this.reorder_threshold = reorder_threshold;
        this.reorder_quantity = reorder_quantity;
        this.supplier = supplier;
    }
}
