package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.response.FileData;
import init.upinmcse.backend.dto.response.FileResponse;
import init.upinmcse.backend.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    FileResponse uploadFile(MultipartFile file, Long postId, String userId, FileType fileType) throws IOException;
    FileData download(String fileName) throws IOException;
}
