package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.FileId;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "text")
    private String fileName;

    public File(String fileName) {
        this.fileName = fileName;
    }

    public File(MultipartFile file) {
        this.fileName = file.getOriginalFilename();
    }

    @PrePersist
    protected void onCreate() {
        setUuid(java.util.UUID.randomUUID());
    }
}
