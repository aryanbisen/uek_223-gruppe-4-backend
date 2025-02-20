package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserNameDTO {
    private User eventCreator;
    private String firstName;
    private String lastName;

    public UserNameDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
