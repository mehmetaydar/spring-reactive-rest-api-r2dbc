package com.reactive.examples.repository;

import com.reactive.examples.model.Department;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Java Spring JPA DepartmentRepository
 * Serves as a data repository that supports non-blocking reactive streams for the department table
 * Please notice the usage of 'Mono' here as part of the non-blocking operations
 * CRUD operations (create, update, delete) are supported automatically as it extends ReactiveCrudRepository
 * Additional customer queries are added
 */

public interface DepartmentRepository extends ReactiveCrudRepository<Department,Integer> {
    Mono<Department> findByUserId(Integer userId);
}
