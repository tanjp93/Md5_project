package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.model.Category;
import ra.project.repository.ICategoryRepository;
import ra.project.service.IService.ICategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryIMPL implements ICategoryService {
    private final ICategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        if (categoryRepository.findById(id).isPresent()){
            return categoryRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsCategoryByCategoryName(String name) {
        return categoryRepository.existsCategoryByCategoryName(name);
    }
}
