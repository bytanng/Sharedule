package com.sharedule.app.controller.s3;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class S3UploadController {
    private static final String BUCKET_NAME = "sharedule";
    private final S3Client s3Client;

    // Constructor to initialize S3Client
    public S3UploadController() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                System.getenv("AWS_ACCESS_KEY_ID"),
                System.getenv("AWS_SECRET_ACCESS_KEY")
        );

        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Save MultipartFile to a temporary file
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            // Define S3 key (path in bucket)
            String key = "uploads/" + file.getOriginalFilename();

            // Upload the file to S3
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(key)
                            .build(),
                    RequestBody.fromFile(tempFile));

            // Delete the temporary file after upload
            tempFile.delete();

            return ResponseEntity.ok("File uploaded successfully: " + key);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }


}
