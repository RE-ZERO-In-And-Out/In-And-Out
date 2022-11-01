package com.rezero.inandout.awss3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AwsS3ServiceImpl 테스트")
class AwsS3ServiceImplTest {

    @Mock
    private AmazonS3Client amazonS3Client;

    @InjectMocks
    private AwsS3ServiceImpl awsS3ServiceImpl;

    @Test
    @DisplayName("이미지 조회")
    void getImageUrlTest_success() throws MalformedURLException {
        //given
        String s3ImageKey = "anyKey";
        URL url = new URL("https://inandoutimagebucket.s3.ap-northeast-2" +
                ".amazonaws.com/2022-10-31T17%3A36%3A50.822%20diary%20%E1%84" +
                "%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%A1%E1%84%8C%E1%85%B5.jpg");

        given(amazonS3Client.getUrl(any(), any()))
                .willReturn(url);
        //when
        String urlString = awsS3ServiceImpl.getImageUrl(s3ImageKey);

        //then
        assertEquals(urlString, url.toString());
    }

    @Test
    @DisplayName("이미지 등록 - 성공")
    void addImageAndGetKeyTest_sucess() {
        //given
        PutObjectResult result = new PutObjectResult();

        MockMultipartFile file = new MockMultipartFile("file",
                "test.png",
                "image/png",
                "«‹png data>>".getBytes());

        given(amazonS3Client.putObject(any()))
                .willReturn(result);
        //when
        String key2 = awsS3ServiceImpl.addImageAndGetKey("anyDir", file);

        //then
        assertEquals(key2, key2);
    }

    @Test
    @DisplayName("이미지 삭제 - 성공")
    void deleteImageTest() {
        //given
        String s3ImageKey = "anyKey";

        //when
        awsS3ServiceImpl.deleteImage(s3ImageKey);

        ArgumentCaptor<String> bucketCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        //then
        verify(amazonS3Client, times(1))
                .deleteObject(bucketCaptor.capture(), keyCaptor.capture());
    }
}