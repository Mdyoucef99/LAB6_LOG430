package com.lab5.customer.api;

import com.lab5.customer.domain.Customer;
import com.lab5.customer.infrastructure.CustomerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerRestController {
    private final CustomerDao customerDao;

    @Autowired
    public CustomerRestController(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            customerDao.addCustomer(customer);
            return ResponseEntity.ok(customer);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            return ResponseEntity.ok(customerDao.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
} 