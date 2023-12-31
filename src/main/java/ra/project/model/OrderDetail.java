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
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @OneToOne
    @JoinColumn(name="product")
    private Product product;
    @NotNull
    private Long quantity;
    //-1: bi huy
    // 0 : in cart // 1: paid //2: to delivery // 3: delivered
    private int status;
    private float price;
//    @Column(name = "timeBuy", columnDefinition = "DATE")
    @OneToOne(mappedBy = "orderDetail",targetEntity = Feedback.class,fetch = FetchType.LAZY)
    @JsonIgnore
    private Feedback feedback;
    @ManyToOne
    @JsonIgnore
    private Order order;
    @ManyToOne
    @JsonIgnore
    private PurchaseHistory purchaseHistory;
}
