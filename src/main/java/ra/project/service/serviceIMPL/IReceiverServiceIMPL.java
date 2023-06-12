package ra.project.service.serviceIMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.project.model.ReceiverAddress;
import ra.project.model.User;
import ra.project.repository.IReceiverAddressRepository;
import ra.project.service.IReceiverAddressService;
import ra.project.service.IUserService;

import java.util.List;
@Service
public class IReceiverServiceIMPL implements IReceiverAddressService {
    @Autowired
    private IReceiverAddressRepository receiverAddressRepository;
    @Autowired
    private IUserService userService;
    @Override
    public List<ReceiverAddress> findAllIReceiverAddress() {
        return receiverAddressRepository.findAll();
    }

    @Override
    public ReceiverAddress findAddressById(Long id) {
        return receiverAddressRepository.findById(id).get();
    }

    @Override
    public List<ReceiverAddress> findAllByUser(Long userId) {
        User user=userService.findUserById(userId);
        return receiverAddressRepository.findReceiverAddressByUser(user);
    }

    @Override
    public boolean existsByPhoneNumber(String phone) {
        return receiverAddressRepository.existsByPhoneNumber(phone);
    }

    @Override
    public List<ReceiverAddress> findByReceiverName(String receiverName) {
        return receiverAddressRepository.findByReceiverNameContaining(receiverName);
    }

    @Override
    public ReceiverAddress save(ReceiverAddress receiverAddress) {
        return receiverAddressRepository.save(receiverAddress);
    }

    @Override
    public void deleteAddressById(Long id) {
        receiverAddressRepository.deleteById(id);
    }

}
