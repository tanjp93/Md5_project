package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.project.dto.request.CountProductByVote;
import ra.project.model.Category;
import ra.project.model.Product;
import ra.project.repository.IProductRepository;
import ra.project.service.IService.IProductService;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductIMPL implements IProductService {
    private final IProductRepository productRepository;
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        if (productRepository.findById(id).isPresent()){
            return productRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
    @Override
    public List<Product> findProductsByCategory(Category category){
        return productRepository.findProductsByCategory(category);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public boolean existsProductByProductName(String name) {
        return productRepository.existsProductByProductName(name);
    }

    @Override
    public   List<Product> findProductByVote(Long stt) {
        return productRepository.getProductByVote(stt);
    }

}
