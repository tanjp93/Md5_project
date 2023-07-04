package ra.project.service.IService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.project.dto.request.CountProductByVote;
import ra.project.model.Category;
import ra.project.model.Product;
import ra.project.service.IGenerateService;
import java.util.List;

public interface IProductService extends IGenerateService<Product,Long> {
    List<Product> findProductsByCategory(Category category);
    Page<Product> findAll(Pageable pageable);
    boolean existsProductByProductName(String name);
    List<Product> findProductByVote(Long vote);
}
