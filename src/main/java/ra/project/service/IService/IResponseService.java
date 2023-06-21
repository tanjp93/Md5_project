package ra.project.service.IService;

import ra.project.model.Response;
import ra.project.model.User;
import ra.project.service.IGenerateService;

public interface IResponseService extends IGenerateService<Response,Long> {
     boolean checkUserResponseRole(User userLogin, Long feedBackId);
}
