package edu.finalyearproject.imsresourceserver.repositories;

import edu.finalyearproject.imsresourceserver.models.Product;
import edu.finalyearproject.imsresourceserver.models.Supplier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer>
{
    List<Product> findAll();

    List<Product> findBysupplier(Supplier supplier);
    Product findByname(String name);
    Optional<Product> findById(Integer id);
    Product findBysku(Integer sku);

    @Override
    <S extends Product> S save(S s);

    @Override
    void deleteById(Integer integer);
}
