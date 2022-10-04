package com.reactive.examples.repository;

import com.reactive.examples.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * Java Spring JPA UserRepository
 * Serves as a data repository that supports non-blocking reactive streams for the users table
 * Please notice the usage of 'Flux' here as part of the non-blocking operations
 * CRUD operations (create, update, delete) are supported automatically as it extends ReactiveCrudRepository
 * Additional customer queries are added
 */

public interface UserRepository extends ReactiveCrudRepository<User,Integer> {
    @Query("select * from users where age >= $1")
    Flux<User> findByAge(int age);
}
