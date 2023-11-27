/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import lombok.Getter;
import lombok.Setter;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;
import org.opensearch.ml.repackage.com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

public abstract class BaseStringMessagePromptTemplate extends BaseMessagePromptTemplate {
    @Getter
    @Setter
    protected StringPromptTemplate promptTemplate;

    @Override
    public List<BaseMessage> formatMessages(Map<String, Object> kwargs) {
        return ImmutableList.of(format(kwargs));
    }

    @Override
    public List<String> inputVariables() {
        return promptTemplate.getInputVariables();
    }

    public abstract BaseMessage format(Map<String, Object> kwargs);
}
