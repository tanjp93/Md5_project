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

    @PostMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?> addDescription(@RequestBody Description description, BindingResult bindingResult, @PathVariable("productId") Long productId) {
        Product product = productService.findById(productId);
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
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?>UpdateDescription(@PathVariable("id")Long id,@RequestBody Description description,BindingResult bindingResult){
        Description descriptionBase=descriptionService.findById(id);
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input!")
                            .data("")
                            .build()
            );
        }
        description.setProduct(descriptionBase.getProduct());
        description.setId(descriptionBase.getId());
        return new ResponseEntity<>(descriptionService.save(description),HttpStatus.OK);
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
