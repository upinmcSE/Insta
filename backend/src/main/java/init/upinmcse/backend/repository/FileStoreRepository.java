package init.upinmcse.backend.repository;

import init.upinmcse.backend.dto.response.FileInfo;
import init.upinmcse.backend.model.File;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public interface FileStoreRepository {
    FileInfo store(MultipartFile file) throws IOException;
    Resource load(File file) throws IOException;
}
