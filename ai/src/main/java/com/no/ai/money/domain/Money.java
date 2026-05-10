package com.no.ai.money.domain;

import com.no.ai.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Money {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 갖고 있는 돈
    private Long money;

    // 누가 갖고 있는지?
    @OneToOne
    private UserEntity user;
}
