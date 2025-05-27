package com.hcmus.mela.shared.storage;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class AzureStorageConfiguration {

    private final AzureStorageProperties azureStorageProperties;

    @Bean
    public BlobServiceClient blobServiceClient() {
        String connectionString =
                String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                        azureStorageProperties.getAccountName(),
                        azureStorageProperties.getAccountKey());
        return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    }

    @Bean
    @ConditionalOnProperty(name = "storage.provider", havingValue = "azure")
    public StorageService azureStorageService(
            BlobServiceClient blobServiceClient,
            AzureStorageProperties azureStorageProperties
    ) {
        return new AzureStorageServiceImpl(blobServiceClient, azureStorageProperties);
    }
}
