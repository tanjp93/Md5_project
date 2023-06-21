package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.model.Description;
import ra.project.model.Product;
import ra.project.repository.IDescriptionRepository;
import ra.project.service.IService.IDescriptionService;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DescriptionIMPL implements IDescriptionService {
    private  final IDescriptionRepository descriptionRepository;
    @Override
    public List<Description> findAll() {
        return descriptionRepository.findAll();
    }

    @Override
    public Description findById(Long id) {
        if (descriptionRepository.findById(id).isPresent()){
            return descriptionRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public Description save(Description description) {
        return descriptionRepository.save(description);
    }

    @Override
    public void deleteById(Long id) {
        if (findById(id)!=null){
            descriptionRepository.deleteById(id);
        }
    }

    @Override
    public List<Description> findDescriptionsByProduct(Product product) {
        return descriptionRepository.findDescriptionsByProduct(product);
    }

    @Override
    public boolean existsByDescribe(String des) {
        return descriptionRepository.existsByDescribe(des);
    }
}
