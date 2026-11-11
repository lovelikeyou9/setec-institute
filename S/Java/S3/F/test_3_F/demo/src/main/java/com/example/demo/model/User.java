
// The [ model ] folder contains classes that represent the data in your application, often corresponding
// to database tables.
// This represents a User in your application and, with @Entity, can represent a database table.
// Model = represents the application's data/entities, such as User, Product, and Category.

package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// [ @Entity ] tells JPA/Hibernate that this Java class represents a database table.
// means Hibernate will treat [ User ] as a database entity and, normally, create/update a table for it.
@Entity
public class User {

    // [ @Id ] tells JPA/Hibernate which field is the Primary Key (PK) of the database table.
    // [ id uniquely identifies each User. ]
    @Id
    // [ @GeneratedValue ] tells JPA/Hibernate how to automatically generate the value of the primary key (id).
    // [ GenerationType ] tells JPA which strategy to use for generating the primary key (id) automatically.
    // [ IDENTITY ] means the database automatically generates the ID value when you insert a new record.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [ @Column ] is used to configure how a Java field is stored as a database column.
    @Column(unique = true, nullable = false)
    private String userName;

    private String password;
    private String role;

    public User() {}

    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
