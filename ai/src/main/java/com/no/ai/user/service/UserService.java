package com.no.ai.user.service;

import com.no.ai.global.security.dto.CustomUserInfoDto;
import com.no.ai.global.security.jwt.JwtUtil;
import com.no.ai.inventory.domain.Inventory;
import com.no.ai.inventory.domain.InventoryItem;
import com.no.ai.inventory.repository.InventoryItemRepository;
import com.no.ai.inventory.repository.InventoryRepository;
import com.no.ai.item.domain.Item;
import com.no.ai.money.domain.Money;
import com.no.ai.money.repository.MoneyRepository;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.domain.UserRole;
import com.no.ai.user.dto.LoginRequestDto;
import com.no.ai.user.dto.UserDTO;
import com.no.ai.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper mapper;
    private final MoneyRepository moneyRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public Long create(UserEntity entity) {

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        if (entity.getEmail().equals("daiswhat@gmail.com")){
            entity.setUserRole(UserRole.ADMIN);
        } else {
            entity.setUserRole(UserRole.USER);
        }


        // 유저 저장하기
        UserEntity userEntity = userRepository.save(entity);
        Money money = Money.builder()
                .user(entity)
                .money(1000L)
                .build();
        userEntity.setMoney(money);

        moneyRepository.save(money);
        return userRepository.save(userEntity).getId();
    }

    public String login(LoginRequestDto userDto) {
        // login
        UserEntity user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 잘못됐습니다."));


        System.out.println(user.getNickName() + user.getEmail() + user.getPassword());
        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        CustomUserInfoDto loginDto = mapper.map(user, CustomUserInfoDto.class);

        UserEntity userEntity = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 이메일입니다."));

        loginDto.setUserId(userEntity.getId());
        loginDto.setUserRole(userEntity.getUserRole());
        // 3. 인증 성공 시 JWT 토큰 생성 및 반환
        return jwtUtil.createAccessToken(loginDto);
    }

    @Transactional
    public void earnItem(String email, Item item) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("없는 유저입니다."));

        Inventory inventory = user.getInventory();

        for (InventoryItem invenItem : inventory.getInventoryItems()) {
            if (invenItem.getItem().getId().equals(item.getId())) {

                invenItem.addQuantity(1);

                inventoryItemRepository.save(invenItem);

                return;
            }

        }

        // 만약 이미 있을 수도 있으니까
        InventoryItem inventoryItem = InventoryItem.builder()
                        .inventory(inventory)
                        .item(item)
                        .quantity(1)
                        .build();

        inventoryItemRepository.save(inventoryItem);


        ;
    }
}
