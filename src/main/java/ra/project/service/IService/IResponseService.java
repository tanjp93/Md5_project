package ra.project.service.IService;

import ra.project.model.Feedback;
import ra.project.model.Response;
import ra.project.model.User;
import ra.project.service.IGenerateService;

import java.util.List;

public interface IResponseService extends IGenerateService<Response,Long> {
     boolean checkUserResponseRole(User userLogin, Long feedBackId);
     List<Response> findResponsesByFeedback(Feedback feedback);
}
