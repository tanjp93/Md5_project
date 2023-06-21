package ra.project.service.IService;

import ra.project.model.PurchaseHistory;
import ra.project.model.User;
import ra.project.service.IGenerateService;

import java.util.List;

public interface IPurchaseHistoryService extends IGenerateService<PurchaseHistory,Long> {
    List<PurchaseHistory> findPurchaseHistoriesByTimeBuyBetween(String from, String to);
}
