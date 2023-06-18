package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.*;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IOrderDetailService;
import ra.project.service.IService.IOrderService;
import ra.project.service.IService.IPurchaseHistoryService;
import ra.project.service.IService.IUserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final IUserService userService;
    private final IOrderService orderService;
    private final UserDetailService userDetailService;
    private final IOrderDetailService orderDetailService;
    private final IPurchaseHistoryService purchaseHistoryService;

    @GetMapping("/getOrder")
    @PreAuthorize("hasAuthority('AMIN,PM')")
    private ResponseEntity<?>getOrderByUserId(Long userId){
        User user= userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User not found!")
                            .data("")
                            .build()
            );
        }
        Order order=user.getOrder();
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
                            .message("UserLogin not found!")
                            .data("")
                            .build()
            );
        }
        Order order=userLogin.getOrder();
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
    @PostMapping("/addToCart")
    @PreAuthorize("hasAuthority('AMIN,PM,USER')")
    private ResponseEntity<?>addToCart(OrderDetail orderDetail){
        User userLogin=userDetailService.getCurrentUser();
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("UserLogin not found!")
                            .data("")
                            .build()
            );
        }
        Order order=userLogin.getOrder();
        List<OrderDetail>orderDetailList=order.getOrderDetails();
        for (OrderDetail o:orderDetailList ) {
            if (o.getProduct().getId()==orderDetail.getProduct().getId()){
                o.setQuantity(o.getQuantity()+orderDetail.getQuantity());
                orderDetailService.save(o);
                orderService.save(order);
                return new ResponseEntity<>( orderService.save(order),HttpStatus.OK);
            }
        }
        orderDetail.setStatus(0);
        orderDetail.setPrice(orderDetail.getProduct().getPrice());
        orderDetailList.add(orderDetail);
        order.setOrderDetails(orderDetailList);
        orderService.save(order);
        return new ResponseEntity<>( orderService.save(order),HttpStatus.OK);
    }
    @PutMapping("/buyProduct")
    @PreAuthorize("hasAuthority('AMIN,PM,USER')")
    private ResponseEntity<?>buyProduct(List<OrderDetail>buyList, @RequestBody ReceiverAddress address){
        User userLogin=userDetailService.getCurrentUser();
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("UserLogin not found!")
                            .data("")
                            .build()
            );
        }
        if (!userLogin.getReceiverAdressList().contains(address)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Address not found!")
                            .data("")
                            .build()
            );
        }
        Order cart=userLogin.getOrder();
        List<OrderDetail> buyProducts=new ArrayList<>();
        for (OrderDetail od: buyList) {
            for (OrderDetail orderDetail: cart.getOrderDetails()){
                if (od.getId()==orderDetail.getId()){
                    orderDetail.setPrice(orderDetail.getProduct().getPrice());
                    orderDetail.setStatus(1);
                    cart.getOrderDetails().remove(orderDetail);
                    orderDetailService.save(orderDetail);
                    orderService.save(cart);
                    buyProducts.add(orderDetail);
                }
            }
        }
        if (buyProducts.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found orderDetail !")
                            .data("")
                            .build()
            );
        }
        PurchaseHistory purchaseHistory=PurchaseHistory.builder()
                .timeBuy(LocalDate.now().toString())
                .user(userLogin)
                .address(address)
                .orderDetails(buyProducts)
                .build();
        purchaseHistoryService.save(purchaseHistory);
        return new ResponseEntity<>(userLogin.getOrder(),HttpStatus.OK);
    }
//    private boolean checkOrderDetailInCartUser(OrderDetail orderDetail,User userLogin){
//        Order order=userLogin.getOrder();
//        List<OrderDetail>orderDetailList=order.getOrderDetails();
//        for (OrderDetail o:orderDetailList ) {
//            if (o.getProduct().getId()==orderDetail.getProduct().getId()){
//                return true;
//            }
//        }
//        return false;
//    }
}
