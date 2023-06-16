package ra.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import ra.project.model.Product;
import ra.project.service.IService.ICategoryService;
import ra.project.service.IService.IProductService;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private IProductService productService;
    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('AMIN,PM,USER')")
    public ResponseEntity<?> getAllProduct(Pageable pageable,
                                           @RequestParam("sortBy") String sortBy,
                                           @RequestParam("orderBy") String orderBy) {
        pageable = getPageable(pageable, sortBy, orderBy);
        return new ResponseEntity<>(productService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/detail")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> findById(@RequestParam("id") Long id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Product is not existed !")
                            .data("")
                            .build()
            );
        }
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/searchByCategory")
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?> getAllProductByCategoryId(@RequestParam("categoryId") Long categoryId,
                                                       @RequestParam("sortBy") String sortBy,
                                                       @RequestParam("orderBy") String orderBy,
                                                       Pageable pageable
    ) {
        pageable = getPageable(pageable, sortBy, orderBy);
        Category category = categoryService.findById(categoryId);
        List<Product> productList = null;
        if (category != null) {
            productList = productService.findProductsByCategory(category);
            if (productList != null) {
                return new ResponseEntity<>(productList, HttpStatus.OK);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseMessage.builder()
                        .status("FAILED")
                        .message("Category is not existed !")
                        .data("")
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN,PM')")
    public ResponseEntity<?> createProduct(@Validated @RequestBody Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Create failed")
                            .data("")
                            .build()
            );
        }
        productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseMessage.builder()
                        .status("CREATED")
                        .message("Create successfully")
                        .data("")
                        .build()
        );
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasAuthority('AMIN,PM')")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @Validated @RequestBody Product product, BindingResult bindingResult) {
        Product product1 = productService.findById(id);
        if (product1 == null || bindingResult.hasErrors()) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Update failed")
                            .data("")
                            .build()
            );
        }
        product.setId(id);
        productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseMessage.builder()
                        .status("OK")
                        .message("Update successfully")
                        .data("")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN,PM,USER')")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("Product not found !")
                            .data("")
                            .build()
            );
        }
        productService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseMessage.builder()
                        .status("OK")
                        .message("Delete successfully !")
                        .data("")
                        .build()
        );
    }

    private static Pageable getPageable(Pageable pageable, String sortBy, String orderBy) {
        Sort sortTable = null;
        if (sortBy == null) {
            sortBy = "id";
            orderBy = "asc";
        }
        switch (sortBy) {
            case "productName":
                if (orderBy.toLowerCase().equals("asc")) {
                    sortTable = Sort.by("productName").ascending();
                } else {
                    sortTable = Sort.by("productName").descending();
                }
                break;
            case "price":
                if (orderBy.toLowerCase().equals("asc")) {
                    sortTable = Sort.by("price").ascending();
                } else {
                    sortTable = Sort.by("price").descending();
                }
                break;
            default:
                sortTable = Sort.by("id").ascending();
        }
        pageable = PageRequest.of(pageable.getPageNumber(), 5, sortTable);
        return pageable;
    }
}
