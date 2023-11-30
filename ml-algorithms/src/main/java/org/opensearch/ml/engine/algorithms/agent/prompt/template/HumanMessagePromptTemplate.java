/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import java.util.Map;

import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.HumanMessage;

public class HumanMessagePromptTemplate extends BaseStringMessagePromptTemplate {
    public HumanMessagePromptTemplate(StringPromptTemplate template) {
        this.promptTemplate = template;
    }

    @Override
    public BaseMessage format(Map<String, Object> kwargs) {
        return new HumanMessage(this.promptTemplate.format(kwargs));
    }

    public static HumanMessagePromptTemplate fromTemplate(String template) {
        return new HumanMessagePromptTemplate(PromptTemplate.fromTemplate(template));
    }
}
