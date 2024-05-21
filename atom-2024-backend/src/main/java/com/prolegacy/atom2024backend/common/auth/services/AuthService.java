package com.prolegacy.atom2024backend.common.auth.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.providers.UserProvider;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.auth.util.JwtTokenUtil;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.mail.EmailDetails;
import com.prolegacy.atom2024backend.common.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {
    @Autowired
    private UserReader userReader;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private EmailService emailService;

    @Value("${auth.password-restoration-code-expiration-minutes:15}")
    private Long passRestorationExpirationMinutes = 15L;

    @Value("classpath:reset-password-email-template.html")
    private Resource passRestorationEmailTemplate;

    private final Cache<String, Long> passwordRestorationCodes = Caffeine.newBuilder()
            .expireAfterWrite(passRestorationExpirationMinutes, TimeUnit.MINUTES)
            .build();

    public UserDto authenticate(String email, String password) throws BadCredentialsException {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email, password
                        )
                );
        return userReader.getUserByEmail(email, true);
    }

    public void logout(String authorization) {
        Pattern pattern = Pattern.compile("Bearer (.*)$");
        Matcher matcher = pattern.matcher(authorization);

        if (!matcher.find()) {
            throw new BusinessLogicException("Нет токена");
        }

        String token = matcher.group(1);
        jwtTokenUtil.getBlacklistedTokens().put(token, Boolean.TRUE);
    }

    public void sendRestorePasswordCode(String email) {
        String lowerCaseEmail = Optional.ofNullable(email).map(e -> e.toLowerCase(Locale.US))
                .orElseThrow(() -> new BusinessLogicException("Не задан email-адрес"));
        UserDto user = Optional.ofNullable(userReader.getUserByEmail(lowerCaseEmail))
                .orElseThrow(() -> new BusinessLogicException("Пользователь с email-адресом %s не найден".formatted(lowerCaseEmail)));
        if (user.getArchived()) {
            throw new BusinessLogicException("Нельзя восстановить пароль к заархивированному аккаунту");
        }

        long code = Math.floorMod(ThreadLocalRandom.current().nextLong(), 10000L);
        passwordRestorationCodes.put(lowerCaseEmail, code);
        try {
            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(lowerCaseEmail)
                    .subject("Восстановление пароля")
                    .msgBody(passRestorationEmailTemplate.getContentAsString(StandardCharsets.UTF_8).formatted(code))
                    .build();
            emailService.sendSimpleMail(emailDetails);
        } catch (Exception e) {
            throw new BusinessLogicException(
                    "Не удалось отправить письмо для восстановления пароля на email-адрес %s"
                            .formatted(lowerCaseEmail)
            );
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void restorePassword(String email, String newPassword, Long code) {
        String lowerCaseEmail = Optional.ofNullable(email).map(e -> e.toLowerCase(Locale.US))
                .orElseThrow(() -> new BusinessLogicException("Не задан email-адрес"));

        if (!code.equals(passwordRestorationCodes.getIfPresent(lowerCaseEmail))) {
            throw new BusinessLogicException("Неверный или истёкший проверочный код, попробуйте ещё раз");
        }

        userService.restorePassword(lowerCaseEmail, newPassword);
    }

    public UserDto getCurrentUserDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User user) {
            return userReader.getUserByEmail(user.getUsername(), true);
        } else {
            return null;
        }
    }
}
