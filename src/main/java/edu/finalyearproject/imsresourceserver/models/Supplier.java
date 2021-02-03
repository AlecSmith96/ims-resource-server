package edu.finalyearproject.imsresourceserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "suppliers")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Supplier
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;
}
