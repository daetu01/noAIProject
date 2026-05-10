package com.no.ai.trade.domain;

import com.no.ai.user.domain.UserEntity;
import jakarta.persistence.*;

@Entity
public class TradeHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity buyUser;

    @ManyToOne
    private UserEntity cellUser;

}
