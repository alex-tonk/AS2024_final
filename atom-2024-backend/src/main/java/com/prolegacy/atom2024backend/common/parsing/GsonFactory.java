package com.prolegacy.atom2024backend.common.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class GsonFactory {
    private static final Gson defaultGson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Optional.class, new TypeAdapter<JsonNodeType>() {
                @Override
                public void write(JsonWriter out, JsonNodeType value) throws IOException {
                    out.value(Optional.ofNullable(value).map(Enum::name).orElse(null));
                }

                @Override
                public JsonNodeType read(JsonReader in) {
                    return null;
                }
            })
            .registerTypeAdapter(Metadata.class, new TypeAdapter<Metadata>() {
                @Override
                public void write(JsonWriter out, Metadata value) throws IOException {
                    out.beginObject();
                    for (Map.Entry<String, MetadataField> entry : value.getFields().entrySet()) {
                        out.name(entry.getKey());
                        out.value(entry.getValue().getNodeType().name());
                    }
                    out.endObject();
                }

                @Override
                public Metadata read(JsonReader in) {
                    return null;
                }
            })
//            .registerTypeAdapter()
            .registerTypeAdapter(JsonNode.class, new TypeAdapter<JsonNode>() {
                @Override
                public void write(JsonWriter out, JsonNode value) throws IOException {
                    switch (value.getNodeType()) {
                        case NULL -> out.nullValue();
                        case NUMBER -> out.value(value.asDouble());
                        case BOOLEAN -> out.value(value.asBoolean());
                        case STRING -> out.value(value.asText());
                        case ARRAY -> {
                            out.beginArray();
                            value.elements().forEachRemaining(jsonNode -> {
                                try {
                                    write(out, jsonNode);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            out.endArray();
                        }
                        case OBJECT, POJO -> {
                            out.beginObject();
                            value.properties().forEach(entry -> {
                                        try {
                                            out.name(entry.getKey());
                                            write(out, entry.getValue());
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            );
                            out.endObject();
                        }
                    }
                }

                @Override
                public JsonNode read(JsonReader in) {
                    return null;
                }
            })
            .create();

    public static Gson defaultGson() {
        return defaultGson;
    }
}
