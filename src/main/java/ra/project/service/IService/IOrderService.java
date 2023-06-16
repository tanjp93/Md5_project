package ra.project.service.IService;

import ra.project.model.Order;
import ra.project.model.User;
import ra.project.service.IGenerateService;

public interface IOrderService extends IGenerateService<Order,Long> {
    Order findOrderByUser(User user);
}
