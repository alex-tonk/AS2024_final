package com.prolegacy.atom2024backend.common.mail;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendSimpleMail(EmailDetails details) throws MessagingException {
        javaMailSender.send(getMimeMessage(details, null));
    }

    public void sendMailWithAttachment(EmailDetails details) throws MessagingException {
        javaMailSender.send(
                getMimeMessage(
                        details,
                        details.attachments().stream().map(attachment -> {
                            FileSystemResource file = new FileSystemResource(new File(attachment));
                            if (file.getFilename() == null) {
                                throw new BusinessLogicException("File for attachment (%s) has no filename".formatted(attachment));
                            }
                            return file;
                        }).toList()
                ));
    }

    @NotNull
    private MimeMessage getMimeMessage(EmailDetails details, List<FileSystemResource> attachments) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(sender);
        mimeMessageHelper.setTo(details.recipient());
        mimeMessageHelper.setText(details.msgBody(), true);
        mimeMessageHelper.setSubject(details.subject());

        if (attachments != null) {
            for (FileSystemResource r : attachments) {
                mimeMessageHelper.addAttachment(Objects.requireNonNull(r.getFilename()), r);
            }
        }

        return mimeMessage;
    }
}
