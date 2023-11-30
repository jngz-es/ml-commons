/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import java.util.List;
import java.util.Map;

import org.opensearch.ml.engine.algorithms.agent.prompt.value.PromptValue;
import org.opensearch.ml.engine.algorithms.agent.prompt.value.StringPromptValue;

public abstract class StringPromptTemplate extends BasePromptTemplate {
    public StringPromptTemplate(List<String> inputVariables) {
        super(inputVariables);
    }

    @Override
    public PromptValue formatPrompt(Map<String, Object> kwargs) {
        return new StringPromptValue(format(kwargs));
    }
}
