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
import ra.project.service.IService.IReceiverAddressService;


import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final IReceiverAddressService receiverAddressService;

    @PostMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseMessage> createAddress(@Validated @RequestBody ReceiverAddress address, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Please fill out completely!")
                            .data("")
                            .build()
            );
        }
        boolean isPhoneNumber = receiverAddressService.existsByPhoneNumber(address.getPhoneNumber());
        if (isPhoneNumber) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Phone number is existed !")
                            .data("")
                            .build()
            );
        }
        receiverAddressService.save(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseMessage.builder()
                        .status("CREATED")
                        .message("Created successfully")
                        .data("")
                        .build()
        );
    }
    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?>findByUserId(@RequestParam("userId")Long userId){
        List<ReceiverAddress> address=receiverAddressService.findAllByUser(userId);
        if (address!=null){
        return new ResponseEntity<>(address,HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("List Address not found !")
                        .data("")
                        .build()
        );
    }
    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?> detailAddress(@RequestParam("id") Long id) {
        ReceiverAddress receiverAddress = receiverAddressService.findAddressById(id);
        if (receiverAddress == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found address to update !")
                            .data("")
                            .build()
            );
        }
        return new ResponseEntity<>(receiverAddressService.findAddressById(id), HttpStatus.OK);
    }
    @PutMapping("/update/{addressId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> updateAddress(@PathVariable("addressId") Long addressId, @Validated @RequestBody ReceiverAddress updateAddress) {
        ReceiverAddress receiverAddress1 = receiverAddressService.findAddressById(addressId);
        if (receiverAddress1 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Not found address to update !")
                            .data("")
                            .build()
            );
        }
        updateAddress.setId(addressId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") Long id) {
        ReceiverAddress address = receiverAddressService.findAddressById(id);
        if (address != null) {
            receiverAddressService.deleteAddressById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseMessage.builder()
                            .status("OK")
                            .message("Delete successfully !")
                            .data("")
                            .build()
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("address not found!")
                        .data("")
                        .build()
        );
    }
}
