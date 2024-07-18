package com.learn.service;

import com.learn.entity.MediaFileEntity;
import com.learn.repository.MediaRepository;
import com.learn.utils.ExceptionHelperUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    @Transactional
    public List<Long> uploadFile(List<MultipartFile> files) {
        List<Long> fileIds = new ArrayList<>();
        for (MultipartFile file : files) {
            Long fileId = persistFile(file);
            fileIds.add(fileId);
        }
        return fileIds;
    }

    @SneakyThrows
    private Long persistFile(MultipartFile file) {
        MediaFileEntity imageData = MediaFileEntity.builder()
                .originalFileName(file.getOriginalFilename())
                .mediaType(file.getContentType())
                .fileData(file.getBytes())
                .build();

        MediaFileEntity mediaFile = mediaRepository.save(imageData);
        return mediaFile.getId();
    }

    public MediaFileEntity fetchById(Long imageId) {
        return mediaRepository.findById(imageId).orElseThrow(ExceptionHelperUtils.notFoundException(imageId));
    }

    @Transactional
    public void deleteFile(Long mediaId) {
        mediaRepository.deleteById(mediaId);
    }
}
