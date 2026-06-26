package com.clothshop.billing.repository;

import com.clothshop.billing.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
