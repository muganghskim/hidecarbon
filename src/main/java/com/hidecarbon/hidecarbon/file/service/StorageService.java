package com.hidecarbon.hidecarbon.file.service;

import com.hidecarbon.hidecarbon.config.StorageProperties;
import com.hidecarbon.hidecarbon.util.RandomStringGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class StorageService {
    private final Path uploadDir;

    public StorageService(StorageProperties storageProperties) throws IOException {
        this.uploadDir = Paths.get(storageProperties.getUploadDir());
        Files.createDirectories(this.uploadDir);
    }

    public String storeFile(MultipartFile file) throws IOException {
        try {
            log.info("경로 {}", uploadDir);
            String uuid = RandomStringGenerator.generateRandomString();
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            String randomFileName = uuid + filename;
            log.info("파일 이름: {}", filename);

            Path targetLocation = uploadDir.resolve(randomFileName);
            log.info("타겟 위치: {}", targetLocation);

            // 대상 파일이 이미 존재하는 경우 삭제 후 새로 생성
            if (Files.exists(targetLocation)) {
                Files.delete(targetLocation);
            }

            Files.copy(file.getInputStream(), targetLocation);
            log.info("파일 저장 성공");

            String fileUrl = "/img/" + randomFileName;
            log.info("저장된 파일 URL: {}", fileUrl);

            return fileUrl;
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw e;
        }
    }
}