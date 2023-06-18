package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Feedback;
@Repository
public interface IFeedbackRepository extends JpaRepository<Feedback,Long> {
}
