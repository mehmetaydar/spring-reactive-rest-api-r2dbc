package com.reactive.examples.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Department model which we persist in the database in the department table
 * Models are consumed by Spring Repository
 * We use lombok annotations for easier class setup
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("department")
public class Department {
    @Id
    @ApiModelProperty(hidden = true)
    private Integer id;
    private String name;
    @Column("user_id")
    private Integer userId;
    private String loc;
}
