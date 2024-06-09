package com.prolegacy.atom2024backend.entities.chat;

import com.prolegacy.atom2024backend.dto.chat.AttachmentDto;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.entities.ids.chat.AttachmentId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private AttachmentId id;
    private FileId fileId;
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    public Attachment(Message message, AttachmentDto attachmentDto) {
        this.message = message;
        this.fileId = attachmentDto.getFileId();
    }
}
