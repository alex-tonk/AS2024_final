package com.prolegacy.atom2024integration.common.hibernate;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StringToLongIdConverter implements GenericConverter {

    private final Map<Class<? extends LongId>, LongIdFactory<? extends LongId>> longIdTypeFactories = new HashMap<>();

    public StringToLongIdConverter() {
        Reflections reflections = new Reflections("com.prolegacy");
        reflections.getSubTypesOf(LongId.class)
                .forEach(clazz -> longIdTypeFactories.put(clazz, new LongIdFactory<>(clazz)));
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return longIdTypeFactories.keySet()
                .stream()
                .map(clazz -> new ConvertiblePair(String.class, clazz))
                .collect(Collectors.toSet());
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        return Optional.ofNullable(longIdTypeFactories.get(targetType.getType()))
                .map(factory -> factory.valueOf(String.valueOf(source)))
                .orElseThrow(() -> new IllegalStateException("Unknown class %s for conversion to LongId".formatted(targetType.getType().getName())));
    }
}
