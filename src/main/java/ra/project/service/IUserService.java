package ra.project.service;



import ra.project.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> findAllUser();
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);
    User save(User user);
}
