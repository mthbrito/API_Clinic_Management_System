package ifpb.api_clinic_management_system.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TB_DOCTOR")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String crm;

    @Column(nullable = false, length = 50)
    private String specialty;

    @Column(length = 20)
    private String phone;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
