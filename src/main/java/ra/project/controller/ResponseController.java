package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.*;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IFeedbackService;
import ra.project.service.IService.IOrderDetailService;
import ra.project.service.IService.IResponseService;
import ra.project.service.IService.IUserService;

import java.util.Set;

@RestController
@RequestMapping("/api/response")
@RequiredArgsConstructor
public class ResponseController {
    private final IResponseService responseService;
    private final UserDetailService userDetailService;
    private final IFeedbackService feedbackService;
    private final IOrderDetailService orderDetailService;
    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> getResponse(@RequestParam("id") Long id) {
        User userLogin = userDetailService.getCurrentUser();
        if (userLogin == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found userLogin!")
                            .data("")
                            .build()
            );
        }
        Response response = responseService.findById(id);
        if (response == null || response.getUser().getId() != userLogin.getId()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found response!")
                            .data("")
                            .build()
            );
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{feedBackId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> addResponse(@PathVariable("feedBackId") Long feedBackId, @RequestBody Response response, BindingResult bindingResult) {
        User userLogin = userDetailService.getCurrentUser();
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input!")
                            .data("")
                            .build());
        }
        boolean check = responseService.checkUserResponseRole(userLogin, feedBackId);
        if (!check) {
            if (!userService.checkManageRole(userLogin)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("Can't not check Authentication!")
                                .data("")
                                .build());
            }
        }
        response.setUser(userLogin);
        response.setFeedback(feedbackService.findById(feedBackId));
        responseService.save(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> deleteResponse(@PathVariable("id") Long id) {
        User userLogin = userDetailService.getCurrentUser();
        boolean check = responseService.checkUserResponseRole(userLogin, id);
        if (!check) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Can't not check Authentication!")
                            .data("")
                            .build());
        }
        responseService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
