package ra.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    private String timeBuy;
    @NotNull
    @OneToOne
    @JoinColumn(name = "address")
    private ReceiverAddress address;
    @OneToMany(mappedBy = "purchaseHistory", targetEntity = OrderDetail.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderDetail> orderDetails;
}
