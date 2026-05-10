package com.no.ai.global.service;

import com.no.ai.board.domain.Board;
import com.no.ai.board.repository.BoardRepository;
import com.no.ai.item.domain.Item;
import com.no.ai.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileService {
    private final BoardRepository boardRepository;
    private final ItemRepository itemRepository;

    // application.properties 에 app.upload.dir을 정의하고, 없는 경우에 default 값으로 user.home (System에 종속적인)
    @Value("${app.uploadFile.dir:${user.home}}")
    private String uploadFileDir;
    @Value("${app.uploadImage.dir:${user.home}}")
    private String uploadImageDir;

    public void fileUpload(Board board, MultipartFile multipartFile) {
        Path copyOfLocation = Paths.get(uploadFileDir + File.separator + StringUtils.cleanPath(multipartFile.getOriginalFilename()));

        board.updateUploadDir(uploadFileDir);

        boardRepository.save(board);

        try {
            // inputStream을 가져와서
            // copyOfLocation (저장위치)로 파일을 쓴다.
            // copy의 옵션은 기존에 존재하면 REPLACE(대체한다), 오버라이딩 한다.
            Files.copy(multipartFile.getInputStream(), copyOfLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not store file : " + multipartFile.getOriginalFilename());
        }
    }


    public void fileUpload(Item item, MultipartFile multipartFile) {
        Path copyOfLocation = Paths.get(uploadImageDir + File.separator + StringUtils.cleanPath(multipartFile.getOriginalFilename()));

        item.updateUploadDir(uploadImageDir);

        itemRepository.save(item);
        try {
            // inputStream을 가져와서
            // copyOfLocation (저장위치)로 파일을 쓴다.
            // copy의 옵션은 기존에 존재하면 REPLACE(대체한다), 오버라이딩 한다.
            Files.copy(multipartFile.getInputStream(), copyOfLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not store file : " + multipartFile.getOriginalFilename());
        }
    }
}
