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
import ra.project.service.IService.IFeedbackService;
import ra.project.service.IService.IOrderDetailService;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedBackController {
    private final IOrderDetailService orderDetailService;
    private final IFeedbackService feedbackService;
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?>getFeedbackById(@RequestParam("id")Long id){
        Feedback feedback=feedbackService.findById(id);
        if (feedback==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?>addFeedback(@RequestBody Feedback feedback, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input")
                            .data("")
                            .build()
            );
        }
        OrderDetail orderDetail=feedback.getOrderDetail();
        if (orderDetail.getStatus()<3){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input")
                            .data("")
                            .build()
            );
        }
        feedbackService.save(feedback);
        List<Feedback> feedbackList=orderDetail.getFeedback();
        feedbackList.remove(feedback);
        orderDetail.setFeedback(feedbackList);
        orderDetailService.save(orderDetail);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?>deleteFeedback(@PathVariable("id")Long id){
        Feedback feedback=feedbackService.findById(id);
        if (feedback==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Id is not available!")
                            .data("")
                            .build()
            );
        }
        feedbackService.deleteById(id);
        OrderDetail orderDetail=feedback.getOrderDetail();
        List<Feedback> feedbackList=orderDetail.getFeedback();
        feedbackList.remove(feedback);
        orderDetail.setFeedback(feedbackList);
        orderDetailService.save(orderDetail);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
