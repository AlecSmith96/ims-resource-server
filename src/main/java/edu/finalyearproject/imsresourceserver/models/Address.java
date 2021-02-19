package edu.finalyearproject.imsresourceserver.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "addresses")
@Data
public class Address
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private int house_number;
    private String line_1;
    private String line_2;
    private String city;
    private String county;
    private String post_code;
}
