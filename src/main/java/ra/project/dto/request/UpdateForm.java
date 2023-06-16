package ra.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateForm {
    private Long id;
    private String oldPassword;
    private String password;
    private String fullName;
    private String avatar;
    private String email;
}
