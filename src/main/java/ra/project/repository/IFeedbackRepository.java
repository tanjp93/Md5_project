package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Feedback;
import ra.project.model.OrderDetail;

import java.util.List;

@Repository
public interface IFeedbackRepository extends JpaRepository<Feedback,Long> {
    List<Feedback> findFeedbacksByOrderDetail(OrderDetail o);
}
