package com.prolegacy.atom2024backend.entities.chat;

import com.prolegacy.atom2024backend.dto.chat.AttachmentDto;
import com.prolegacy.atom2024backend.entities.ids.chat.AttachmentId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.net.URI;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    @Id
    private AttachmentId id;
    @ManyToOne
    private Message message;

    @Column(columnDefinition = "varchar")
    private URI uri;

    public Attachment(Message message, AttachmentDto attachmentDto) {
        this.message = message;
        this.uri = attachmentDto.getUri();
    }
}
