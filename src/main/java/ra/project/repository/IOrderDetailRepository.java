package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Order;
import ra.project.model.OrderDetail;

import java.util.List;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    List<OrderDetail>findOrderDetailsByOrder(Order order);
}

