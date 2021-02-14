package edu.finalyearproject.imsresourceserver.repositories;

import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Purchase;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends CrudRepository<Purchase, Integer>
{
    List<Purchase> findAll();
    Purchase findByid(int id);
    List<Purchase> findBysupplier(int supplier_id);
    List<Purchase> findByproducts(Product product);

    @Override
    <S extends Purchase> S save(S s);
}
