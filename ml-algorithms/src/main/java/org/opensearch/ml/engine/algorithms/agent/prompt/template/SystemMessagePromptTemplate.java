/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import java.util.Map;

import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.SystemMessage;

public class SystemMessagePromptTemplate extends BaseStringMessagePromptTemplate {
    public SystemMessagePromptTemplate(StringPromptTemplate template) {
        this.promptTemplate = template;
    }

    @Override
    public BaseMessage format(Map<String, Object> kwargs) {
        return new SystemMessage(this.promptTemplate.format(kwargs));
    }

    public static SystemMessagePromptTemplate fromTemplate(String template) {
        return new SystemMessagePromptTemplate(PromptTemplate.fromTemplate(template));
    }
}
