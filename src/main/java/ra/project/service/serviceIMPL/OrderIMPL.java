package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.model.Order;
import ra.project.model.User;
import ra.project.repository.IOrderRepository;
import ra.project.service.IService.IOrderService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderIMPL implements IOrderService {
    private final IOrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Long id) {
        if (orderRepository.findById(id).isPresent()) {
            return orderRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order findOrderByUser(User user) {
        return orderRepository.findOrderByUser(user);
    }
}
