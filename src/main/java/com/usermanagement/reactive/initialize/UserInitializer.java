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
        new User(null, "Jordan Sun", 30, 10000, "jordansun@company.com"),
        new User(null, "Satoshi Nakamoto", 5, 1000, "satoshi@company.com"),
        new User(null, "Vitalik Bullet", 40, 1000000, "vitalik@company.com"),
        new User(null, "David Cameron", 20, 10000, "dcameron@company.com"),
        new User(null, "Albert Einstein", 55, 1000, "albeins@company.com"),
        new User(null, "Martin Mangan", 35, 1000000, "martman@company.com"),
        new User(null, "Felipe Melo", 40, 10000, "felipmelo@company.com"),
        new User(null, "Lionel Messi", 35, 1000, "liomes@company.com"),
        new User(null, "Christiano Ronaldo", 36, 1000000, "chrisr@company.com"),
        new User(null, "Wayne Rooney", 40, 10000, "rooney@company.com"),
        new User(null, "Diego Maradona", 50, 1000, "maradona@company.com"),
        new User(null, "Zehra Gunes", 25, 1000000, "zgunes@company.com"),
        new User(null, "Lebron James", 34, 10000, "ljames@company.com"),
        new User(null, "Michael Jordan", 60, 1000, "mjordan@company.com"),
        new User(null, "Bruma Randy", 45, 1000000, "brumarand@company.com"));
  }

  private List<Department> getDepartments() {
    return Arrays.asList(
        new Department(null, "Engineering", 1, "Berlin"),
        new Department(null, "Marketing", 2, "Cleveland"));
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
