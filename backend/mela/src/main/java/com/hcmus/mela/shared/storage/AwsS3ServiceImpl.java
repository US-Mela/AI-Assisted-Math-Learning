package com.hcmus.mela.shared.storage;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.Map;

public class AwsS3ServiceImpl implements StorageService {

    private final S3Presigner presigner;
    private final AwsS3Properties awsS3Properties;

    public AwsS3ServiceImpl(S3Presigner presigner, AwsS3Properties awsS3Properties) {
        this.presigner = presigner;
        this.awsS3Properties = awsS3Properties;
    }

    @Override
    public Map<String, String> getUploadUserImagePreSignedUrl(String fileName) {
        // Generate a pre-signed URL for uploading user images
        String preSignedUrl = generateUploadPreSignedUrl(awsS3Properties.getFolder().getUsersImages() + fileName);
        String folderPath = awsS3Properties.getFolder().getUsersImages();
        String storedUrl = getStoredUrl(folderPath, fileName);

        return Map.of(
                "preSignedUrl", preSignedUrl,
                "storedUrl", storedUrl
        );
    }

    @Override
    public Map<String, String> getUploadConversationFilePreSignedUrl(String fileName) {
        // Generate a pre-signed URL for uploading user images
        String preSignedUrl = generateUploadPreSignedUrl(awsS3Properties.getFolder().getConversationsFiles() + fileName);
        String folderPath = awsS3Properties.getFolder().getConversationsFiles();
        String storedUrl = getStoredUrl(folderPath, fileName);

        return Map.of(
                "preSignedUrl", preSignedUrl,
                "storedUrl", storedUrl
        );
    }

    public String generateUploadPreSignedUrl(String fileName) {
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(awsS3Properties.getExpireTime()))
                .putObjectRequest(PutObjectRequest.builder()
                        .bucket(awsS3Properties.getBucket())
                        .key(fileName)
                        .build()));

        return presignedRequest.url().toString();
    }

    public String getStoredUrl(String folderPath, String fileName) {
        return "https://" +
                awsS3Properties.getBucket() +
                ".s3.amazonaws.com/" +
                folderPath +
                fileName;
    }
}
