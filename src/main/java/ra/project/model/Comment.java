package ra.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "comments")
    private User user;
    @NotNull
    private String content;
    @OneToMany(mappedBy = "comment",targetEntity = Response.class,fetch =FetchType.EAGER )
    @JsonIgnore
    private List<Response> responseList;
    @ManyToOne
    @JoinColumn(name = "orderDetail")
    private OrderDetail orderDetail;
}
