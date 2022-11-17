package com.rezero.inandout.awss3;

import org.springframework.web.multipart.MultipartFile;

public interface AwsS3Service {
    String getImageUrl(String s3ImageKey);
    String addImageAndGetKey(String dir, MultipartFile file);
    void deleteImage(String oldKey);
}
