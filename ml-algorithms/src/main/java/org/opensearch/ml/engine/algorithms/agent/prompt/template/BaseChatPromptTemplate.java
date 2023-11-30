/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import java.util.List;
import java.util.Map;

import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.value.ChatPromptValue;
import org.opensearch.ml.engine.algorithms.agent.prompt.value.PromptValue;

public abstract class BaseChatPromptTemplate extends BasePromptTemplate {
    public BaseChatPromptTemplate(List<String> inputVariables) {
        super(inputVariables);
    }

    @Override
    public PromptValue formatPrompt(Map<String, Object> kwargs) {
        return new ChatPromptValue(formatMessages(kwargs));
    }

    @Override
    public String format(Map<String, Object> kwargs) {
        return formatPrompt(kwargs).toString();
    }

    public abstract List<BaseMessage> formatMessages(Map<String, Object> kwargs);
}
