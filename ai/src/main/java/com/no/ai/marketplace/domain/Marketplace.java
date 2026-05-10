package com.no.ai.marketplace.domain;

import com.no.ai.item.domain.Item;
import com.no.ai.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Marketplace {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity registerUser;

    @ManyToOne
    private Item item;

    private Long price;

    @CreationTimestamp
    private LocalDateTime registeredAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateItem(Item item) {
        this.item = item;
    }
    public void updateUser(UserEntity user) {
        this.registerUser = user;
    }
}
