package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.model.OrderDetail;
import ra.project.repository.IOrderDetailRepository;
import ra.project.service.IService.IOrderDetailService;

import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderDetailIMPL implements IOrderDetailService {
    private final IOrderDetailRepository orderDetailRepository;
    @Override
    public List<OrderDetail> findAll() {
        return orderDetailRepository.findAll();
    }

    @Override
    public OrderDetail findById(Long id) {
        if (orderDetailRepository.findById(id).isPresent()) {
            return orderDetailRepository.findById(id).get();
        }
        return null;
    }


    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public void deleteById(Long id) {
         orderDetailRepository.deleteById(id);
    }

}
