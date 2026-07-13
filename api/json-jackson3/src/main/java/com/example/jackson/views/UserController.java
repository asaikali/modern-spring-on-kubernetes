package com.example.jackson.views;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {

  private final User testUser =
      new User().setEmail("adib@exmaple.com").setUsername("adib").setSecretNote("secretNote");

  // returns public view only
  @GetMapping("/user/public")
  @JsonView(Views.Public.class)
  public User getUserPublic() {
    return testUser;
  }

  // return the internal view
  @GetMapping("/user/internal")
  @JsonView(Views.Internal.class)
  public User getUserInternal() {
    return testUser;
  }

  // input binding using Public view (username only)
  @PostMapping("/user/register")
  public ResponseEntity<String> registerUser(@RequestBody @JsonView(Views.Public.class) User user) {
    return ResponseEntity.ok(
        "Registered user: " + user.getUsername() + ", secret note: " + user.getSecretNote());
  }

  // input binding using Internal view (username + password)
  @PostMapping("/user/internal")
  public ResponseEntity<String> internalUser(
      @RequestBody @JsonView(Views.Internal.class) User user) {
    return ResponseEntity.ok(
        "Internal user: " + user.getUsername() + ", secretNote: " + user.getSecretNote());
  }
}
