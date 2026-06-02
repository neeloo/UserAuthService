package com.Neeloo.UserAuthService.services;


import com.Neeloo.UserAuthService.models.User;

public interface IAuthService {
    User signup(String name, String email, String password);
    User login(String email,String password);
}