/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.repositories;

import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository to perform auto-generated queries on the Purchase table in the database.
 */
@Repository
public interface PurchaseRepository extends CrudRepository<Purchase, Integer>
{
    List<Purchase> findAll();
    Purchase findByid(int id);
    List<Purchase> findBysupplier(Supplier supplier);
    List<Purchase> findByproducts(Product product);

    @Override
    <S extends Purchase> S save(S s);
}
