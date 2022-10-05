package com.usermanagement.reactive.repository;

import com.usermanagement.reactive.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/** similar to UserRepository but this supports pagination and sorting */
@Repository
public interface UserPaginationRepository extends ReactiveSortingRepository<User, Integer> {
  Flux<User> findAllBy(Pageable pageable);
}
