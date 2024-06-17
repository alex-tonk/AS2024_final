package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.File;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class FileUploadService {
    @Autowired
    private FileRepository fileRepository;

    public File uploadFile(MultipartFile file) {
        File saved = fileRepository.save(new File(file));

        try (OutputStream os = new FileOutputStream(saved.getUuid().toString())) {
            os.write(file.getBytes());
        } catch (IOException e) {
            throw new BusinessLogicException("Не удалось сохранить файл");
        }

        return saved;
    }

    public File createFromFile(java.io.File file) {
        File saved = fileRepository.save(new File(file.getName()));

        try {
            Files.copy(file.toPath(), Path.of(saved.getUuid().toString()));
        } catch (IOException e) {
            throw new BusinessLogicException("Не удалось сохранить файл");
        }

        return saved;
    }

    public Resource serveFile(FileId fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessLogicException("Файл не найден"));
        try {
            return new UrlResource(Path.of(file.getUuid().toString()).toUri());
        } catch (MalformedURLException e) {
            throw new BusinessLogicException("Ошибка запроса файла");
        }
    }

    public void deleteFile(FileId fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessLogicException("Файл не найден"));
        try {
            java.nio.file.Files.delete(Path.of(file.getUuid().toString()));
        } catch (IOException e) {
            throw new BusinessLogicException("Ошибка удаления файла");
        }
        fileRepository.delete(file);
    }
}
