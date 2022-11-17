### base가 되는 이미지
FROM openjdk:8-jre

### 이미지 생성 과정에서 실행할 명령어
### RUN

### 위치: 컨테이너 밖, 컨테이너 안
COPY build/libs/inandout-*.jar app.jar

### 컨테이너 시작시 실행할 스크립트
ENTRYPOINT ["java", "-Dcom.amazonaws.sdk.disableEc2Metadata=true", "-jar", "app.jar"]