package com.no.ai.item.service;

import com.no.ai.global.service.FileService;
import com.no.ai.item.domain.Item;
import com.no.ai.item.dto.ItemDto;
import com.no.ai.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    private final ModelMapper modelMapper;
    public Item registerByAdmin(ItemDto.CREATE dto) {
        Item item = modelMapper.map(dto, Item.class);
        return itemRepository.save(item);
    }
}
