package ra.project.service;
import ra.project.model.ReceiverAddress;
import ra.project.model.User;

import java.util.List;
import java.util.Optional;

public interface IReceiverAddressService {
    List<ReceiverAddress> findAllIReceiverAddress();
    ReceiverAddress findAddressById(Long id);
    List<ReceiverAddress> findAllByUser(Long userId);
    boolean existsByPhoneNumber(String phone);
    List<ReceiverAddress> findByReceiverName(String receiverName);
    ReceiverAddress save(ReceiverAddress receiverAddress);
    void deleteAddressById(Long id);
}
