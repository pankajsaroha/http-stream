package com.stream.http.HttpStream.controller;

import com.stream.http.HttpStream.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/http-stream")
public class HttpStreamController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/streamed-data")
    public ResponseEntity<StreamingResponseBody> getEmployeesData (final HttpServletResponse response) {
        return ResponseEntity.ok(employeeService.getEmployees(response));
    }
}
