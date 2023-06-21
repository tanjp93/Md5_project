package ra.project.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name  = "user_id")
    private User user;
    @NotBlank
    private String receiverName;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String address;
}
