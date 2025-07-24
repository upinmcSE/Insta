package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.constant.FileType;
import init.upinmcse.backend.dto.response.FileData;
import init.upinmcse.backend.dto.response.FileResponse;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.File;
import init.upinmcse.backend.repository.db.FileRepository;
import init.upinmcse.backend.service.IFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Primary
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class S3FileService implements IFileService {

    @Value("${minio.bucket-name}")
    @NonFinal
    String bucketName;

    @Value("${app.file.download-prefix}")
    @NonFinal
    String urlPrefix;

    FileRepository fileRepository;
    MinioClient minioClient;

    @Override
    public FileResponse uploadFile(MultipartFile file, Long postId, String userId, FileType fileType) throws IOException {
        String fileExtension = StringUtils
                .getFilenameExtension(file.getOriginalFilename());

        String fileName = Objects.isNull(fileExtension)
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + fileExtension;

        try{
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }catch (Exception e) {
            log.error("Error uploading file to MinIO: {}", e.getMessage(), e);
            throw new ErrorException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        var fileMgmt = File.builder()
                .id(fileName)
                .contentType(file.getContentType())
                .path(bucketName + "/" + fileName)
                .url(urlPrefix + "/" + fileName)
                .size(file.getSize())
                .md5Checksum(DigestUtils.md5DigestAsHex(file.getInputStream()))
                .userId(userId)
                .postId(postId)
                .fileType(fileType)
                .build();

        fileRepository.save(fileMgmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileMgmt.getUrl())
                .build();
    }

    @Override
    public FileData download(String fileName) {
        try {
            var fileMgmt = fileRepository.findById(fileName)
                    .orElseThrow(() -> new ErrorException(ErrorCode.FILE_NOT_FOUND));

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            return new FileData(fileMgmt.getContentType(), new InputStreamResource(stream));

        } catch (Exception e) {
            log.error("Error downloading file from MinIO: {}", e.getMessage(), e);
            throw new ErrorException(ErrorCode.FILE_NOT_FOUND);
        }
    }
}
