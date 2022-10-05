package com.usermanagement.reactive.initialize;

import com.usermanagement.reactive.model.Department;
import com.usermanagement.reactive.model.User;
import com.usermanagement.reactive.repository.DepartmentRepository;
import com.usermanagement.reactive.repository.UserRepository;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Profile("!test")
@Slf4j
public class UserInitializer implements CommandLineRunner {

  @Autowired private UserRepository userRepository;

  @Autowired private DepartmentRepository departmentRepository;

  @Override
  public void run(String... args) {
    initialDataSetup();
  }

  private List<User> getData() {
    return Arrays.asList(
        new User(null, "Suman Das", 30, 10000, "sumandas@company.com"),
        new User(null, "Arjun Das", 5, 1000, "arjundas@company.com"),
        new User(null, "Saurabh Ganguly", 40, 1000000, "sganguly@company.com"));
  }

  private List<Department> getDepartments() {
    return Arrays.asList(
        new Department(null, "Mechanical", 1, "Mumbai"),
        new Department(null, "Computer", 2, "Bangalore"));
  }

  private void initialDataSetup() {
    userRepository
        .deleteAll()
        .thenMany(Flux.fromIterable(getData()))
        .flatMap(userRepository::save)
        .thenMany(userRepository.findAll())
        .subscribe(
            user -> {
              log.info("User Inserted from CommandLineRunner " + user);
            });

    departmentRepository
        .deleteAll()
        .thenMany(Flux.fromIterable(getDepartments()))
        .flatMap(departmentRepository::save)
        .thenMany(departmentRepository.findAll())
        .subscribe(
            user -> {
              log.info("Department Inserted from CommandLineRunner " + user);
            });
  }
}