package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.Feedback;
import ra.project.model.OrderDetail;
import ra.project.model.Role;
import ra.project.model.User;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IFeedbackService;
import ra.project.service.IService.IOrderDetailService;
import ra.project.service.IService.IOrderService;
import ra.project.service.IService.IUserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedBackController {
    private final IOrderDetailService orderDetailService;
    private final IFeedbackService feedbackService;
    private final UserDetailService userDetailService;
    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> getFeedbackById(@RequestParam("id") Long id) {
        User userLogin=userDetailService.getCurrentUser();
        Feedback feedback = feedbackService.findById(id);
        if (feedback == null||feedback.getUser().getId()!=userLogin.getId()) {
            if (!userService.checkManageRole(userLogin)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("Feedback not found")
                                .data("")
                                .build()
                );
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseMessage.builder()
                        .status("OK")
                        .message(feedback.getContent())
                        .data(feedback)
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> addFeedback(@RequestBody Feedback feedback, BindingResult bindingResult) {
        User userLogin = userDetailService.getCurrentUser();
        OrderDetail orderDetail = orderDetailService.findById(feedback.getOrderDetail().getId());
        if (bindingResult.hasErrors()||userLogin==null||orderDetail==null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input")
                            .data("")
                            .build()
            );
        }
        User userBuy=null;
        if (orderDetail.getOrder()!=null){
            userBuy=orderDetail.getOrder().getUser();
        }else {
            userBuy=orderDetail.getPurchaseHistory().getUser();
        }
        if (!userService.checkManageRole(userLogin)){
            if (userBuy.getId()!=userLogin.getId()){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("Can not authenticate user!")
                                .data("")
                                .build()
                );
            }
        }
        feedback.setUser(userLogin);
        feedback.setOrderDetail(orderDetail);
        if (orderDetail.getStatus() < 3) {
            feedback.setVote(5L);
        }
        feedbackService.save(feedback);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> deleteFeedback(@PathVariable("id") Long id) {
        User userLogin = userDetailService.getCurrentUser();
        Feedback feedback = feedbackService.findById(id);
        if (userLogin==null||feedback==null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid authentication!")
                            .data("")
                            .build()
            );
        }
        User userBuy=null;
        OrderDetail orderDetail = orderDetailService.findById(feedback.getOrderDetail().getId());
        if (orderDetail.getOrder()!=null){
            userBuy=orderDetail.getOrder().getUser();
        }else {
            userBuy=orderDetail.getPurchaseHistory().getUser();
        }
        if (userBuy.getId()!=userLogin.getId()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Can not authenticate user!")
                            .data("")
                            .build()
            );
        }
        feedbackService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
