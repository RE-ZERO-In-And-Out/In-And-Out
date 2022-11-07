package com.rezero.inandout.awss3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.rezero.inandout.exception.AwsS3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsS3ServiceImpl implements AwsS3Service{

    private final AmazonS3Client amazonS3Client;

    @Value(value = "${cloud.aws.bucket.name}")
    private String s3Bucket;

    @Override
    public String getImageUrl(String s3ImageKey) {
        log.info(s3ImageKey + "에 대한 Image Url 가져옴");
        return amazonS3Client.getUrl(s3Bucket, s3ImageKey).toString();
    }

    @Override
    public String addImageAndGetKey(String dir, MultipartFile file) {
        String key = dir + "/" + LocalDateTime.now() + file.getOriginalFilename();

        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(file.getContentType());
        objectMetaData.setContentLength(file.getSize());

        // S3에 업로드
        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(s3Bucket, key, file.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
            log.error("Image 등록 완료 : " + key);
        } catch (IOException e) {
            log.error("Image 등록 중 익셉션 발생 : " + e.getMessage());
            throw new AwsS3Exception(e.getMessage());
        }

        return key;
    }

    @Override
    public void deleteImage(String key) {
        amazonS3Client.deleteObject(s3Bucket, key);
        log.error("Image 삭제 완료 : " + key);
    }

}
