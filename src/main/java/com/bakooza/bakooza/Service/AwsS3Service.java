package com.bakooza.bakooza.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Transactional
public interface AwsS3Service {
    public Long uploadFile(MultipartFile multipartFile, Long postId);

    public void deleteFile(String fileName);

    public List<String> getFile(List<String> fileNameList);
}
