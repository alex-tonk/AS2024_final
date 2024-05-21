package com.prolegacy.atom2024backend.common.js;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;

import java.util.concurrent.*;
import java.util.function.BiFunction;

public class JsEvaluator {

    private static final String languageName = "js";
    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Context context;
    private final Long timeoutSeconds;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Boolean executed = false;

    JsEvaluator(Long timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        this.context = Context.newBuilder()
                .allowPolyglotAccess(PolyglotAccess.NONE)
                .allowHostAccess(HostAccess.NONE)
                .allowHostClassLookup(c -> false)
                .allowHostClassLoading(false)
                .allowCreateThread(false)
                .allowCreateProcess(false)
                .allowNativeAccess(false)
                .allowIO(IOAccess.NONE)
                .build();
    }

    public CompletableFuture<JsonNode> evalJsonAsync(String jsCode) throws ExecutionException {
        if (executed) {
            throw new ExecutionException("JsEvaluator has already been used once", null);
        }
        this.executed = true;

        return CompletableFuture.supplyAsync(() -> this.evalInternal(jsCode), executor)
                .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .handle((value, throwable) -> {
                    if (throwable != null) {
                        throw new RuntimeException(throwable);
                    }
                    if (value == null) {
                        return null;
                    }
                    try {
                        Thread.sleep(500);
                        return objectMapper.readTree(JsonParser.parseString(value.toString()).toString());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    public JsonNode evalJson(String jsCode) throws TimeoutException, ExecutionException, InterruptedException, JsonProcessingException {
        if (executed) {
            throw new ExecutionException("JsEvaluator has already been used once", null);
        }
        this.executed = true;

        Future<Value> result = executor.submit(() -> this.evalInternal(jsCode));

        return objectMapper.readTree(JsonParser.parseString(result.get(timeoutSeconds, TimeUnit.SECONDS).toString()).toString());
    }

    public Value eval(String jsCode) throws TimeoutException, ExecutionException, InterruptedException {
        if (executed) {
            throw new ExecutionException("JsEvaluator has already been used once", null);
        }
        this.executed = true;

        Future<Value> result = executor.submit(() -> this.evalInternal(jsCode));

        return result.get(timeoutSeconds, TimeUnit.SECONDS);
    }

    public <T> T eval(String jsCode, Class<T> returnClass) throws TimeoutException, ExecutionException, InterruptedException {
        if (executed) {
            throw new ExecutionException("JsEvaluator has already been used once", null);
        }
        this.executed = true;

        Future<T> result = executor.submit(() -> this.evalInternal(jsCode, returnClass));

        return result.get(timeoutSeconds, TimeUnit.SECONDS);
    }

    public <T> T eval(String jsCode, TypeLiteral<T> returnClass) throws TimeoutException, ExecutionException, InterruptedException {
        if (executed) {
            throw new ExecutionException("JsEvaluator has already been used once", null);
        }
        this.executed = true;

        Future<T> result = executor.submit(() -> this.evalInternal(jsCode, returnClass));

        return result.get(timeoutSeconds, TimeUnit.SECONDS);
    }

    private Value evalInternal(String jsCode) {
        return context.eval(languageName, jsCode);
    }

    private <T> T evalInternal(String jsCode, Class<T> returnClass) {
        return context.eval(languageName, jsCode).as(returnClass);
    }

    private <T> T evalInternal(String jsCode, TypeLiteral<T> returnClass) {
        return context.eval(languageName, jsCode).as(returnClass);
    }

    public <T> void putMember(T javaObj, String jsName) {
        context.getBindings(languageName).putMember(jsName, javaObj);
    }

}
