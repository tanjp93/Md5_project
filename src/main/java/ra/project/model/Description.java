package ra.project.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Description {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Lob
    private String describe;
    @Lob
    private String image;
    @ManyToOne
    @JoinColumn (name = "productId")
    private  Product product;
}
