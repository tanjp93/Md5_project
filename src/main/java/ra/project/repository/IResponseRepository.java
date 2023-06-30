package ra.project.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Feedback;
import ra.project.model.Response;

import java.util.List;

@Repository
public interface IResponseRepository extends JpaRepository<Response,Long> {
    List<Response> findResponsesByFeedback(Feedback feedback);
}
