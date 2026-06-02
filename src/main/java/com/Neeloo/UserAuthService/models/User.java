package com.Neeloo.UserAuthService.models;


import com.Neeloo.UserAuthService.dtos.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends BaseModel{
    private String name;
    private String email;
    private String password;
    @ManyToMany
    private List<Role> roles = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public UserDTO toUserDTO(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(this.getId());
        userDTO.setName(this.getName());
        userDTO.setEmail(this.getEmail());
        userDTO.setRoles(this.getRoles());
        return userDTO;
    }
}


/*

A  B
1  1/M
1/M    1



User Role
1     M
M     1

M : M


Role?


RBAC - Role based access control

Roles
SDE(L4)
SDE(L5)
PMM
PM


user can have multiple roles


users

id name email password

roles

id value


user_role

user_id role_id
 */