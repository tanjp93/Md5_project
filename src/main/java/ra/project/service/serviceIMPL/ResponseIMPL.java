package ra.project.service.serviceIMPL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.project.model.Feedback;
import ra.project.model.OrderDetail;
import ra.project.model.Response;
import ra.project.model.User;
import ra.project.repository.IResponseRepository;
import ra.project.service.IService.IFeedbackService;
import ra.project.service.IService.IOrderDetailService;
import ra.project.service.IService.IResponseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseIMPL implements IResponseService {
    private final IResponseRepository responseRepository;
    private final IFeedbackService feedbackService;
    private final IOrderDetailService orderDetailService;

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
    @Override
    public boolean checkUserResponseRole(User userLogin, Long feedBackId){
        Feedback feedback = feedbackService.findById(feedBackId);
        if (userLogin==null||feedback==null) {
            return false;
        }
        User userBuy=null;
        OrderDetail orderDetail = orderDetailService.findById(feedback.getOrderDetail().getId());
        if (orderDetail.getOrder()!=null){
            userBuy=orderDetail.getOrder().getUser();
        }else {
            userBuy=orderDetail.getPurchaseHistory().getUser();
        }
        if (userBuy.getId()!=userLogin.getId()){
            return false;
        }
        return true;
    }
}
