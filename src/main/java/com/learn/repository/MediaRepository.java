package com.learn.repository;

import com.learn.entity.MediaFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<MediaFileEntity, Long> {

}
