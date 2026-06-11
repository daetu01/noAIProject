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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_AUDIO_SIZE = 20 * 1024 * 1024;

    private static final List<String> IMAGE_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );
    private static final List<String> IMAGE_EXTENSIONS = List.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".gif",
            ".webp"
    );

    private static final List<String> AUDIO_CONTENT_TYPES = List.of(
            "audio/mpeg",
            "audio/wav",
            "audio/x-wav",
            "audio/ogg",
            "audio/mp4"
    );
    private static final List<String> AUDIO_EXTENSIONS = List.of(
            ".mp3",
            ".wav",
            ".ogg",
            ".m4a"
    );

    private final BoardRepository boardRepository;
    private final ItemRepository itemRepository;

    @Value("${app.uploadFile.dir:${user.home}}")
    private String uploadFileDir;

    @Value("${app.uploadImage.dir:${user.home}}")
    private String uploadImageDir;

    public void fileUpload(Board board, MultipartFile multipartFile) {
        Path savedPath = storeFile(
                multipartFile,
                Paths.get(uploadFileDir),
                MAX_IMAGE_SIZE,
                IMAGE_CONTENT_TYPES,
                IMAGE_EXTENSIONS
        );

        board.updateUploadDir(savedPath.toString());
        boardRepository.save(board);
    }

    public void fileUpload(Item item, MultipartFile multipartFile) {
        Path savedPath = storeFile(
                multipartFile,
                Paths.get(uploadImageDir),
                MAX_IMAGE_SIZE,
                IMAGE_CONTENT_TYPES,
                IMAGE_EXTENSIONS
        );

        item.updateUploadDir(savedPath.toString());
        itemRepository.save(item);
    }

    public String saveAudio(MultipartFile file) throws IOException {
        Path savedPath = storeFile(
                file,
                Paths.get("uploads/music"),
                MAX_AUDIO_SIZE,
                AUDIO_CONTENT_TYPES,
                AUDIO_EXTENSIONS
        );

        return "/uploads/music/" + savedPath.getFileName();
    }

    public String saveImage(MultipartFile file) throws IOException {
        Path savedPath = storeFile(
                file,
                Paths.get("uploads/cover"),
                MAX_IMAGE_SIZE,
                IMAGE_CONTENT_TYPES,
                IMAGE_EXTENSIONS
        );

        return "/uploads/cover/" + savedPath.getFileName();
    }

    private Path storeFile(
            MultipartFile file,
            Path uploadDir,
            long maxSize,
            List<String> allowedContentTypes,
            List<String> allowedExtensions
    ) {
        validateFile(file, maxSize, allowedContentTypes, allowedExtensions);

        Path baseDir = uploadDir.toAbsolutePath().normalize();
        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;
        Path targetPath = baseDir.resolve(fileName).normalize();

        if (!targetPath.startsWith(baseDir)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        try {
            Files.createDirectories(baseDir);
            Files.copy(file.getInputStream(), targetPath);
            return targetPath;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    private void validateFile(
            MultipartFile file,
            long maxSize,
            List<String> allowedContentTypes,
            List<String> allowedExtensions
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다.");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다.");
        }
    }

    private String getExtension(String originalFileName) {
        String cleanName = StringUtils.cleanPath(originalFileName == null ? "" : originalFileName);
        int dotIndex = cleanName.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == cleanName.length() - 1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }

        return cleanName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }
}
