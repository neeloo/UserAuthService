package com.Neeloo.UserAuthService.services;

import com.Neeloo.UserAuthService.dtos.UserToken;
import com.Neeloo.UserAuthService.exceptions.UserAlreadyExistException;
import com.Neeloo.UserAuthService.models.Role;
import com.Neeloo.UserAuthService.models.State;
import com.Neeloo.UserAuthService.models.User;
import com.Neeloo.UserAuthService.repository.RoleRepo;
import com.Neeloo.UserAuthService.repository.SessionRepo;
import com.Neeloo.UserAuthService.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private SecretKey secretKey;

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
        //Encode the password using encode() method
        user.setPassword(bCryptPasswordEncoder.encode(password));
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
    public UserToken login(String email, String password) {
        /*
        password = raw password
        password in db = encoded password

        matches() method
        BCrypt password encoder is a one-way hashing
        Can you decode the encoded password? No, you cannot decode the encoded password. You can only compare the raw password with the encoded password using the matches() method of BCryptPasswordEncoder. The matches() method takes the raw password and the encoded password as input and returns true if they match, otherwise it returns false.
        matches(rawPassword, encodedPassword)
        1. Extracts the salt from the encoded password
        2. Re-hashes the raw password with the extracted salt
        3. Compares the re-hashed password with the encoded password
         */
        // TODO Auto-generated method stub

        //Fetch user
        Optional<User> optionalUser = userRepo.findByEmail(email);

        //Check if user exists
        if(optionalUser.isEmpty()){
            throw new UserNotRegisteredException("User with email " + email + " does not exist");
        }

        User user = optionalUser.get();

        //Check if passowrd is correct
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new IncorrectPasswordException("Invalid credentials");
        }

        /*
        iat (Issued At): Timestamp of when the token was created.
        exp (Expiry): When the token becomes invalid.
        iss (Issuer): Who created the token (e.g., "scaler").
        userId: Custom claim for identification.
        scope: Roles/Permissions assigned to the user.
         */

        Map<String,Object> payload = new HashMap<>();
        Long nowInMillis = System.currentTimeMillis(); // gets us timestamp in epoch
        System.out.println("nowInMillis = " + nowInMillis);
        payload.put("iat",nowInMillis); //Jan 16 58392
        payload.put("exp",nowInMillis+100000); //100 seconds
        payload.put("userId",user.getId());
        payload.put("iss","scaler");
        payload.put("scope",user.getRoles()); //payload



        String token = Jwts.builder()
                .claims(payload)
                .signWith(secretKey)
                .compact();

        System.out.println("token = " + token);

        //We have generated the token

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setState(State.ACTIVE);
        sessionRepo.save(session);

        return new UserToken(user, token);
    }

    /*
    KISS - Keep It Simple Stupid
     */

    public Boolean validateToken(String token) {
        /*
        Optional step to check if token is in the db
         */

        Optional<Session> optionalSession = sessionRepo.findByToken(token);

        if(optionalSession.isEmpty()){
            System.out.println("Token not found in the database");
            return false;
        }

        /*
        Parsing : If a signature is tampered with, the parser will throw an exception

        token = a.b.c was generated using a secret key
        algo(a,b,secret key) = c (token) prevToken
        Parse this token
        If you want to generate the token again, will you need the same secret key?
        extract a, b, c (originalToken)
        algo(a,b,secret key) = c1 (token) generatedNow
        if match , then return true
         */


        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Long tokenExpiry = (Long) claims.get("exp");
        Long currentTime = System.currentTimeMillis();


        System.out.println("tokenExpiry = " + tokenExpiry);


        if(currentTime > tokenExpiry){
            return false;
        }else{
            return true;
        }
    }
}

/*
Today's was most basic implementation for signup and login

x = 5
f(x) = x % 5

x = 5 , f(x) = 0

f(x) = 0 , x = 0,5,10,15,20,....

f(x) is one way hashing which takes input x and gives output f(x) but we cannot get x from f(x). So we will use hashing for password. We will hash the password and store the hash in the database. When user tries to login, we will hash the password entered by the user and compare it with the hash stored in the database. If they match, then the password is correct.
Salting

"umang"

f(x) = hash("umang") = 12x45tyu
Cost factor = 5

f(12x45tyu) = 137ndshjtyu
f(12x45tyu) = 137ndshjtyu

more CPU itensive
 */