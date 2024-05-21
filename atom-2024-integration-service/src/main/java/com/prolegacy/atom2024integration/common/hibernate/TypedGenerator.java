package com.prolegacy.atom2024integration.common.hibernate;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

import java.util.Properties;

public class TypedGenerator extends SequenceStyleGenerator {

    private JavaType<?> javaType = null;

    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) throws MappingException {
        if (type instanceof BasicType<?> namedBasicType) {
            this.javaType = namedBasicType.getJdbcJavaType();
        } else {
            throw new IllegalStateException("@GeneratedValue(generator = \"typed-sequence\") is used for unknown BasicType");
        }

        parameters.put(
                "sequence_name",
                ConfigurationHelper.getString("target_table", parameters)
                        + "_"
                        + ConfigurationHelper.getString("target_column", parameters)
                        + "_seq"
        );


        super.configure(
                LongId.class.isAssignableFrom(javaType.getJavaTypeClass())
                        ? new NamedBasicTypeImpl<>(LongJavaType.INSTANCE, ((BasicType<?>) type).getJdbcType(), "long")
                        : type,
                parameters,
                serviceRegistry);
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        var value = super.generate(session, owner, currentValue, eventType);
        return javaType.wrap(value, null);
    }
}
