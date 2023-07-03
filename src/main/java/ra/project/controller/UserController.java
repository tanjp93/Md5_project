package ra.project.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.request.UpdateForm;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.User;
import ra.project.security.jwt.JwtProvider;
import ra.project.security.jwt.JwtTokenFilter;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IUserService;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtTokenFilter jwtTokenFilter;
    private final PasswordEncoder passwordEncoder;
    private  final UserDetailService userDetailService;

    @GetMapping("/allUser")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?> findAllUser(Pageable pageable,
                                         @RequestParam("sortBy") String sortBy,
                                         @RequestParam("orderBy") String orderBy
    ) {
        Sort sortable = null;
        if (sortBy == null || sortBy.equals("")) {
            sortBy = "id";
            if (orderBy == null || orderBy.equals("")){
                orderBy = "asc";
            }
        }
        switch (sortBy) {
            case "id":
                if (orderBy.toLowerCase().equals("asc")) {
                    sortable = Sort.by("id").ascending();
                } else {
                    sortable = Sort.by("id").descending();
                }
                break;
            case "fullName":
                if (orderBy.toLowerCase().equals("asc")) {
                    sortable = Sort.by("fullName").ascending();
                } else {
                    sortable = Sort.by("fullName").descending();
                }
                break;
            default:
                sortable = Sort.by("id").ascending();
        }
        pageable = PageRequest.of(pageable.getPageNumber(), 10, sortable);
        return new ResponseEntity<>(userService.findAllUser(pageable), HttpStatus.OK);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> updateUser( @Validated @RequestBody UpdateForm updateUser, BindingResult bindingResult) {
        User userLogin=userDetailService.getCurrentUser();
        if (bindingResult.hasErrors()||userLogin==null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input")
                            .data("")
            );
        }

        try {
            boolean matches = passwordEncoder.matches(updateUser.getOldPassword(), userLogin.getPassword());
            if (!matches) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("Wrong password! ")
                                .data("")
                                .build());
            }
            if (!updateUser.getEmail().equals(userLogin.getEmail())) {
                boolean isExistEmail = userService.existsByEmail(updateUser.getEmail());
                if (isExistEmail) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                            ResponseMessage.builder()
                                    .status("FAILED")
                                    .message("This email is already existed!")
                                    .data("")
                                    .build()
                    );
                }
                userLogin.setEmail(updateUser.getEmail());
            }
            userLogin.setPassword(passwordEncoder.encode(updateUser.getPassword()));
            userLogin.setFullName(updateUser.getFullName());
            userLogin.setAvatar(updateUser.getAvatar());
            userService.save(userLogin);
            logout();
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not Found User ")
                            .data("")
                            .build()
            );
        }
    }
    @GetMapping("/detail")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?> getUserDetail(@RequestBody User user) {
         user = userService.findById(user.getId());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User not found")
                            .data("")
                            .build()
            );
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@RequestBody User user) {
         user = userService.findById(user.getId());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User not found")
                            .data("")
                            .build()
            );
        }
        userService.deleteById(user.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/logout")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<ResponseMessage> logout(HttpServletRequest request) {
        String jwt = jwtTokenFilter.getTokenFromRequest(request);
        if (jwt=="" || null == jwt){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User Login not found!")
                            .data("")
                            .build()
            );
        }
        if(logout()){
            return ResponseEntity.ok().body(
                    ResponseMessage.builder()
                            .status("OK")
                            .message("Logout successfully!")
                            .data("")
                            .build()
            );
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    private boolean logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecurityContextHolder.clearContext(); // Huỷ thông tin xác thực
            return true;
        }
        return false;
    }
    @PutMapping("/changeStt")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?>changeStatus(@RequestBody User user1){
        User userLogin=userDetailService.getCurrentUser();
        user1=userService.findById(user1.getId());
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User Login not found!")
                            .data("")
                            .build()
            );
        }
        if (userLogin.getRoles().size()<user1.getRoles().size()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Your account is not acceptable for change status!")
                            .data("")
                            .build()
            );
        }
        user1.setStatus(!user1.isStatus());
        return new ResponseEntity<>( userService.save(user1),HttpStatus.CREATED);
    }
}
