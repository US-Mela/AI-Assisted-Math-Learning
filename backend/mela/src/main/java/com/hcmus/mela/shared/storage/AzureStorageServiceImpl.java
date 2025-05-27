package com.hcmus.mela.shared.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import lombok.RequiredArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class AzureStorageServiceImpl implements StorageService {

    private final BlobServiceClient blobServiceClient;

    private final AzureStorageProperties azureStorageProperties;

    public BlobClient getBlobClient(String containerName, String blobName) {
        return blobServiceClient.getBlobContainerClient(containerName)
                .getBlobClient(blobName);
    }

    public String getBlobUrl(String containerName, String blobName) {
        return getBlobClient(containerName, blobName).getBlobUrl();
    }

    @Override
    public Map<String, String> getUploadUserImagePreSignedUrl(String fileName) {
        String containerName = azureStorageProperties.getContainer().getUsers().getName();
        String blobName = azureStorageProperties.getContainer().getUsers().getImages() + fileName;
        String preSignedUrl = generateUploadPreSignedUrl(containerName, blobName);
        String storedUrl = getStoredUrl(containerName, blobName);
        return Map.of(
                "preSignedUrl", preSignedUrl,
                "storedUrl", storedUrl
        );
    }

    @Override
    public Map<String, String> getUploadConversationFilePreSignedUrl(String fileName) {
        String containerName = azureStorageProperties.getContainer().getConversations().getName();
        String blobName = azureStorageProperties.getContainer().getConversations().getFiles() + fileName;
        String preSignedUrl = generateUploadPreSignedUrl(containerName, blobName);
        String storedUrl = getStoredUrl(containerName, blobName);
        return Map.of(
                "preSignedUrl", preSignedUrl,
                "storedUrl", storedUrl
        );
    }

    public String generateUploadPreSignedUrl(String containerName, String blobName) {
        BlobSasPermission permissions = new BlobSasPermission()
                .setWritePermission(true)
                .setDeletePermission(true);

        OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(azureStorageProperties.getExpireTime());

        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions);
        String sasToken = getBlobClient(containerName, blobName).generateSas(sasValues);

        return getBlobUrl(containerName, blobName) + "?" + sasToken;
    }


    public String getStoredUrl(String containerName, String blobName) {
        return URLDecoder.decode(getBlobUrl(containerName, blobName), StandardCharsets.UTF_8);
    }
}