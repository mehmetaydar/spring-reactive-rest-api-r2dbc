package com.reactive.examples.service;

import com.reactive.examples.dto.UserDepartmentDTO;
import com.reactive.examples.model.Department;
import com.reactive.examples.model.User;
import com.reactive.examples.repository.DepartmentRepository;
import com.reactive.examples.repository.UserPaginationRepository;
import com.reactive.examples.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Services could be think of 'workers' in Spring Rest APIs
 * Business logic with data accesses are encapsulated in the service logic
 */

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPaginationRepository userPaginationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Mono<User> createUser(User user){
        return userRepository.save(user);
    }

    public Flux<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Mono<Page<User>> getUsers(PageRequest pageRequest){
        return this.userPaginationRepository.findAllBy(pageRequest)
                .collectList()
                .zipWith(this.userPaginationRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
    }

    public Mono<User> findById(Integer userId){
        return userRepository.findById(userId);
    }

    //We heavily use Java Streams API for code readability
    public Mono<User> updateUser(Integer userId,  User user){
        return userRepository.findById(userId)
                .flatMap(dbUser -> {
                    dbUser.setAge(user.getAge());
                    dbUser.setSalary(user.getSalary());
                    dbUser.setName(user.getName());
                    dbUser.setEmail(user.getEmail());
                    return userRepository.save(dbUser);
                });
    }

    public Mono<User> deleteUser(Integer userId){
        return userRepository.findById(userId)
                .flatMap(existingUser -> userRepository.delete(existingUser)
                .then(Mono.just(existingUser)));
    }

    public Flux<User> findUsersByAge(int age){
        return userRepository.findByAge(age);
    }

    //Here the db lookup uses parallel processing
    public Flux<User> fetchUsers(List<Integer> userIds) {
        return Flux.fromIterable(userIds)
                .parallel()
                .runOn(Schedulers.elastic())
                .flatMap(i -> findById(i))
                .ordered((u1, u2) -> u2.getId() - u1.getId());
    }

    private Mono<Department> getDepartmentByUserId(Integer userId){
        return departmentRepository.findByUserId(userId);
    }

    //Here we combine data from two different tables
    public Mono<UserDepartmentDTO> fetchUserAndDepartment(Integer userId){
        Mono<User> user = findById(userId).subscribeOn(Schedulers.elastic());
        Mono<Department> department = getDepartmentByUserId(userId).subscribeOn(Schedulers.elastic());
        return Mono.zip(user, department, userDepartmentDTOBiFunction);
    }

    private BiFunction<User, Department, UserDepartmentDTO> userDepartmentDTOBiFunction = (x1, x2) -> UserDepartmentDTO.builder()
            .age(x1.getAge())
            .departmentId(x2.getId())
            .departmentName(x2.getName())
            .userName(x1.getName())
            .userId(x1.getId())
            .loc(x2.getLoc())
            .salary(x1.getSalary()).build();

}
