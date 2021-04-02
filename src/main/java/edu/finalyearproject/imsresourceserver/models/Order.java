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
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order implements Comparable<Order>
{
    @Transient
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customer;

    private Date order_date;

    private Date arrival_date;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();

    @Transient
    private Float totalCost;

    public Date getOrderDateObject()
    {
        return order_date;
    }

    @Override
    public int compareTo(Order order)
    {
        return order_date.compareTo(order.getOrderDateObject());
    }

    public String getOrder_date()
    {
        return DATE_FORMATTER.format(order_date);
    }

    public String getArrival_date()
    {
        if (arrival_date == null)
        {
            return "null";
        }
        return DATE_FORMATTER.format(arrival_date);
    }

    public String getTotalCost()
    {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        Float totalCost = Float.valueOf(0);

        for (Product product : products)
        {
            totalCost += product.getPrice();
        }

        return df.format(totalCost);
    }
}
