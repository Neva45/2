package com.example.edu_plat.module_media.repository;

import com.example.edu_plat.module_media.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

}