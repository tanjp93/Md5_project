package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.project.model.ReceiverAddress;
import ra.project.model.User;
import java.util.List;

public interface IReceiverAddressRepository extends JpaRepository<ReceiverAddress,Long> {
    boolean existsByPhoneNumber(String phone);
    List<ReceiverAddress>findByReceiverNameContaining(String findByReceiverName);
    List<ReceiverAddress>findReceiverAddressByUser(User user);
    ReceiverAddress save(ReceiverAddress ReceiverAddress);
}
