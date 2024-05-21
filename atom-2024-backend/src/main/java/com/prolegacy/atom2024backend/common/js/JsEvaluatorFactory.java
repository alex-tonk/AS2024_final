package com.prolegacy.atom2024backend.common.js;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JsEvaluatorFactory {

    @Value("${js-evaluator.timeout-seconds:10}")
    private Long timeoutSeconds;

    public JsEvaluator create() {
        return new JsEvaluator(timeoutSeconds);
    }
}
