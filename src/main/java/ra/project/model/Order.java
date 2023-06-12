package ra.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
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
    @OneToOne
    @JoinColumn(name="userId")
    private User user;
    @OneToOne
    @JoinColumn(name="address")
    private ReceiverAddress address;
    @OneToMany(mappedBy = "order",targetEntity = OrderDetail.class ,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderDetail> orders;
}
