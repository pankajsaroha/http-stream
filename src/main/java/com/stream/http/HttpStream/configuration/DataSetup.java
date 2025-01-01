package com.stream.http.HttpStream.configuration;

import com.stream.http.HttpStream.model.Employee;
import com.stream.http.HttpStream.repository.EmployeeRepository;
import com.stream.http.HttpStream.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class DataSetup {

    @Autowired
    private EmployeeService employeeService;

    private AtomicInteger atomicInteger;

    @PostConstruct
    public void initialDataSetup () {
        List<Employee> employees = new ArrayList<>();
        atomicInteger = new AtomicInteger(0);

        for (int i = 0; i < 500; i++) {
            addPerson(employees);
        }
        employeeService.save(employees);
    }

    public void addPerson (List<Employee> employees) {
        Random rand = new Random();
        employees.add(Employee.builder()
                .employeeId(nextInt())
                .age(20 + rand.nextInt(10))
                .salary(20000 + (int) (rand.nextDouble(20) * 200))
                .build());
    }

    private int nextInt() {
        return atomicInteger.incrementAndGet();
    }
}
