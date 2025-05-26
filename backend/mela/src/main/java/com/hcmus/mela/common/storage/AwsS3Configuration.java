package com.hcmus.mela.common.storage;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@AllArgsConstructor
public class AwsS3Configuration {

    private final AwsS3Properties awsS3Properties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsS3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                awsS3Properties.getAccessKey(),
                                awsS3Properties.getSecretKey())))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(awsS3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                awsS3Properties.getAccessKey(),
                                awsS3Properties.getSecretKey())))
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "storage.provider", havingValue = "s3")
    public StorageService s3StorageService(S3Presigner presigner, AwsS3Properties awsS3Properties) {
        return new AwsS3ServiceImpl(presigner, awsS3Properties);
    }
}
