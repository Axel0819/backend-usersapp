package com.axel.backend.usersapp.backendusersapp.controllers;

import com.axel.backend.usersapp.backendusersapp.models.entities.User;
import com.axel.backend.usersapp.backendusersapp.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService service;

    @GetMapping
    public List<User> list(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){
        Optional<User> userOptional = service.findById(id);

        if(userOptional.isPresent()) return ResponseEntity.ok(userOptional.orElseThrow());

        return ResponseEntity.notFound().build();
    }
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult validationResult){
        if(validationResult.hasErrors()){
            return validation(validationResult);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@Valid @RequestBody User user, BindingResult validationResult, @PathVariable Long id){
        if(validationResult.hasErrors()){
            return validation(validationResult);
        }

        Optional<User> userOptional = service.update(user, id);

        if (userOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(userOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id){
        Optional<User> userOptional = service.findById(id);

        if (userOptional.isPresent()){
            service.remove(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    //Custom method to collect all errors
    private ResponseEntity<?> validation(BindingResult validationResult) {
        Map<String, String> errors = new HashMap<>();

        validationResult.getFieldErrors().forEach(err->{
            errors.put(err.getField(), err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
