package com.example.demo.core.security.permissionevaluators;

import com.example.demo.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionEvaluator {



  public boolean isUserAboveAge(User principal, int age) {
    // change this to something more usefully
    return age > 0 && principal != null;
  }

}
