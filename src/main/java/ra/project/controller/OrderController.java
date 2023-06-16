package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.Order;
import ra.project.model.User;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.serviceIMPL.OrderIMPL;
import ra.project.service.serviceIMPL.UserServiceIMPL;

import javax.servlet.http.HttpServletRequest;

@Controller
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final UserServiceIMPL userServiceIMPL;
    private final OrderIMPL orderIMPL;
    private final UserDetailService userDetailService;

    @GetMapping("/getOrder")
    @PreAuthorize("hasAuthority('AMIN,PM,USER')")
    private ResponseEntity<?>getOrderByUserId(Long userId){
        User user=userServiceIMPL.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User not found!")
                            .data("")
                            .build()
            );
        }
        Order order=orderIMPL.findOrderByUser(user);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
    @GetMapping("/getOrderByUserLogin")
    @PreAuthorize("hasAuthority('AMIN,PM,USER')")
    private ResponseEntity<?>getOrderByUserLogin(){
        User userLogin=userDetailService.getCurrentUser();
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User not found!")
                            .data("")
                            .build()
            );
        }
        Order order=orderIMPL.findOrderByUser(userLogin);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
