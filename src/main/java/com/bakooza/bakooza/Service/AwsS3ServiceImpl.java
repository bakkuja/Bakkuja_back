package com.bakooza.bakooza.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bakooza.bakooza.Entity.PostImage;
import com.bakooza.bakooza.Repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3ServiceImpl implements AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final PostImageRepository postImageRepository;

    public Long uploadFile(MultipartFile multipartFile, Long postId) {
//        List<String> fileNameList = new ArrayList<>();

//         forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        if (!multipartFile.isEmpty()) {
//            multipartFile.forEach(file -> {
//                String fileName = createFileName(file.getOriginalFilename());
//                ObjectMetadata objectMetadata = new ObjectMetadata();
//                objectMetadata.setContentLength(file.getSize());
//                objectMetadata.setContentType(file.getContentType());
//
//                try (InputStream inputStream = file.getInputStream()) {
//                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
//                                               .withCannedAcl(CannedAccessControlList.PublicRead)); // 누구나 볼 수 있도록 PublicRead
//                } catch (IOException e) {
//                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
//                }
//
//                DecimalFormat decimalFormat = new DecimalFormat("0.00");
//                float size = (float) file.getSize() / 1024; // kb
//                String unit = "KB";
//                if (size > 1024) {
//                    size /= 1024; // mb
//                    unit = "MB";
//                }
//
//                PostImage postImage = PostImage.builder()
//                        .postId(postId)
//                        .imagePath(fileName)
//                        .imageSize(decimalFormat.format(size) + unit)
//                        .uploadDate(LocalDate.now())
//                        .build();
//
//                postImageRepository.save(postImage);
//            });
//        }
            String fileName = createFileName(multipartFile.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            try (InputStream inputStream = multipartFile.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                                           .withCannedAcl(CannedAccessControlList.PublicRead)); // 누구나 볼 수 있도록 PublicRead
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            float size = (float) multipartFile.getSize() / 1024; // kb
            String unit = "KB";
            if (size > 1024) {
                size /= 1024; // mb
                unit = "MB";
            }

            PostImage postImage = PostImage.builder()
                    .postId(postId)
                    .imagePath("https://bakooza.s3.ap-northeast-2.amazonaws.com/" + fileName)
                    .imageSize(decimalFormat.format(size) + unit)
                    .uploadDate(LocalDate.now())
                    .build();

            postImageRepository.save(postImage);
        }
        return postId;
    }

    public void deleteFile(String fileName) { // 이미지 삭제
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        postImageRepository.deleteByImagePath(fileName);
    }

    private String createFileName(String fileName) { // 생성된 날짜를 이름으로 하는 폴더에 UUID를 이용해 파일의 이름이 겹치지 않도록 함
        return LocalDate.now() + "/" + UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단함
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    public List<String> getFile(List<String> fileNameList) {
        List<String> files = new ArrayList<>();
        for (String fileName : fileNameList) {
            files.add(amazonS3.getUrl(bucket, fileName).toString());
        }
        return files;
    }
}