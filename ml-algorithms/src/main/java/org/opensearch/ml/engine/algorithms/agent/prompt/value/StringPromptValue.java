/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.value;

import lombok.AllArgsConstructor;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.HumanMessage;
import org.opensearch.ml.repackage.com.google.common.collect.ImmutableList;

import java.util.List;

@AllArgsConstructor
public class StringPromptValue extends PromptValue {
    private String text;

    @Override
    public List<BaseMessage> toMessages() {
        return ImmutableList.of(new HumanMessage(text));
    }
}
