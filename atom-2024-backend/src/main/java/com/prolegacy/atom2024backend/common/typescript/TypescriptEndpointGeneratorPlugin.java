package com.prolegacy.atom2024backend.common.typescript;

import com.blueveery.springrest2ts.converters.DefaultNullableTypesStrategy;
import com.blueveery.springrest2ts.converters.ModelClassesToTsClassesConverter;
import com.blueveery.springrest2ts.converters.TypeMapper;
import com.blueveery.springrest2ts.filters.HasAnnotationJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSSimpleType;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.auth.dto.RoleDto;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.hibernate.LongId;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TypescriptEndpointGeneratorPlugin {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Provide directory path for typescript generation");
        }

        String typescriptDirectory = args[0];

        Rest2tsGenerator tsGenerator = setupTsGenerator();

        Map<Class<?>, TSType> customTypeMapping = tsGenerator.getCustomTypeMapping();
        customTypeMapping.put(Instant.class, TypeMapper.tsDate);
        customTypeMapping.put(LocalDate.class, TypeMapper.tsDate);

        Reflections reflections = new Reflections("com.prolegacy");
        reflections.getSubTypesOf(LongId.class).forEach(idClass ->
                customTypeMapping.put(idClass, TypeMapper.tsNumber)
        );
        customTypeMapping.put(JsonNode.class, TypeMapper.tsObject);
        customTypeMapping.put(MultipartFile.class, new TSSimpleType("FormData"));

        tsGenerator.registerExternalClassMapping(PageQuery.class, "primeng/table", "TableLazyLoadEvent", new TSFormalTypeParameter("T"));
        tsGenerator.registerExternalClassMapping(UserDto.class, "../models/UserDto", "UserDto");
        tsGenerator.registerExternalClassMapping(RoleDto.class, "../models/RoleDto", "RoleDto");

        // set of java root packages for class scanning
        Set<String> javaPackageSet = Collections.singleton("com.prolegacy");
        tsGenerator.generate(javaPackageSet, Paths.get(typescriptDirectory));
    }

    private static @NotNull Rest2tsGenerator setupTsGenerator() {
        Rest2tsGenerator tsGenerator = new Rest2tsGenerator();

        // Rest classes filtering
        tsGenerator.setRestClassesCondition(new HasAnnotationJavaTypeFilter(TypescriptEndpoint.class));

        // Java model classes converter setup
        tsGenerator.setModelClassesConverter(new ModelClassesToTsClassesConverter(new EmptyImplementationGenerator(), new NullableFieldsMapper()));
        tsGenerator.setEnumConverter(new StringBasedEnumConverter());

        DefaultNullableTypesStrategy nullableTypesStrategy = new DefaultNullableTypesStrategy();
        nullableTypesStrategy.setUsePrimitiveTypesWrappers(false);
        tsGenerator.setNullableTypesStrategy(nullableTypesStrategy);

        // Spring REST controllers converter
        tsGenerator.setRestClassesConverter(new MySpringRestToTsConverter());
        return tsGenerator;
    }

}
