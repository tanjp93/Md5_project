package ra.project.service.IService;

import ra.project.model.Order;
import ra.project.model.OrderDetail;
import ra.project.service.IGenerateService;

import java.util.List;

public interface IOrderDetailService extends IGenerateService<OrderDetail,Long> {
    List<OrderDetail>findOrderDetailsByOrder(Order order);
}
