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
import ra.project.model.Role;
import ra.project.model.User;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IDescriptionService;
import ra.project.service.IService.IProductService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/description")
@RequiredArgsConstructor
public class DescriptionController {
    private final IDescriptionService descriptionService;
    private final UserDetailService userDetailService;
    private final IProductService productService;

    @PostMapping("/{productId}")
    @PreAuthorize("hasAuthority('ADMIN,PM')")
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
        descriptionService.save(description);
        product.getDescriptions().add(description);
        productService.save(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{descriptionId}")
    @PreAuthorize("hasAuthority('ADMIN,PM')")
    public ResponseEntity<?> updateDescription(@RequestBody Description description, BindingResult bindingResult, @PathVariable("descriptionId") Long descriptionId) {
        Description descriptionBase=descriptionService.findById(descriptionId);
        if (bindingResult.hasErrors() || descriptionBase == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Invalid input!")
                            .data("")
                            .build()
            );
        }
        descriptionService.save(description);
        Product product=description.getProduct();
        List<Description> descriptionList=product.getDescriptions();
        for (Description d:descriptionList) {
            if (d.getId()==description.getId()){
                int index=descriptionList.indexOf(d);
                descriptionList.set(index,description);
            }
        }
        product.setDescriptions(descriptionList);
        productService.save(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping ("/{descriptionId}")
    @PreAuthorize("hasAuthority('ADMIN,PM')")
    public ResponseEntity<?> deleteDescription(@PathVariable("descriptionId") Long descriptionId) {
        Description description=descriptionService.findById(descriptionId);
        if (description==null){
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
