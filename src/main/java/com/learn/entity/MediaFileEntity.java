package com.learn.entity;

import com.learn.constants.TableNames;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = TableNames.MEDIA_FILES)
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "file_data",columnDefinition = "bytea")
    private byte[] fileData;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    // TODO : Establish Relationship with FilledHtmlForm and User
}
