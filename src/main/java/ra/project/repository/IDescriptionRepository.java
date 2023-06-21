package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Description;
import ra.project.model.Product;

import java.util.List;

@Repository
public interface IDescriptionRepository extends JpaRepository<Description,Long> {
    List<Description>findDescriptionsByProduct(Product product);
    boolean existsByDescribe(String des);
}
