package com.stream.http.HttpStream.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stream.http.HttpStream.model.Employee;
import com.stream.http.HttpStream.repository.EmployeeRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    public ResponseEntity<String> save (List<Employee> employees) {
        try {
            repository.saveAll(employees);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.ok("Data generated successfully");
    }

    @Async
    public CompletableFuture<StreamingResponseBody> getEmployees (final HttpServletResponse response) {

        final int pageSize = 10;
        final Pageable[] pageable = {PageRequest.of(0, pageSize)};
        final StreamingResponseBody stream = out -> {
            boolean firstPage = true;
            int page = 0;

            while (true) {
                Page<Employee> dataPage = repository.findAll(pageable[0]);
                if (dataPage.isEmpty()) {
                    break; // no more data
                }
                //add comma between json chunks
                if (!firstPage) {
                    out.write((",".getBytes()));
                }

                //convert the list to json as client expect data in this format. Raw byte won't be interpretable and client and server should have a predefined deserialization mechanism
                String jsonData = convertDataToJson(dataPage.getContent());
                out.write(jsonData.getBytes());
                out.flush();
                firstPage = false;
                pageable[0] = PageRequest.of(++page, pageSize);
                log.info("Current Thread: " + Thread.currentThread().getName());
                //Added wait to show the streaming as data stored is not huge
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        return CompletableFuture.completedFuture(stream);
    }

    private String convertDataToJson (List<Employee> data) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(data);
    }
}
