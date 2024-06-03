package com.prolegacy.atom2024backend.readers;

import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.QUser;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.query.query.JPAQuery;
import com.prolegacy.atom2024backend.common.query.query.JPAQueryFactory;
import com.prolegacy.atom2024backend.dto.chat.AttachmentDto;
import com.prolegacy.atom2024backend.dto.chat.ChatDto;
import com.prolegacy.atom2024backend.dto.chat.MessageDto;
import com.prolegacy.atom2024backend.entities.chat.QAttachment;
import com.prolegacy.atom2024backend.entities.chat.QChat;
import com.prolegacy.atom2024backend.entities.chat.QMessage;
import com.prolegacy.atom2024backend.entities.ids.chat.ChatId;
import com.prolegacy.atom2024backend.entities.ids.chat.MessageId;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class ChatReader {
    private static final QChat chat = QChat.chat;
    private static final QMessage message = QMessage.message;
    private static final QMessage lastMessage = new QMessage("lastMessage");
    private static final QAttachment attachment = QAttachment.attachment;
    private static final QUser user = QUser.user;

    @Autowired
    private JPAQueryFactory queryFactory;

    public ChatDto getChat(ChatId chatId) {
        ChatDto chat = baseQuery().where(ChatReader.chat.id.eq(chatId)).fetchFirst();
        if (chat == null) return null;
        chat.setMessages(getMessages(chatId));
        chat.setMembers(getMembers(chatId));
        return chat;
    }

    public List<ChatDto> getChats(UserId userId) {
        return baseQuery()
                .innerJoin(chat.members, user).where(user.id.eq(userId))
                .orderBy(lastMessage.createdDate.desc().nullsLast())
                .orderBy(chat.id.desc())
                .fetch();
    }

    public MessageDto getMessage(ChatId chatId, MessageId messageId) {
        return messageQuery().where(message.chat.id.eq(chatId).and(message.id.eq(messageId))).fetchFirst();
    }

    public List<MessageDto> getMessages(ChatId chatId) {
        List<MessageDto> messages = messageQuery()
                .innerJoin(chat).on(chat.id.eq(message.chat.id))
                .where(chat.id.eq(chatId))
                .fetch();

        Map<MessageId, List<AttachmentDto>> messageIdListMap = attachmentQuery()
                .innerJoin(message).on(message.id.eq(attachment.message.id))
                .where(message.chat.id.eq(chatId))
                .stream()
                .collect(Collectors.groupingBy(attachmentDto -> attachmentDto.getMessage().getId()));
        for (MessageDto message : messages) {
            message.setAttachments(messageIdListMap.get(message.getId()));
        }
        return messages;
    }

    public List<UserDto> getMembers(ChatId chatId) {
        return memberQuery().where(chat.id.eq(chatId)).fetch();
    }

    private JPAQuery<ChatDto> baseQuery() {
        QUser lastMessage$author = new QUser("lastMessage$author");
        return queryFactory.from(chat)
                .leftJoin(lastMessage).on(lastMessage.chat.id.eq(chat.id)
                        .and(lastMessage.id.eq(
                                JPAExpressions.select(message.id.max())
                                        .from(message)
                                        .where(message.chat.id.eq(chat.id))
                        ))
                )
                .leftJoin(lastMessage$author).on(lastMessage$author.id.eq(lastMessage.author.id))
                .selectDto(
                        ChatDto.class,
                        UserReader.getShortName(lastMessage$author).as("lastMessage$author$shortName"),
                        UserReader.getFullName(lastMessage$author).as("lastMessage$author$fullName")
                );
    }

    private JPAQuery<MessageDto> messageQuery() {
        return queryFactory.from(message)
                .selectDto(
                        MessageDto.class,
                        UserReader.getFullName(message.author).as("author$fullName"),
                        UserReader.getShortName(message.author).as("author$shortName")
                );
    }

    private JPAQuery<AttachmentDto> attachmentQuery() {
        return queryFactory.from(attachment).selectDto(AttachmentDto.class);
    }

    private JPAQuery<UserDto> memberQuery() {
        return queryFactory.from(user)
                .innerJoin(chat).on(chat.members.contains(user))
                .selectDto(
                        UserDto.class,
                        UserReader.getShortName(user).as("shortName"),
                        UserReader.getFullName(user).as("fullName")
                );
    }
}
