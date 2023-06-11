package ra.project.service.serviceIMPL;

import ra.project.model.Role;
import ra.project.model.RoleName;
import ra.project.repository.IRoleRepository;
import ra.project.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceIMPL implements IRoleService {

    private final IRoleRepository roleService;

    @Override
    public Optional<Role> findByName(RoleName name) {
        return roleService.findByName(name);
    }
}
