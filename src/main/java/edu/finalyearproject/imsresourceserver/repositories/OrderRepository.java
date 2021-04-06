/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.repositories;

import edu.finalyearproject.imsresourceserver.models.Customer;
import edu.finalyearproject.imsresourceserver.models.Order;
import edu.finalyearproject.imsresourceserver.models.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository to perform auto-generated queries on the Order table in the database.
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, Integer>
{
    List<Order> findAll();
    Order findByid(int id);
    List<Order> findBycustomer(Customer customer);
    List<Order> findByproducts(Product product);
}
