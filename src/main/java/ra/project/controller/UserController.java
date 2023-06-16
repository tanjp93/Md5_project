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
import ra.project.service.IService.IUserService;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v6/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN,PM')")
    public ResponseEntity<?> findAllUser(Pageable pageable,
                                         @RequestParam("sortBy") String sortBy,
                                         @RequestParam("orderBy") String orderBy
    ) {
        Sort sortable = null;
        if (sortBy == null || sortBy.equals("")) {
            sortBy = "id";
            orderBy = "asc";
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

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?> updateUser(HttpServletRequest request, @Validated @RequestBody UpdateForm updateUser, BindingResult bindingResult) {
        String jwt = jwtTokenFilter.getTokenFromRequest(request);
        String username = jwtProvider.getUserNameFromToken(jwt);
        User user;
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input")
                            .data("")
            );
        }

        try {
            user = userService.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with -> username" + username));
            boolean matches = passwordEncoder.matches(updateUser.getOldPassword(), user.getPassword());
            if (!matches) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("Wrong password! ")
                                .data("")
                                .build());
            }
            if (!updateUser.getEmail().equals(user.getEmail())) {
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
                user.setEmail(updateUser.getEmail());
            }
            user.setPassword(passwordEncoder.encode(updateUser.getPassword()));
            user.setFullName(updateUser.getFullName());
            user.setAvatar(updateUser.getAvatar());
            userService.save(user);
            logout();
            return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
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
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?> getUserDetail(@RequestParam("userId") Long id) {
        User user = userService.findById(id);
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User not found")
                            .data("")
                            .build()
            );
        }
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/logout")
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

}
