package edu.finalyearproject.imsresourceserver.repositories;

import edu.finalyearproject.imsresourceserver.models.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Integer>
{
    Address findByid(int address_id);
}
