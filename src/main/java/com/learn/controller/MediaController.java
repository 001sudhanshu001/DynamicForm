package com.learn.controller;

import com.learn.entity.MediaFileEntity;
import com.learn.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dynamic-form")
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/save-file")
    public Long saveFile(@RequestParam("file") MultipartFile file) {
        return mediaService.uploadFile(List.of(file)).getFirst();
    }

    @PostMapping("/save-files")
    public List<Long> saveFiles(@RequestParam("files") List<MultipartFile> files) {
        return mediaService.uploadFile(files);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/download-file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam Long fileId) {
        MediaFileEntity mediaFile = mediaService.fetchById(fileId);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(mediaFile.getMediaType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
//                  + mediaFile.getOriginalFileName() + "\"") // To download
                .body(mediaFile.getFileData());
    }

    @DeleteMapping("/delete-file")
    public void deleteImage(@RequestParam Long fileId) {
        mediaService.deleteFile(fileId);
    }
}
