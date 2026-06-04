package com.Neeloo.UserAuthService.services;



import com.Neeloo.UserAuthService.exceptions.IncorrectPasswordException;
import com.Neeloo.UserAuthService.exceptions.UserAlreadyExistException;
import com.Neeloo.UserAuthService.exceptions.UserNotRegisteredException;
import com.Neeloo.UserAuthService.models.Role;
import com.Neeloo.UserAuthService.models.State;
import com.Neeloo.UserAuthService.models.User;
import com.Neeloo.UserAuthService.repository.RoleRepo;
import com.Neeloo.UserAuthService.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;
    @Override
    public User signup(String name, String email, String password) {
        // TODO Auto-generated method stub

        //Check uniqueness

        Optional<User> optionalUser = userRepo.findByEmail(email);

        if(optionalUser.isPresent()){
            throw new UserAlreadyExistException("User with email " + email + " already exists");
        }

        //Create new user

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        user.setState(State.ACTIVE);

        //Assign roles

        Role role = null;
        Optional<Role> optionalRole = roleRepo.findByValue("DEFAULT");

        if(optionalRole.isEmpty()){
            role = new Role();
            role.setValue("DEFAULT");
            role.setUpdatedAt(System.currentTimeMillis());
            role.setCreatedAt(System.currentTimeMillis());
            role.setState(State.ACTIVE);
            roleRepo.save(role);
        }else{
            role = optionalRole.get();
        }

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        user.setRoles(roles);

        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
        // TODO Auto-generated method stub

        //Fetch user
        Optional<User> optionalUser = userRepo.findByEmail(email);

        //Check if user exists
        if(optionalUser.isEmpty()){
            throw new UserNotRegisteredException("User with email " + email + " does not exist");
        }

        User user = optionalUser.get();

        //Check if passowrd is correct
        if(!password.equals(user.getPassword())){
            throw new IncorrectPasswordException("Invalid credentials");
        }

        return user;
    }

    @Override
    public Boolean validateToken(String token) {
        return null;
    }
}

/*
Today's was most basic implementation for signup and login
 */