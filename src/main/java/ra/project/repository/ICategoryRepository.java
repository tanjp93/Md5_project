package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category,Long> {

}
