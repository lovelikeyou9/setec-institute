
package com.example.demo.repository;

import com.example.demo.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

// [ JpaRepository ] is used to work with a database through your Entity. It gives you many database
// operations without having to write SQL yourself.
public interface StaffRepository extends JpaRepository<Staff, Long>{

}
