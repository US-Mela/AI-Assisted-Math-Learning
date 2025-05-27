package com.hcmus.mela.shared.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private Folder folder;
    private int expireTime;

    @Setter
    @Getter
    public static class Folder {
        private String usersImages;
        private String conversationsFiles;
    }
}
