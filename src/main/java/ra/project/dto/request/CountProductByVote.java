package ra.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.project.model.Product;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountProductByVote {
    private Long id;
    private Product productName;
    private BigInteger count;
}
