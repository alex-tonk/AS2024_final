package com.prolegacy.atom2024backend.common.mail;

import lombok.Builder;

import java.util.List;

@Builder
public record EmailDetails(
        String recipient,
        // TODO
        List<String> copyRecipients,
        String msgBody,
        String subject,
        List<String> attachments
) {
}