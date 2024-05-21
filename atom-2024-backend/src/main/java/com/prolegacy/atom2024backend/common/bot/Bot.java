package com.prolegacy.atom2024backend.common.bot;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Component
@ConditionalOnProperty(value = "bot.enabled", havingValue = "true")
public class Bot extends AbilityBot {
    @Value("${bot.creator-id}")
    private Long creatorId;

    public Bot(@Value("${bot.token}") String token, @Value("${bot.name}") String name) {
        super(token, name);
    }

    @Override
    public long creatorId() {
        return creatorId;
    }

    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .info("Регистрирует пользователя в боте")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Добро пожаловать! Введите /commands, чтобы увидеть список доступных комманд", ctx.chatId()))
                .build();
    }

    public Ability uppercase() {
        return Ability.builder()
                .name("uppercase")
                .info("КАПСЛОК: /uppercase <текст>")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    if (ctx.arguments().length == 0) {
                        silent.send("Комманде требуется аргумент: /uppercase <текст>", ctx.chatId());
                    }
                    silent.send(Arrays.stream(ctx.arguments()).map(s -> s.toUpperCase(Locale.ROOT)).collect(Collectors.joining(" ")), ctx.chatId());
                })
                .build();
    }

    @Override
    public void onUpdateReceived(Update update) {
        super.onUpdateReceived(update);

        Message message = update.getMessage();
        if (message == null || message.isCommand() || StringUtils.isBlank(message.getText())) return;

        try {
            execute(
                    SendMessage.builder()
                            .chatId(message.getChatId())
                            .text("А может ты...\n%s!!!".formatted(message.getText()))
                            .build()
            );
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
