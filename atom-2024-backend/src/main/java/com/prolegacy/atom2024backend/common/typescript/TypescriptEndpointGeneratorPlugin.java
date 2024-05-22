package com.prolegacy.atom2024backend.common.typescript;

import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.filters.HasAnnotationJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.*;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.annotation.TypescriptIgnore;
import com.prolegacy.atom2024backend.common.auth.dto.RoleDto;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.hibernate.LongId;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import org.reflections.Reflections;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class TypescriptEndpointGeneratorPlugin {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Provide directory path for typescript generation");
        }

        String typescriptDirectory = args[0];

        Rest2tsGenerator tsGenerator = new Rest2tsGenerator();

        // Rest classes filtering
        tsGenerator.setRestClassesCondition(new HasAnnotationJavaTypeFilter(TypescriptEndpoint.class));

        // Java model classes converter setup
        JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
        jacksonObjectMapper.setFieldsVisibility(JsonAutoDetect.Visibility.ANY);
        tsGenerator.setModelClassesConverter(new ModelClassesToTsInterfacesConverter(jacksonObjectMapper));
        tsGenerator.setEnumConverter(new JavaEnumToTsEnumConverter() {
            @Override
            public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
                TSEnum tsEnum = (TSEnum) TypeMapper.map(javaClass);
                for (Object enumConstant : javaClass.getEnumConstants()) {
                    String name = ((Enum<?>) enumConstant).name();
                    tsEnum.add("%s = '%s'".formatted(name, name));
                }
                tsEnum.addAllAnnotations(javaClass.getAnnotations());
                conversionListener.tsScopedTypeCreated(javaClass, tsEnum);
            }
        });
        DefaultNullableTypesStrategy nullableTypesStrategy = new DefaultNullableTypesStrategy();
        nullableTypesStrategy.setUsePrimitiveTypesWrappers(false);
        tsGenerator.setNullableTypesStrategy(nullableTypesStrategy);

        // Spring REST controllers converter
        tsGenerator.setRestClassesConverter(new SpringRestToTsConverter(
                new Angular4ImplementationGenerator() {
                    @Override
                    public List<TSDecorator> getDecorators(TSClass tsClass) {
                        List<ILiteral> literalList = this.injectableDecorator.getTsLiteralList();
                        if (literalList.isEmpty()) {
                            TSJsonLiteral jsonLiteral = new TSJsonLiteral();
                            jsonLiteral.getFieldMap().put("providedIn", new TSLiteral("root", TypeMapper.tsString, "root"));
                            literalList.add(jsonLiteral);
                        }
                        return Collections.singletonList(this.injectableDecorator);
                    }

                    @Override
                    protected String composeRequestOptions(String requestHeadersVar, String requestParamsVar, boolean isRequestParamDefined, boolean isRequestHeaderDefined, String requestOptions, boolean isJsonParsingRequired) {
                        if (isRequestHeaderDefined || isRequestParamDefined || isJsonParsingRequired) {
                            List<String> requestOptionsList = new ArrayList<>();
                            if (isRequestHeaderDefined) {
                                requestOptionsList.add(requestHeadersVar);
                            }
                            if (isRequestParamDefined) {
                                requestOptionsList.add(requestParamsVar);
                            }
                            if (isJsonParsingRequired) {
                                requestOptionsList.add("responseType: 'json'");
                            }
                            requestOptions += ", {";
                            requestOptions += String.join(", ", requestOptionsList);
                            requestOptions += "}";
                        }
                        return requestOptions;
                    }

                    @Override
                    protected String getGenericType(TSMethod method, boolean isRequestOptionRequired) {
                        return super.getGenericType(method, false);
                    }

                    @Override
                    protected String getParseResponseFunction(boolean isJsonResponse, TSMethod method) {
                        return super.getParseResponseFunction(false, method);
                    }
                }
        ) {
            @Override
            protected boolean isRestMethod(Method method) {
                return !method.isAnnotationPresent(TypescriptIgnore.class) && super.isRestMethod(method);
            }

            @Override
            protected String createTsClassName(Class javaClass) {
                String tsClassName = super.createTsClassName(javaClass);
                Class<?> clazz = (Class<?>) javaClass;
                if (clazz.isAnnotationPresent(TypescriptEndpoint.class)) {
                    TypescriptEndpoint annotation = clazz.getAnnotation(TypescriptEndpoint.class);
                    if (annotation.value() != null && !annotation.value().isEmpty()) {
                        return annotation.value();
                    } else if (javaClass.getSimpleName().endsWith("Controller")) {
                        return javaClass.getSimpleName().replaceFirst("Controller$", "Service");
                    }
                }
                return tsClassName;
            }
        });

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
}
