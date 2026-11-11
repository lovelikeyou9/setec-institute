
package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// [ @Entity ] tells JPA/Hibernate that a Java class represents a table in your database.
@Entity
public class Staff {

    @Id
    // [ @Id ] tells JPA/Hibernate which field is the primary key of your database table.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // [ GenerationType ] tells JPA/Hibernate how to generate the value of a field marked with @GeneratedValue,
    // usually the primary key.

    // [ @GeneratedValue ] is used to tell JPA/Hibernate how to automatically generate the value of a primary key, usually an [ id ].
    private Long SId;
    private String userName;
    private String password;

    public Staff() {}
    // A constructor is used when Java creates a new object.

    public Staff(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Long getSId() { return SId; }
    public void setSId(Long SId) { this.SId = SId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
