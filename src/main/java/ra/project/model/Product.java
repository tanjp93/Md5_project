package ra.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String productName;
    @Min(value = 0, message = "The value must be at least 0")
    @NotNull
    private Long stoke;
    @NotNull
    private float price;
    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;
    @Lob
    private String image;
    private String title;
    @OneToMany(mappedBy = "product",targetEntity = Description.class,fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Description> descriptions;
}
