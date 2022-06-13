package com.bakooza.bakooza.Controller;

import com.bakooza.bakooza.Service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

//    @PostMapping("/file") // 이미지 업로드
//    public ResponseEntity<List<String>> uploadFile(@RequestPart List<MultipartFile> multipartFile) {
//        return new ResponseEntity<>(awsS3Service.uploadFile(multipartFile), HttpStatus.OK);
//    }

    @DeleteMapping("/image") // 이미지 삭제
    public ResponseEntity<Void> deleteFile(@RequestParam String fileName) {
        awsS3Service.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/image") // 이미지 조회
    public ResponseEntity<List<String>> getFile(@RequestParam List<String> fileNameList){
        return new ResponseEntity<>(awsS3Service.getFile(fileNameList), HttpStatus.OK);
    }
}
