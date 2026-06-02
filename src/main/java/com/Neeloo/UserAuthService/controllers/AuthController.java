package com.Neeloo.UserAuthService.controllers;


import com.Neeloo.UserAuthService.dtos.LoginRequestDto;
import com.Neeloo.UserAuthService.dtos.SignupRequestDTO;
import com.Neeloo.UserAuthService.dtos.UserDTO;
import com.Neeloo.UserAuthService.models.User;
import com.Neeloo.UserAuthService.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
1. Signup API: POST
    Return: ResponseEntity<UserDTO>. Why not ResponseEntity<User>? Because we don't want to return the password in the response. So we will create a UserDTO class which will have all the fields of User except the password.


2. Login API: POST , Request: payload (email, password)
    Return: ResponseEntity<String> (JWT token)

We will maintain sessions for all users.
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignupRequestDTO signupRequestDTO){
        try{
            User user = authService.signup(
                    signupRequestDTO.getName(),
                    signupRequestDTO.getEmail(),
                    signupRequestDTO.getPassword());
            UserDTO userDTO = user.toUserDTO();
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }catch (Exception e){
            throw e;
        }
    }


    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDto loginRequestDto){
        try{
            User user = authService.login(
                    loginRequestDto.getEmail(),
                    loginRequestDto.getPassword());
            UserDTO userDTO = user.toUserDTO();
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }catch (Exception e){
            throw e;
        }
    }

}
/*
JWTs
Tokens
 */