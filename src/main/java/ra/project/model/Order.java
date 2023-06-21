package ra.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @OneToOne
    @JoinColumn(name = "userId")
    private User user;
    @OneToMany(mappedBy = "order", targetEntity = OrderDetail.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderDetail> orderDetails;
}
