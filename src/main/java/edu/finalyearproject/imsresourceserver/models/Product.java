package edu.finalyearproject.imsresourceserver.models;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="supplier_id", nullable=false)
    private Supplier supplier;
}
