package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.Category;
import ra.project.model.Product;
import ra.project.model.Role;
import ra.project.model.User;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.ICategoryService;
import ra.project.service.IService.IProductService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;
    private final UserDetailService userDetailService;
    private final IProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        List<Category> categories = categoryService.findAll();
        if (categories != null) {
            return new ResponseEntity(categories, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseMessage.builder()
                        .status("Data does not exist")
                        .message("")
                        .data("")
                        .build()
        );
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getCategoryById(@RequestParam("id") Long id) {
        Category category = categoryService.findById(id);
        if (category != null) {
            return new ResponseEntity(category, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseMessage.builder()
                        .status("Data does not exist")
                        .message("")
                        .data("")
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?> createCategory(@Validated @RequestBody Category category, BindingResult bindingResult) {
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
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("failed")
                            .data("")
                            .build()
            );
        }
        if (categoryService.existsCategoryByCategoryName(category.getCategoryName())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Category is existed!")
                            .data("")
                            .build()
            );
        }
        Set<Role> roleList=userLogin.getRoles();
        for (Role role:roleList) {
            if (role.getName().name().equals("ADMIN")||role.getName().name().equals("PM")) {
                categoryService.save(category);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("User Login is not admin or manager")
                        .data("")
                        .build()
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?> updateCategory(@Validated @RequestBody Category category, BindingResult bindingResult) {
        User userLogin = userDetailService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Update failed")
                            .data("")
                            .build()
            );
        }
        if (userLogin==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("UserLogin is not found!")
                            .data("")
                            .build()
            );
        }
        Category category1 = categoryService.findById(category.getId());
        if (!category1.getCategoryName().toLowerCase().equals(category.getCategoryName().toLowerCase())){
            if (categoryService.existsCategoryByCategoryName(category.getCategoryName())){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("Category is existed!")
                                .data("")
                                .build()
                );
            }
        }
        Set<Role> roleList=userLogin.getRoles();
        for (Role role:roleList) {
            if (role.getName().name().equals("ADMIN")||role.getName().name().equals("PM")) {
                if (category1!=null){
                    categoryService.save(category);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseMessage.builder()
                                .status("FAILED")
                                .message("Update failed")
                                .data("")
                                .build()
                );
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("User Login is not admin or manager")
                        .data("")
                        .build()
        );
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PM')")
    public ResponseEntity<?>deleteById(@PathVariable("id")Long id){
        Category category=categoryService.findById(id);
        if (category!=null){
            if (category.getProductList().isEmpty()||category.getProductList()==null){
                List<Product>products=category.getProductList();
                for (Product p:products ) {
                    p.setCategory(null);
                    productService.save(p);
                }
                categoryService.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body(
                        ResponseMessage.builder()
                                .status("OK")
                                .message("Delete successfully")
                                .data("")
                                .build()
                );
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("Delete failed")
                        .data("")
                        .build()
        );
    }
}
