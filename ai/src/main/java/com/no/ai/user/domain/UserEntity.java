package com.no.ai.user.domain;

import com.no.ai.money.domain.Money;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;
    private String email;
    private String password;
    private UserRole userRole;

    // 한 사람당 지갑은 한 개니까?
    @OneToOne
    private Money money;
}
