package com.example.jackson.views;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final User testUser = new User("adib", "adib@example.com", "secret item");

  @GetMapping("/user/public")
  @JsonView(Views.Public.class)
  public User getUserPublic() {
    return testUser;
  }

  @GetMapping("/user/internal")
  @JsonView(Views.Internal.class)
  public User getUserInternal() {
    return testUser;
  }
}
