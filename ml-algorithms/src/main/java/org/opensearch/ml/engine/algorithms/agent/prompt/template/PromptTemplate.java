/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import lombok.Getter;
import lombok.Setter;
import org.opensearch.ml.engine.algorithms.agent.prompt.PromptHelper;

import java.util.List;
import java.util.Map;

public class PromptTemplate extends StringPromptTemplate {
    @Getter
    @Setter
    private String template;

    public PromptTemplate(List<String> inputVariables, String template) {
        super(inputVariables);
        this.template = template;
    }

    @Override
    public String format(Map<String, Object> kwargs) {
        return PromptHelper.renderTemplate(template, kwargs);
    }

    public static PromptTemplate fromTemplate(String template) {
        List<String> inputVariables = PromptHelper.getVariables(template);
        return new PromptTemplate(inputVariables, template);
    }
}
