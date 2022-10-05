package com.usermanagement.reactive.repository;

import com.usermanagement.reactive.model.User;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Java Spring JPA UserRepository Serves as a data repository that supports non-blocking reactive
 * streams for the users table Please notice the usage of 'Flux' here as part of the non-blocking
 * operations This repository is for searching
 */
@Repository
public interface UserSearchRepository
    extends ReactiveCrudRepository<User, Integer>, ReactiveQueryByExampleExecutor<User> {}
