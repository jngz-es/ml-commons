/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.value;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.opensearch.ml.engine.algorithms.agent.prompt.PromptHelper;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;

import java.util.List;

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
