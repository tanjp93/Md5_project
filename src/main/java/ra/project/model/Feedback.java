package ra.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    @NotNull
    private String content;
    @OneToMany(mappedBy = "feedback",targetEntity = Response.class,fetch =FetchType.EAGER )
    @JsonIgnore
    private List<Response> responseList;
    @OneToOne
    @JoinColumn(name = "orderDetail")
    private OrderDetail orderDetail;
    @Min(1)
    @Max(5)
    private Long vote;
}
