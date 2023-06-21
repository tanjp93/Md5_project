package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.project.model.Role;
import ra.project.model.User;
import ra.project.repository.IUserRepository;
import ra.project.security.userPrincipal.UserDetailService;
import ra.project.service.IService.IUserService;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceIMPL implements IUserService {
    private final IUserRepository userRepository;
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            return userRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public Page<User> findAllUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    @Override
    public boolean checkManageRole( User userLogin ) {
        if (userLogin!=null){
            Set<Role> roles = userLogin.getRoles();
            for (Role role : roles) {
                if (role.getName().name().equals("ADMIN") || role.getName().name().equals("PM")) {
                    return true;
                }
            }
        }
        return false;
    }
}
