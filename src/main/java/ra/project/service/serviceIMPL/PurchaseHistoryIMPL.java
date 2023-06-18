package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.model.PurchaseHistory;
import ra.project.repository.IPurchaseHistoryRepository;
import ra.project.service.IService.IPurchaseHistoryService;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PurchaseHistoryIMPL implements IPurchaseHistoryService {
    private final IPurchaseHistoryRepository purchaseHistoryRepository;
    @Override
    public List<PurchaseHistory> findAll() {
        return purchaseHistoryRepository.findAll();
    }

    @Override
    public PurchaseHistory findById(Long id) {
        return purchaseHistoryRepository.findById(id).get();
    }

    @Override
    public PurchaseHistory save(PurchaseHistory purchaseHistory) {
        return purchaseHistoryRepository.save(purchaseHistory);
    }

    @Override
    public void deleteById(Long id) {
        purchaseHistoryRepository.deleteById(id);
    }

    @Override
    public List<PurchaseHistory> findPurchaseHistoriesByTimeBuyBetween(String from, String to) {
        if (from.compareTo(to)>0||from.compareTo(LocalDate.now().toString())>0){
            return null;
        }
        return purchaseHistoryRepository.findPurchaseHistoriesByTimeBuyBetween(from,to);
    }
}
