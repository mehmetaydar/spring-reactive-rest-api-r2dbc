package com.usermanagement.reactive.controller;

import com.usermanagement.reactive.dto.UserDepartmentDTO;
import com.usermanagement.reactive.model.User;
import com.usermanagement.reactive.service.UserService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * User RestController that maps operations on User entities to "/users" rest end point Controllers
 * has associated service(s) that perform specific operations For UserController it uses the
 * userService which is autowired (automatically assigned to UserService Bean) When we need to
 * return a single resource we use 'Mono' When we need to return a collections of resources we use
 * 'Flux'
 */
@RestController
@RequestMapping("/users")
public class UserController {
  @Autowired private UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<User> create(@Valid @RequestBody User user) {
    return userService.createUser(user);
  }

  // Get all users without pagination and sorting
  @GetMapping
  public Flux<User> getAllUsers() {
    return userService.getAllUsers();
  }

  // Get users with pagination and sorting
  @GetMapping("all")
  public Mono<Page<User>> getAllUsersWithPaginationAndSorting(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "sort", defaultValue = "id;ASC,name;DESC", required = false)
          String[] sortBy) {

    Sort allSorts =
        Sort.by(
            Arrays.stream(sortBy)
                .map(sort -> sort.split(";", 2))
                .map(
                    array ->
                        new Sort.Order(replaceOrderStringThroughDirection(array[1]), array[0])
                            .ignoreCase())
                .collect(Collectors.toList()));

    return userService.getUsers(PageRequest.of(page, size, allSorts));
  }

  private Sort.Direction replaceOrderStringThroughDirection(String sortDirection) {
    if (sortDirection.equalsIgnoreCase("DESC")) {
      return Sort.Direction.DESC;
    } else {
      return Sort.Direction.ASC;
    }
  }

  @GetMapping("/{userId}")
  public Mono<ResponseEntity<User>> getUserById(@PathVariable Integer userId) {
    Mono<User> user = userService.findById(userId);
    return user.map(u -> ResponseEntity.ok(u)).defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PutMapping("/{userId}")
  public Mono<ResponseEntity<User>> updateUserById(
      @PathVariable Integer userId, @Valid @RequestBody User user) {
    return userService
        .updateUser(userId, user)
        .map(updatedUser -> ResponseEntity.ok(updatedUser))
        .defaultIfEmpty(ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/{userId}")
  public Mono<ResponseEntity<Void>> deleteUserById(@PathVariable Integer userId) {
    return userService
        .deleteUser(userId)
        .map(r -> ResponseEntity.ok().<Void>build())
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("/age/{age}")
  public Flux<User> getUsersByAgeGreater(@PathVariable int age) {
    return userService.findUsersByAge(age);
  }

  @PostMapping("/search/id")
  public Flux<User> fetchUsersByIds(@RequestBody List<Integer> ids) {
    return userService.fetchUsers(ids);
  }

  @GetMapping("/{userId}/department")
  public Mono<UserDepartmentDTO> fetchUserAndDepartment(@PathVariable Integer userId) {
    return userService.fetchUserAndDepartment(userId);
  }

  @GetMapping("search")
  public Flux<User> searchUsersByEmailOrName(
      @RequestParam(value = "email", defaultValue = "") String email,
      @RequestParam(value = "name", defaultValue = "") String name) {
    return userService.findUsersByEmailOrName(email, name);
  }
}
