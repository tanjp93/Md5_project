package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.Feedback;
import ra.project.model.Role;
import ra.project.model.User;
import ra.project.model.Response;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IFeedbackService;
import ra.project.service.IService.IResponseService;

import java.util.Set;

@RestController
@RequestMapping("/response")
@RequiredArgsConstructor
public class ResponseController {
    private final IResponseService responseService;
    private final UserDetailService userDetailService;
    private final IFeedbackService feedbackService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    private ResponseEntity<?> getResponse(@RequestParam("id") Long id) {
        User userLogin=userDetailService.getCurrentUser();
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found userLogin!")
                            .data("")
                            .build()
            );
        }
        Response response = responseService.findById(id);
        if (response == null||response.getUser().getId()!=userLogin.getId()) {
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
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    private ResponseEntity<?>addResponse(@RequestBody Response response, BindingResult bindingResult){
        User userLogin=userDetailService.getCurrentUser();
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found userLogin!")
                            .data("")
                            .build()
            );
        }
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid Input!")
                            .data("")
                            .build());
        }
        Feedback feedback=response.getFeedback();
        Set<Role> roleUserLogin=userLogin.getRoles();
        if (feedback.getUser().getId()!=userLogin.getId()){
            for (Role role:  roleUserLogin) {
                if (!role.getName().equals("ADMIN")|| !role.getName().equals("PM")){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                            ResponseMessage.builder()
                                    .status("FAILED")
                                    .message("Not found response!")
                                    .data("")
                                    .build()
                    );
                }
            }
        }
        responseService.save(response);
        feedback.getResponseList().add(response);
        feedbackService.save(feedback);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    private ResponseEntity<?>addResponse(@PathVariable("id")Long id,@RequestBody Response response, BindingResult bindingResult){
        User userLogin=userDetailService.getCurrentUser();
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found userLogin!")
                            .data("")
                            .build()
            );
        }
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid Input!")
                            .data("")
                            .build());
        }
        Response response1=responseService.findById(id);
        if (response1.getUser().getId()!=userLogin.getId()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Can't not update!")
                            .data("")
                            .build());
        }
        response.setId(id);
        response.setUser(userLogin);
        responseService.save(response);
        Feedback feedback=response.getFeedback();
        feedback.getResponseList().add(response);
        feedbackService.save(feedback);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('AMIN,PM,USER')")
    public ResponseEntity<?> deleteResponse(@PathVariable("id")Long id){
        User userLogin=userDetailService.getCurrentUser();
        Response response=responseService.findById(id);
        if (userLogin==null||response.getUser().getId()!=userLogin.getId()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Can't not delete!")
                            .data("")
                            .build());
        }
        Feedback feedback=response.getFeedback();
        feedback.getResponseList().remove(response);
        feedbackService.save(feedback);
        responseService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
