package com.reactive.examples.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.*;

/**
 * User model which we persist in the database in the users table
 * Models are consumed by Spring Repository
 * We use lombok annotations for easier class setup
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class User {

    @Id
    @ApiModelProperty(hidden = true)
    private Integer id;

    @NotBlank(message = "Name is mandatory")
    @NotNull(message = "Name cannot be null")
    private String name;

    @Min(value = 1, message = "Age should not be less than 1")
    @Max(value = 150, message = "Age should not be greater than 150")
    private int age;

    private double salary;

    @NotBlank(message = "Email is mandatory")
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
}
