package ra.project.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Response;
@Repository
public interface IResponseRepository extends JpaRepository<Response,Long> {
}
