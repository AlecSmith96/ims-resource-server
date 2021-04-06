/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.repositories;

import edu.finalyearproject.imsresourceserver.models.Supplier;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * JPA Repository to perform auto-generated queries on the Supplier table in the database.
 */
public interface SupplierRepository extends CrudRepository<Supplier, Integer>
{
    List<Supplier> findAll();

    Supplier findByname(String name);
}
