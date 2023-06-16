package ra.project.service.IService;
import ra.project.model.ReceiverAddress;

import java.util.List;
import java.util.Optional;

public interface IReceiverAddressService {
    List<ReceiverAddress> findAllIReceiverAddress();
    ReceiverAddress findAddressById(Long id);
    List<ReceiverAddress> findAllByUser(Long userId);
    boolean existsByPhoneNumber(String phone);
    List<ReceiverAddress> findByReceiverName(String receiverName);
    void save(ReceiverAddress receiverAddress);
    void deleteAddressById(Long id);
}
