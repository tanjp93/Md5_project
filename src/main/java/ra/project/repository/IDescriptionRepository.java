package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Description;

@Repository
public interface IDescriptionRepository extends JpaRepository<Description,Long> {

}
