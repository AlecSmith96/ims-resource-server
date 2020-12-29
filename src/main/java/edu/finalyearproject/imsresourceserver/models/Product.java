package edu.finalyearproject.imsresourceserver.models;

import lombok.Data;

import javax.persistence.*;

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
    private Integer inventory_on_hand;
    private Integer reorder_threshold;
}
