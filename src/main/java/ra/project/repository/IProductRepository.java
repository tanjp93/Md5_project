package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Category;
import ra.project.model.Product;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product,Long> {
    List<Product> findProductsByCategory(Category category);
}
