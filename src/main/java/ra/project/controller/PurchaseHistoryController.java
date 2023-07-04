package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.*;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchaseHistory")
@RequiredArgsConstructor
public class PurchaseHistoryController {
    private final IPurchaseHistoryService purchaseHistoryService;
    private final IOrderDetailService orderDetailService;
    private  final UserDetailService userDetailService;
    private final IUserService userService;
    private  final IOrderService orderService;
    private final IProductService productService;
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?>getRevenue(@RequestParam("from")String from,@RequestParam("to")String to){
        User user=userDetailService.getCurrentUser();
        if (user == null||!userService.checkManageRole(user)) {
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
                if (orderDetails.get(j).getStatus()==4){
                    revenue+=orderDetails.get(j).getPrice()*orderDetails.get(j).getQuantity();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseMessage.builder()
                        .status("OK")
                        .message("Revenue from :"+from+" to :"+to+ " :"+revenue+" (vnd)")
                        .data(revenue)
                        .build()
        );
    }
    @PutMapping("/changeSttOrderDetail")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?>updateSttOrderDetail(@RequestBody OrderDetail orderDetail){
        if (orderDetail.getStatus()==0||orderDetail.getStatus()>4||orderDetail.getStatus()<-2){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Status is not available!")
                            .data("")
                            .build()
            );
        }
        User userLogin=userDetailService.getCurrentUser();
        if (!userService.checkManageRole(userLogin)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User Login is not PM!")
                            .data("")
                            .build()
            );
        }
        OrderDetail orderDetail1= orderDetailService.findById(orderDetail.getId());
        if (orderDetail1==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("OrderDetail Not found!")
                            .data("")
                            .build()
            );
        }
        orderDetail1.setStatus(orderDetail.getStatus());
        return new ResponseEntity<>(orderDetailService.save(orderDetail1),HttpStatus.OK);
    }
    @PutMapping("/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?>cancelOrderDetail(@RequestBody OrderDetail orderDetail){
        PurchaseHistory purchaseHistory1=orderDetail.getPurchaseHistory();
        if (orderDetail.getStatus()!=1||purchaseHistory1==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Can not cancel order !")
                            .data("")
                            .build());
        }

        User userLogin=userDetailService.getCurrentUser();
        //
        if (userLogin.getId()!=purchaseHistory1.getUser().getId()) {
            if (!userService.checkManageRole(userLogin)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("Can not cancel order !")
                                .data("")
                                .build());
            }
        }
        //cancel order
        orderDetail.setOrder(userLogin.getOrder());
        orderDetail.setPurchaseHistory(null);
        orderDetail.setStatus(-1);
        orderDetailService.save(orderDetail);
        Product product=orderDetail.getProduct();
        product.setStoke(orderDetail.getQuantity()+ product.getStoke());
        productService.save(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
