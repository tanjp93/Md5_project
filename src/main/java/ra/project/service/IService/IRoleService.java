package ra.project.service.IService;



import ra.project.model.Role;
import ra.project.model.RoleName;
import java.util.Optional;

public interface IRoleService {
    Optional<Role> findByName(RoleName name);
}
