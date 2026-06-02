package com.Neeloo.UserAuthService.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class Role extends BaseModel{
    private String value;

//    @ManyToMany(mappedBy = "roles")
//    private List<User> users;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}