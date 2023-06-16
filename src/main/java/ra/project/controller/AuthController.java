package ra.project.controller;


import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import ra.project.dto.request.SignInForm;
import ra.project.dto.request.SignUpForm;
import ra.project.dto.response.JwtResponse;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.Order;
import ra.project.model.Role;
import ra.project.model.RoleName;
import ra.project.model.User;
import ra.project.security.jwt.JwtProvider;
import ra.project.security.userPrincipal.UserPrincipal;
import ra.project.service.IService.IOrderService;
import ra.project.service.IService.IRoleService;
import ra.project.service.IService.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private  final IOrderService orderService;


    @PostMapping("/signUp")
    public ResponseEntity<ResponseMessage> doSignUp(@Validated @RequestBody SignUpForm signUpForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid Input!")
                            .data("")
                            .build()
            );
        }
        boolean isExistUsername = userService.existsByUsername(signUpForm.getUsername());
        if (isExistUsername) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("This username is already existed!")
                            .data("")
                            .build()
            );
        }
        boolean isExistPhoneNumber = userService.existsByEmail(signUpForm.getEmail());
        if (isExistPhoneNumber) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("This email is already existed!")
                            .data("")
                            .build()
            );
        }
        Set<Role> roles = new HashSet<>();

        if (signUpForm.getRoles() == null || signUpForm.getRoles().isEmpty()) {
            Role role = roleService.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Failed -> NOT FOUND ROLE"));
            roles.add(role);
        } else {
            signUpForm.getRoles().forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleService.findByName(RoleName.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Failed -> NOT FOUND ROLE"));
                        roles.add(adminRole);
                    case "pm":
                        Role pmRole = roleService.findByName(RoleName.PM)
                                .orElseThrow(() -> new RuntimeException("Failed -> NOT FOUND ROLE"));
                        roles.add(pmRole);
                    case "user":
                        Role userRole = roleService.findByName(RoleName.USER)
                                .orElseThrow(() -> new RuntimeException("Failed -> NOT FOUND ROLE"));
                        roles.add(userRole);
                }
            });
        }

        User user = User.builder()
                .fullName(signUpForm.getFullName())
                .username(signUpForm.getUsername())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .email(signUpForm.getEmail())
                .roles(roles)
                .build();
        //creat Order follow User
        orderService.save(Order.builder().user(user).build());
        return ResponseEntity.ok().body(
                ResponseMessage.builder()
                        .status("OK")
                        .message("Account created successfully!")
                        .data(userService.save(user))
                        .build()
        );
    }


    @PostMapping("/signIn")
    public ResponseEntity<?> doSignIn(@Validated @RequestBody SignInForm signInForm,BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid Input!")
                            .data("")
                            .build()
            );
        }
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            signInForm.getUsername(),
                            signInForm.getPassword())
                    );

            String token = jwtProvider.generateToken(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return new ResponseEntity<>(
                    JwtResponse.builder()
                            .status("OK")
                            .type("Bearer")
                            .fullName(userPrincipal.getFullName())
                            .token(token)
                            .roles(userPrincipal.getAuthorities())
                            .build(), HttpStatus.OK);

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(
                    ResponseMessage.builder()
                            .status("Failed")
                            .message("Invalid username or password!")
                            .data("")
                            .build(), HttpStatus.UNAUTHORIZED);
        }
    }
}
