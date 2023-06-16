package ra.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ra.project.dto.response.ResponseMessage;
import ra.project.model.Category;
import ra.project.service.IService.ICategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
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
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
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
    @PreAuthorize("hasAuthority('ADMIN,PM')")
    public ResponseEntity<?> createCategory(@Validated @RequestBody Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("failed")
                            .data("")
                            .build()
            );
        }
        categoryService.save(category);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasAuthority('ADMIN,PM')")
    private ResponseEntity<?> updateCategory(@PathVariable("id")Long id,@Validated @RequestBody Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Update failed")
                            .data("")
                            .build()
            );
        }
        Category category1 = categoryService.findById(id);
        if (category1!=null){
            category.setId(id);
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
    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    private ResponseEntity<?>deleteById(@PathVariable("id")Long id){
        Category category=categoryService.findById(id);
        if (category!=null){
            if (category.getProductList().isEmpty()||category.getProductList()==null){
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
