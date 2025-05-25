package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.dto.response.FileData;
import init.upinmcse.backend.dto.response.FileResponse;
import init.upinmcse.backend.enums.FileType;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.File;
import init.upinmcse.backend.repository.FileRepository;
import init.upinmcse.backend.repository.FileStoreRepository;
import init.upinmcse.backend.service.IFileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService implements IFileService {
//    @Qualifier(value = "localStoreFile")
    FileStoreRepository fileStoreRepository;
    FileRepository fileRepository;

    @Override
    public FileResponse uploadFile(MultipartFile file, Long postId, String userId, FileType fileType) throws IOException {
        // store file
        var fileInfo = fileStoreRepository.store(file);

        // save file info to database
        var fileMgmt = File.builder()
                .id(fileInfo.getName())
                .contentType(fileInfo.getContentType())
                .path(fileInfo.getPath())
                .url(fileInfo.getUrl())
                .size(fileInfo.getSize())
                .md5Checksum(fileInfo.getMd5Checksum())
                .userId(userId)
                .postId(postId)
                .fileType(fileType)
                .build();
        fileRepository.save(fileMgmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    @Override
    public FileData download(String fileName) throws IOException {
        var fileMgmt = fileRepository.findById(fileName).orElseThrow(
                () -> new ErrorException(ErrorCode.FILE_NOT_FOUND));

        var resource = fileStoreRepository.load(fileMgmt);

        return new FileData(fileMgmt.getContentType(), resource);
    }
}
