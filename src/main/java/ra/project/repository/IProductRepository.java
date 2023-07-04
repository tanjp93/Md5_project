package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.project.dto.request.CountProductByVote;
import ra.project.model.Category;
import ra.project.model.Product;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductsByCategory(Category category);

    boolean existsProductByProductName(String name);

    //Tim san pham theo luot yêu thích trong tháng
    @Query(value = "select  p.id as id,p.product_name as productName,count(p.id) as count from product p \n" +
            "join order_detail od on p.id = od.product join feedback f on od.id=f.order_detail where f.vote =:vote  group by p.id", nativeQuery = true)
    List<Product> getProductByVote(@Param("vote") Long vote);
}
