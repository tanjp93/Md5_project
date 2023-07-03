package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.request.BuyProducts;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.*;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final UserDetailService userDetailService;
    private final IOrderDetailService orderDetailService;
    private final IPurchaseHistoryService purchaseHistoryService;
    private final IProductService productService;
    private final IReceiverAddressService addressService;
    private final IFeedbackService feedbackService;
    private  final IResponseService responseService;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?>getOrder(){
        User user= userDetailService.getCurrentUser();
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

    @PostMapping("/addToCart")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?>addToCart(@RequestBody OrderDetail orderDetail){
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
        Order order=orderService.findOrderByUser(userLogin);
        orderDetail.setStatus(0);
        orderDetail.setPrice(productService.findById(orderDetail.getProduct().getId()).getPrice());
        orderDetail.setOrder(order);
        List<OrderDetail>orderDetailList=orderDetailService.findOrderDetailsByOrder(order);
        for (OrderDetail o:orderDetailList ) {
            if (o.getProduct().getId()==orderDetail.getProduct().getId()){
                orderDetail.setId(o.getId());
                orderDetail.setQuantity(o.getQuantity()+orderDetail.getQuantity());
                return new ResponseEntity<>(orderDetailService.save(orderDetail),HttpStatus.OK);
            }
        }
        orderDetailList.add(orderDetail);
        order.setOrderDetails(orderDetailList);
        return new ResponseEntity<>( orderDetailService.save(orderDetail),HttpStatus.OK);
    }
    @PutMapping("/buyProduct")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?>buyProduct(@RequestBody BuyProducts buyProducts){
        User userLogin=userDetailService.getCurrentUser();
        ReceiverAddress address=addressService.findAddressById(buyProducts.getReceiverAddress().getId());
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("UserLogin not found!")
                            .data("")
                            .build()
            );
        }
        List<ReceiverAddress>receiverAddresses=addressService.findReceiverAddressByUser(userLogin);
        if (receiverAddresses==null||receiverAddresses.isEmpty()||address==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Create Address first!")
                            .data("")
                            .build());
        }
        boolean check=false;
        for (ReceiverAddress address1:receiverAddresses ) {
            if (address1.getId()==address.getId()){
                address=address1;
                check=true;
                break;
            }
        }
        if (!check){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Address is not available!")
                            .data("")
                            .build());
        }
        Order cart=orderService.findOrderByUser(userLogin);
        List<OrderDetail> orderDetailList=orderDetailService.findOrderDetailsByOrder(cart);
        boolean isExistedOrderDetailId=false;
        List<OrderDetail>buyList=buyProducts.getOrderDetailList();
        for (OrderDetail orderDetail:orderDetailList) {
            for (OrderDetail o:buyList ) {
                if (orderDetail.getId()==o.getId()){
                    isExistedOrderDetailId=true;
                    break;
                }
            }
        }
        if (!isExistedOrderDetailId){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found orderDetail !")
                            .data("")
                            .build()
            );
        }
        List<OrderDetail> buyProducts1=new ArrayList<>();
        PurchaseHistory purchaseHistory=PurchaseHistory.builder()
                .timeBuy(LocalDate.now().toString())
                .user(userLogin)
                .address(address)
                .orderDetails(buyProducts1)
                .build();
        purchaseHistory= purchaseHistoryService.save(purchaseHistory);
        Product product;
        for (OrderDetail orderDetail:orderDetailList) {
            for (OrderDetail o:buyList ) {
                if (orderDetail.getId()==o.getId()){
                    orderDetail.setStatus(1);
                    orderDetail.setOrder(null);
                    orderDetail.setPurchaseHistory(purchaseHistory);
                    buyProducts1.add(orderDetail);
                    product=productService.findById(orderDetail.getProduct().getId());
                    Long stock1=product.getStoke()-orderDetail.getQuantity();
                    if (stock1<0){
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                                ResponseMessage.builder()
                                        .status("FAILED")
                                        .message("Quantity is over!")
                                        .data("")
                                        .build()
                        );
                    }
                    product.setStoke(stock1);
                    productService.save(product);
                    orderDetailService.save(orderDetail);
                }
            }
        }

        //k tim thay san pham trong cart
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("deleteOrderDetail/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?>removeCartItem(@PathVariable("id") Long id){
        OrderDetail orderDetail=orderDetailService.findById(id);
        if (orderDetail==null||orderDetail.getStatus()!=0){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found object to delete!")
                            .data("")
                            .build()
            );
        }
        User userLogin=userDetailService.getCurrentUser();
        if (orderService.findOrderByUser(userLogin).getId()!=orderDetail.getOrder().getId()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Quantity is over!")
                            .data("")
                            .build()
            );
        }
        orderDetail.setPurchaseHistory(null);
        orderDetail.setOrder(null);
        List<Feedback>feedbackList=feedbackService.findFeedbacksByOrderDetail(orderDetail);
        for (Feedback feedback:feedbackList) {
            List<Response> responses=responseService.findResponsesByFeedback(feedback);
            for (int i = 0; i <responses.size() ; i++) {
                responseService.deleteById(responses.get(i).getId());
            }
            feedbackService.deleteById(feedback.getId());
        }
        orderDetailService.deleteById(orderDetail.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
