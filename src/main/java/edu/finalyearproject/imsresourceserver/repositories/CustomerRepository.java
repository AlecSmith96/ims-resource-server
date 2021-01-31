package edu.finalyearproject.imsresourceserver.repositories;

import edu.finalyearproject.imsresourceserver.models.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer>
{
    List<Customer> findAll();
}
