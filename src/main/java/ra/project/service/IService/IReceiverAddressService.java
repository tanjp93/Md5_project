package ra.project.service.IService;
import ra.project.model.ReceiverAddress;
import ra.project.model.User;

import java.util.List;

public interface IReceiverAddressService {
    List<ReceiverAddress> findAllIReceiverAddress();
    ReceiverAddress findAddressById(Long id);
    List<ReceiverAddress> findReceiverAddressByUser(User user);
    boolean existsByPhoneNumber(String phone);
    List<ReceiverAddress> findByReceiverName(String receiverName);
    void save(ReceiverAddress receiverAddress);
    void deleteAddressById(Long id);
}
