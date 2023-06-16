package ra.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private String fullName;

    @Lob
    private String avatar;
    private String email;

    /**
     * LAZY: in các field;
     * EAGER: in all field (bao gồm relationship);
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roles;
    @OneToMany(mappedBy = "user",targetEntity = ReceiverAddress.class,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ReceiverAddress> receiverAdressList;
    @OneToOne
    @JsonIgnore
    private PurchaseHistory PurchaseHistory;
    private boolean status;

}
