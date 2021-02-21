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
}
