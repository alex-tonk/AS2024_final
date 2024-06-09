package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.chat.AttachmentDto;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private FileId id;
    private UUID uuid;

    private String fileName;

    public File(AttachmentDto attachmentDto) {
        this.fileName = attachmentDto.getFileName();
    }

    public File(MultipartFile file) {
        this.fileName = file.getOriginalFilename();
    }

    @PrePersist
    protected void onCreate() {
        setUuid(java.util.UUID.randomUUID());
    }
}
