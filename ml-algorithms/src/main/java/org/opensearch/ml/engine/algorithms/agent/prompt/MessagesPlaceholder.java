/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt;

import lombok.AllArgsConstructor;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.template.BaseMessagePromptTemplate;
import org.opensearch.ml.repackage.com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MessagesPlaceholder extends BaseMessagePromptTemplate {
    private String variableName;

    @Override
    public List<BaseMessage> formatMessages(Map<String, Object> kwargs) {
        Object value = kwargs.get(variableName);
        if (value == null || !(value instanceof List)) {
            return null;
        }

        List<BaseMessage> res = ((List<?>) value).stream().filter(o -> o instanceof BaseMessage).map(o -> (BaseMessage) o).collect(Collectors.toList());
        return res;
    }

    @Override
    public List<String> inputVariables() {
        return ImmutableList.of(variableName);
    }
}
