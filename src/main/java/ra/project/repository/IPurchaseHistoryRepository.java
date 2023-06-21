package ra.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project.model.PurchaseHistory;
import ra.project.model.User;

import java.util.List;

@Repository
public interface IPurchaseHistoryRepository extends JpaRepository<PurchaseHistory,Long> {
    List<PurchaseHistory> findPurchaseHistoriesByTimeBuyBetween(String from, String to);
}
