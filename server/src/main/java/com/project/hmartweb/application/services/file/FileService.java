package com.project.hmartweb.application.services.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService {
    @Value("${cloud.aws.bucket-name}")
    private String bucketName;

    private final AmazonS3 amazonS3;


    @Override
    public String uploadFile(MultipartFile multipartFile) {
        File file = convert(multipartFile);
        String fileName = System.currentTimeMillis() + "-" + file.getName().replaceAll("\\s", "_");
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        file.delete();
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    @Override
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException("Error occurred while download the file: " + exception);
        }
    }

    @Override
    public String deleteFile(String fileName) {
        amazonS3.deleteObject(bucketName, fileName);
        return fileName;
    }

    private File convert(MultipartFile multipartFile) {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(multipartFile.getBytes());
        } catch (IOException exception) {
            throw new RuntimeException("Error occurred while convert multipart file into file: " + exception);
        }

        return file;
    }
}
