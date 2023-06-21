package ra.project.service.IService;

import ra.project.model.Description;
import ra.project.model.Product;
import ra.project.service.IGenerateService;

import java.util.List;

public interface IDescriptionService extends IGenerateService<Description,Long> {
    List<Description>findDescriptionsByProduct(Product product);
    boolean existsByDescribe(String des);
}
