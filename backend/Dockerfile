# 1. 베이스 이미지 (Java 17)
FROM --platform=linux/amd64 amazoncorretto:17

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. JAR 파일 복사 (빌드 과정에서 생성된 파일 복사)
# GitHub Actions 워크플로에서 Gradle로 빌드한 JAR 파일이 /backend/build/libs에 위치
COPY ./build/libs/*.jar app.jar

# 4. 실행 포트 노출
EXPOSE 8080

# 5. JAR 파일 실행
CMD ["java", "-jar", "app.jar"]
