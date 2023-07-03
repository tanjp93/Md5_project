package ra.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.project.model.Product;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProduct {
    @Id
    private Long id;
    @OneToOne
    @JoinColumn(name="product")
    private Product product;
    private Long quantity;
}
