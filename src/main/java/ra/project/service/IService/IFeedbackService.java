package ra.project.service.IService;

import ra.project.model.Feedback;
import ra.project.model.OrderDetail;
import ra.project.service.IGenerateService;

import java.util.List;

public interface IFeedbackService extends IGenerateService<Feedback,Long> {
    List<Feedback> findFeedbacksByOrderDetail(OrderDetail o);
}
