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
    // 0 : in cart // 1: paid //2: to delivery // 3: delivered
    private int status;
    private float price;
//    @Column(name = "timeBuy", columnDefinition = "DATE")
    @OneToMany(mappedBy = "orderDetail",targetEntity = Feedback.class,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Feedback> feedback;
    @ManyToOne
    @JsonIgnore
    private Order order;
    @ManyToOne
    @JsonIgnore
    private PurchaseHistory purchaseHistory;
}
