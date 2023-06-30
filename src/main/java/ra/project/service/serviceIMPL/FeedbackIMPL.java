package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.model.Feedback;
import ra.project.model.OrderDetail;
import ra.project.repository.IFeedbackRepository;
import ra.project.service.IService.IFeedbackService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackIMPL implements IFeedbackService {
    private  final IFeedbackRepository feedbackRepository;
    @Override
    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback findById(Long id) {
        if (feedbackRepository.findById(id).isPresent()){
            return feedbackRepository.findById(id).get();
        }
       return null;
    }

    @Override
    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public void deleteById(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public List<Feedback> findFeedbacksByOrderDetail(OrderDetail o) {
        return feedbackRepository.findFeedbacksByOrderDetail(o);
    }
}
