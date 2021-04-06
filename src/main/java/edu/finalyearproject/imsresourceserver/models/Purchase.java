/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "purchases")
@Data
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Purchase implements Comparable<Purchase>
{
    @Transient
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="supplier_id", nullable=false)
    private Supplier supplier;

    private Date purchase_date;

    private Date arrival_date;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "purchase_items",
            joinColumns = @JoinColumn(name = "purchase_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();

    public Purchase()
    {

    }

    public Purchase(Supplier supplier, Date date, Set<Product> products)
    {
        this.supplier = supplier;
        this.purchase_date = date;
        this.products = products;
    }

    public Date getPurchaseDateObject()
    {
        return purchase_date;
    }

    @Override
    public int compareTo(Purchase purchase)
    {
        return purchase_date.compareTo(purchase.getPurchaseDateObject());
    }

    public String getPurchase_date()
    {
        return DATE_FORMATTER.format(purchase_date);
    }

    public String getArrival_date()
    {
        if (arrival_date == null)
        {
            return "null";
        }
        return DATE_FORMATTER.format(arrival_date);
    }
}
