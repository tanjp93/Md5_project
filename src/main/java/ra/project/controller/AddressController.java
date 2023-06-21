package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.ReceiverAddress;
import ra.project.model.User;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IReceiverAddressService;
import ra.project.service.IService.IUserService;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final IReceiverAddressService receiverAddressService;
    private final UserDetailService userDetailService;
    private final IUserService userService;


    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<ResponseMessage> createAddress(@Validated @RequestBody ReceiverAddress address, BindingResult bindingResult) {
        User userLogin = userDetailService.getCurrentUser();
        if (bindingResult.hasErrors() || userLogin == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User Login is not found!")
                            .data("")
                            .build()
            );
        }
        List<ReceiverAddress> addressList = userLogin.getReceiverAdressList();
        if (addressList == null) {
            addressList = new ArrayList<>();
        }
        for (ReceiverAddress add : addressList) {
            if (add.getPhoneNumber().equals(address.getPhoneNumber())) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("PhoneNumber is already existed! ")
                                .data("")
                                .build()
                );
            }
        }
        address.setUser(userLogin);
        receiverAddressService.save(address);
        addressList.add(address);
        userLogin.setReceiverAdressList(addressList);
        userService.save(userLogin);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseMessage.builder()
                        .status("CREATED")
                        .message("Created successfully")
                        .data("")
                        .build()
        );
    }

    @GetMapping("/listAddress")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> findListAddressByUserId() {
        User userLogin = userDetailService.getCurrentUser();
        if (userLogin == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User Login is not found!")
                            .data("")
                            .build()
            );
        }
        List<ReceiverAddress> adressList = userLogin.getReceiverAdressList();
        if (adressList == null || adressList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("List Address not found !")
                            .data("")
                            .build()
            );
        }
        return new ResponseEntity<>(adressList,HttpStatus.OK);
    }

    @GetMapping("/detail")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> detailAddress(@RequestParam("id") Long id) {
        User userLogin = userDetailService.getCurrentUser();
        if (userLogin == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User Login is not found!")
                            .data("")
                            .build()
            );
        }
        List<ReceiverAddress> addressList = userLogin.getReceiverAdressList();
        if (addressList == null || addressList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found address in List address ")
                            .data("")
                            .build()
            );
        }
        for (ReceiverAddress add : addressList) {
            if (add.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        ResponseMessage.builder()
                                .status("OK")
                                .message("Address is found !")
                                .data(add)
                                .build()
                );
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("Not found")
                        .data("")
                        .build()
        );
    }

    @PutMapping("/update/{addressId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> updateAddress(@PathVariable("addressId") Long addressId, @Validated @RequestBody ReceiverAddress updateAddress) {
        User userLogin = userDetailService.getCurrentUser();
        if (userLogin == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User Login is not found!")
                            .data("")
                            .build()
            );
        }
        List<ReceiverAddress> addressList = userLogin.getReceiverAdressList();
        if (addressList == null || addressList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found address in List address")
                            .data("")
                            .build()
            );
        }
        for (ReceiverAddress add : addressList) {
            if (add.getPhoneNumber().equals(updateAddress.getPhoneNumber())) {
                if (add.getId() != addressId) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                            ResponseMessage.builder()
                                    .status("FAILED")
                                    .message("PhoneNumber is already existed! ")
                                    .data("")
                                    .build()
                    );
                }
            }
        }
        for (ReceiverAddress add : addressList) {
            if (add.getId().equals(addressId)) {
                int index = addressList.indexOf(add);
                updateAddress.setUser(userLogin);
                updateAddress.setId(addressId);
                addressList.set(index, updateAddress);
                receiverAddressService.save(addressList.get(index));
                //
                userLogin.setReceiverAdressList(addressList);
                userService.save(userLogin);
                return ResponseEntity.status(HttpStatus.CREATED).body(
                        ResponseMessage.builder()
                                .status("OK")
                                .message("Update successfully")
                                .data(updateAddress)
                                .build()
                );
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("Not found address to Update! ")
                        .data("")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM','USER')")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") Long id) {
        User userLogin = userDetailService.getCurrentUser();
        if (userLogin == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("User Login is not found!")
                            .data("")
                            .build()
            );
        }
        List<ReceiverAddress> addressList = userLogin.getReceiverAdressList();
        if (addressList == null || addressList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found address in List address ")
                            .data("")
                            .build()
            );
        }
        for (ReceiverAddress add : addressList) {
            if (add.getId().equals(id)) {
                addressList.remove(add);
                userLogin.setReceiverAdressList(addressList);
                userService.save(userLogin);
                receiverAddressService.deleteAddressById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("address not found!")
                        .data("")
                        .build()
        );
    }
}
