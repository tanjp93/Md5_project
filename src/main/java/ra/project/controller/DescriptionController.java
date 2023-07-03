package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.Description;
import ra.project.model.Product;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IDescriptionService;
import ra.project.service.IService.IProductService;

import java.util.List;

@RestController
@RequestMapping("/api/description")
@RequiredArgsConstructor
public class DescriptionController {
    private final IDescriptionService descriptionService;
    private final UserDetailService userDetailService;
    private final IProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?> addDescription(@RequestBody Description description, BindingResult bindingResult) {
        Product product = productService.findById(description.getProduct().getId());
        if (bindingResult.hasErrors() || product == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input!")
                            .data("")
                            .build()
            );
        }
        description.setProduct(product);
        descriptionService.save(description);
        product.getDescriptions().add(description);
        productService.save(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?>UpdateDescription(@RequestBody Description description,BindingResult bindingResult){
        Description descriptionBase=descriptionService.findById(description.getId());
        if (bindingResult.hasErrors()||descriptionBase==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input!")
                            .data("")
                            .build()
            );
        }
        if (productService.findById(description.getProduct().getId())!=null){
            return new ResponseEntity<>(descriptionService.save(description),HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("Invalid input!")
                        .data("")
                        .build()
        );
    }
    @DeleteMapping("/{descriptionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?> deleteDescription(@PathVariable("descriptionId") Long descriptionId) {
        Description description = descriptionService.findById(descriptionId);
        if (description == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input!")
                            .data("")
                            .build()
            );
        }
        descriptionService.deleteById(descriptionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
