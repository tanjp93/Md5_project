package ra.project.service.serviceIMPL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.project.model.ReceiverAddress;
import ra.project.model.User;
import ra.project.repository.IReceiverAddressRepository;
import ra.project.service.IService.IReceiverAddressService;
import ra.project.service.IService.IUserService;


import java.util.List;

@Service
public class ReceiverServiceIMPL implements IReceiverAddressService {
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
        User user = userService.findById(userId);
        if (user != null) {
            return receiverAddressRepository.findReceiverAddressByUser(user);
        }
        return null;
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
    public void save(ReceiverAddress receiverAddress) {
        receiverAddressRepository.save(receiverAddress);
    }

    @Override
    public void deleteAddressById(Long id) {
        receiverAddressRepository.deleteById(id);
    }

}
