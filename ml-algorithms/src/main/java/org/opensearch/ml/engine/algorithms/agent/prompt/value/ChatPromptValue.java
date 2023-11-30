/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.value;

import java.util.List;

import org.opensearch.ml.engine.algorithms.agent.prompt.PromptHelper;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;

import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class ChatPromptValue extends PromptValue {
    @Setter
    private List<BaseMessage> messages;

    public String toString() {
        return PromptHelper.getBufferString(messages, null, null);
    }

    @Override
    public List<BaseMessage> toMessages() {
        return messages;
    }
}
