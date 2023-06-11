package ra.project.controller;

import ra.project.model.User;
import ra.project.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v6/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("")
    public List<User> findAllUser() {
        return userService.findAllUser();
    }

}
