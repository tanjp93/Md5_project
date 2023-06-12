package ra.project.service;



import ra.project.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> findAllUser();
    User findUserById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    User save(User user);
}
