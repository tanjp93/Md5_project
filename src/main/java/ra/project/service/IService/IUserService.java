package ra.project.service.IService;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.project.model.User;
import ra.project.service.IGenerateService;
import java.util.Optional;

public interface IUserService extends IGenerateService<User,Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUserName(String userName);
    Page<User>findAllUser(Pageable pageable);
    boolean checkManageRole(User userLogin);
}
