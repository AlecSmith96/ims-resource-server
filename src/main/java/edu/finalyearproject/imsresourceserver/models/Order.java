package edu.finalyearproject.imsresourceserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customer;

    private String order_date;

    private String arrival_date;

    @ManyToMany(/*mappedBy = "orders",*/ cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();
}
