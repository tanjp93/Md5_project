package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.ReceiverAddress;
import ra.project.model.User;
import ra.project.service.IReceiverAddressService;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final IReceiverAddressService receiverAddressService;

    @PostMapping("")
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

    @GetMapping("/detail/{idUser}")
    public ResponseEntity<?> getAddressByUserId(@PathVariable("idUser") Long userId) {
        return new ResponseEntity<>(receiverAddressService.findAllByUser(userId), HttpStatus.OK);
    }

    @PutMapping("/update/{addressId}")
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
        return new ResponseEntity<>(receiverAddressService.save(updateAddress), HttpStatus.CREATED);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> updateAddress(@RequestParam("id") Long id) {
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

    @DeleteMapping("/{id}")
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
