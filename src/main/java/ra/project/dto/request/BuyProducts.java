package ra.project.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ra.project.model.OrderDetail;
import ra.project.model.ReceiverAddress;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "receiverAddress")
    private ReceiverAddress receiverAddress;
    @OneToMany
    @JoinColumn(name = "orderDetail")
    private List<OrderDetail>orderDetailList;
}
