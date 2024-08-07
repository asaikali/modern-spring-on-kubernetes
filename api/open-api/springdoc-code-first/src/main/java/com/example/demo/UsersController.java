package com.example.demo;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

  @PostMapping("add")
  String addUser(String username) {
    return "User added: " + username;
  }

  @GetMapping("all")
  List<String> getUsers() {
    return List.of("user1", "user2", "user3");
  }
}
