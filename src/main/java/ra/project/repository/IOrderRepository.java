package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.Order;
import ra.project.model.User;

@Repository
public interface IOrderRepository extends JpaRepository<Order,Long> {
    Order findOrderByUser(User user);
}
