package ra.project.security.userPrincipal;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import ra.project.model.User;
import ra.project.repository.IUserRepository;
import ra.project.service.IService.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final IUserService userService;
    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userService.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Failed -> NOT FOUND USE at username: " + username));
        return UserPrincipal.build(user);
    }
    public User getCurrentUser(){
        Optional<User> user;
        String userName;
        //Lay 1 object principal trong SecurityContexHolder
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //So sanh obj voi Userdetails neu ma dung thi gan userName = principal.getUsername();
        if(principal instanceof UserDetails){
            userName = ((UserDetails) principal).getUsername();
        } else {
            //neu khong phai user hien tai thi userName = principal.toString();
            userName = principal.toString();
        }
        //kiem tra neu userName ton tai trong DB thi gan user = ham tim kiem trong DB theo userName do
        if(userRepository.existsByUsername(userName)){
            user = userService.findByUserName(userName);
        } else {
            //Neu chua ton tai thi tra ve 1 the hien cua lop User thong qua Optional.of
            user = Optional.of(new User());
            //set cho no 1 cai ten user an danh Day la truong hop ma tuong tac qua dang nhap kieu FB hay GG
            user.get().setUsername("Anonymous");
        }
        return user.get();
    }


}
