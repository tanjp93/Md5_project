package ra.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateForm {
    private Long id;
    @NotNull
    private String oldPassword;
    @NotNull
    private String password;
    private String fullName;
    private String avatar;
    private String email;
}
