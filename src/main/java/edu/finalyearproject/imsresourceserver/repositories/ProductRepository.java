package edu.finalyearproject.imsresourceserver.repositories;

import edu.finalyearproject.imsresourceserver.models.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer>
{
    List<Product> findAll();
    Product findByname(String name);
    @Override
    <S extends Product> S save(S s);
    Optional<Product> findById(Integer id);
    Product findBysku(Integer sku);

    @Override
    void deleteById(Integer integer);
}
