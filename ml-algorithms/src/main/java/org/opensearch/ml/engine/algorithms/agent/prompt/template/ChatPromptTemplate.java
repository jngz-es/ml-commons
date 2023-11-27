/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatPromptTemplate extends BaseChatPromptTemplate {
    private List<?> messages;

    public ChatPromptTemplate(List<String> inputVariables, List<?> messages) {
        super(inputVariables);
        this.messages = messages;
    }

    @Override
    public List<BaseMessage> formatMessages(Map<String, Object> kwargs) {
        return messages.stream().flatMap(m -> {
            if (m instanceof BaseMessage) {
                return List.of((BaseMessage) m).stream();
            } else if (m instanceof BaseMessagePromptTemplate) {
                return ((BaseMessagePromptTemplate) m).formatMessages(kwargs).stream();
            } else if (m instanceof BaseChatPromptTemplate) {
                return ((BaseChatPromptTemplate) m).formatMessages(kwargs).stream();
            } else {
                throw new IllegalArgumentException("Unsupported message type to format.");
            }
        }).collect(Collectors.toList());
    }
}
