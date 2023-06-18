package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.model.Response;
import ra.project.repository.IResponseRepository;
import ra.project.service.IService.IResponseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseIMPL implements IResponseService {
    private final IResponseRepository responseRepository;

    @Override
    public List<Response> findAll() {
        return responseRepository.findAll();
    }

    @Override
    public Response findById(Long id) {
        if (responseRepository.findById(id).isPresent()) {
            return responseRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public Response save(Response response) {
        return responseRepository.save(response);
    }

    @Override
    public void deleteById(Long id) {
        responseRepository.deleteById(id);
    }
}
