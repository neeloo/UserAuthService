package com.Neeloo.UserAuthService.controllers;


import com.Neeloo.UserAuthService.dtos.LoginRequestDto;
import com.Neeloo.UserAuthService.dtos.SignupRequestDTO;
import com.Neeloo.UserAuthService.dtos.UserDTO;
import com.Neeloo.UserAuthService.dtos.UserToken;
import com.Neeloo.UserAuthService.exceptions.UnauthorizedException;
import com.Neeloo.UserAuthService.models.User;
import com.Neeloo.UserAuthService.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
            UserToken userToken = authService.login(
                    loginRequestDto.getEmail(),
                    loginRequestDto.getPassword());
            UserDTO userDTO = userToken.getUser().toUserDTO();
            String token = userToken.getToken();

            MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
            headers.add(HttpHeaders.SET_COOKIE, token);

            HttpHeaders httpHeaders = new HttpHeaders(headers);

            return new ResponseEntity<>(userDTO, httpHeaders, HttpStatus.OK);
        }catch (Exception e){
            throw e;
        }
    }


    @PostMapping("/validate-token")
    public void validateToken(@RequestBody String token){
        Boolean res = authService.validateToken(token);
        if(!res){
            throw new UnauthorizedException("Invalid token");
        }
    }

}
/*
JWTs
Tokens
 */