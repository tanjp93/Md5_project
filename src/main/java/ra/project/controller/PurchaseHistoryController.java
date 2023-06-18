package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.OrderDetail;
import ra.project.model.PurchaseHistory;
import ra.project.model.User;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IOrderDetailService;
import ra.project.service.IService.IPurchaseHistoryService;
import java.util.List;

@RestController
@RequestMapping("/purchaseHistory")
@RequiredArgsConstructor
public class PurchaseHistoryController {
    private final IPurchaseHistoryService purchaseHistoryService;
    private final IOrderDetailService orderDetailService;
    private  final UserDetailService userDetailService;
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN,PM')")
    private ResponseEntity<?>getRevenue(@RequestParam("from")String from,@RequestParam("to")String to){
        User user=userDetailService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User not found!")
                            .data("")
                            .build()
            );
        }
        List<PurchaseHistory>purchaseHistories=purchaseHistoryService.findPurchaseHistoriesByTimeBuyBetween(from, to);
        if (purchaseHistories==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found!")
                            .data("")
                            .build()
            );
        }
        Float revenue=0f;
        for (int i = 0; i <purchaseHistories.size() ; i++) {
            List<OrderDetail>orderDetails=purchaseHistories.get(i).getOrderDetails();
            for (int j = 0; j <orderDetails.size(); j++) {
                revenue+=orderDetails.get(j).getPrice()*orderDetails.get(j).getQuantity();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseMessage.builder()
                        .status("OK")
                        .message("Revenue from :"+from+" to :"+to+ " :"+revenue+" (vnd)")
                        .data(revenue)
                        .build()
        );
    }
    @PutMapping("/changeSttOrderDetail/{orderDetail_id}")
    @PreAuthorize("hasAuthor")
    private ResponseEntity<?>updateSttOrderDetail(@PathVariable("orderDetail_id")Long id){
        OrderDetail orderDetail= orderDetailService.findById(id);
        if (orderDetail==null){
            if (orderDetail.getStatus()>=3||orderDetail.getStatus()<1)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("OrderDetail Not found!")
                            .data("")
                            .build()
            );
        }
        if (orderDetail.getStatus()>=3||orderDetail.getStatus()<=0){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("OrderDetail is not available!")
                            .data("")
                            .build());
        }
        orderDetail.setStatus(orderDetail.getStatus()+1);
        return new ResponseEntity<>(orderDetailService.save(orderDetail),HttpStatus.OK);
    }
}
