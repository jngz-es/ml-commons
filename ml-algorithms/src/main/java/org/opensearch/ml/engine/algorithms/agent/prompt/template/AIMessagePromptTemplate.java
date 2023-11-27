/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import org.opensearch.ml.engine.algorithms.agent.prompt.message.AIMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;

import java.util.Map;

public class AIMessagePromptTemplate extends BaseStringMessagePromptTemplate {
    public AIMessagePromptTemplate(StringPromptTemplate template) {
        this.promptTemplate = template;
    }

    @Override
    public BaseMessage format(Map<String, Object> kwargs) {
        return new AIMessage(this.promptTemplate.format(kwargs));
    }

    public static AIMessagePromptTemplate fromTemplate(String template) {
        return new AIMessagePromptTemplate(PromptTemplate.fromTemplate(template));
    }
}
