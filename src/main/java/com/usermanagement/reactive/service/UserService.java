package com.usermanagement.reactive.service;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;
import static org.springframework.data.domain.ExampleMatcher.matchingAny;

import com.usermanagement.reactive.dto.UserDepartmentDTO;
import com.usermanagement.reactive.model.Department;
import com.usermanagement.reactive.model.User;
import com.usermanagement.reactive.repository.DepartmentRepository;
import com.usermanagement.reactive.repository.UserPaginationRepository;
import com.usermanagement.reactive.repository.UserRepository;
import com.usermanagement.reactive.repository.UserSearchRepository;
import com.usermanagement.reactive.utilities.TextSanitizer;
import java.util.List;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Services could be think of 'workers' in Spring Rest APIs Business logic with data accesses are
 * encapsulated in the service logic
 */
@Service
@Slf4j
@Transactional
public class UserService {

  @Autowired private UserRepository userRepository;

  @Autowired private UserPaginationRepository userPaginationRepository;

  @Autowired private UserSearchRepository userSearchRepository;

  @Autowired private DepartmentRepository departmentRepository;

  public Mono<User> createUser(User user) {
    return userRepository.save(user);
  }

  public Flux<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Mono<Page<User>> getUsers(PageRequest pageRequest) {
    return this.userPaginationRepository
        .findAllBy(pageRequest)
        .collectList()
        .zipWith(this.userPaginationRepository.count())
        .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
  }

  public Mono<User> findById(Integer userId) {
    return userRepository.findById(userId);
  }

  // We heavily use Java Streams API for code readability
  public Mono<User> updateUser(Integer userId, User user) {
    return userRepository
        .findById(userId)
        .flatMap(
            dbUser -> {
              dbUser.setAge(user.getAge());
              dbUser.setSalary(user.getSalary());
              dbUser.setName(user.getName());
              dbUser.setEmail(user.getEmail());
              return userRepository.save(dbUser);
            });
  }

  public Mono<User> deleteUser(Integer userId) {
    return userRepository
        .findById(userId)
        .flatMap(existingUser -> userRepository.delete(existingUser).then(Mono.just(existingUser)));
  }

  public Flux<User> findUsersByAge(int age) {
    return userRepository.findByAge(age);
  }

  // Here the db lookup uses parallel processing
  public Flux<User> fetchUsers(List<Integer> userIds) {
    return Flux.fromIterable(userIds)
        .parallel()
        .runOn(Schedulers.elastic())
        .flatMap(i -> findById(i))
        .ordered((u1, u2) -> u2.getId() - u1.getId());
  }

  private Mono<Department> getDepartmentByUserId(Integer userId) {
    return departmentRepository.findByUserId(userId);
  }

  // Here we combine data from two different tables
  public Mono<UserDepartmentDTO> fetchUserAndDepartment(Integer userId) {
    Mono<User> user = findById(userId).subscribeOn(Schedulers.elastic());
    Mono<Department> department = getDepartmentByUserId(userId).subscribeOn(Schedulers.elastic());
    return Mono.zip(user, department, userDepartmentDTOBiFunction);
  }

  private BiFunction<User, Department, UserDepartmentDTO> userDepartmentDTOBiFunction =
      (x1, x2) ->
          UserDepartmentDTO.builder()
              .age(x1.getAge())
              .departmentId(x2.getId())
              .departmentName(x2.getName())
              .userName(x1.getName())
              .userId(x1.getId())
              .loc(x2.getLoc())
              .salary(x1.getSalary())
              .build();

  public Flux<User> findUsersByEmailOrName(String email, String name) {
    User user = new User();
    if (email != null && !email.equals("")) user.setEmail(TextSanitizer.sanitize(email));
    if (name != null && !name.equals("")) user.setName(TextSanitizer.sanitize(name));
    else user.setName("DidThisBecauseofAnErrorInTheLibrary.TherewontbeanynameLikeThis");
    ExampleMatcher matcher =
        matchingAny()
            .withIgnoreCase()
            .withMatcher("name", startsWith())
            .withIgnorePaths("age")
            .withIgnorePaths("salary")
            .withIgnorePaths("id");
    Example<User> searchCriteriaExample = Example.of(user, matcher);
    return userSearchRepository.findAll(searchCriteriaExample);
  }
}
