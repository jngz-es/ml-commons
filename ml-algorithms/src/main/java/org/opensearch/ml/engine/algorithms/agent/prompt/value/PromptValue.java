/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.value;

import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;

import java.io.Serializable;
import java.util.List;

public abstract class PromptValue implements Serializable {
    private static final long serialVersionUID = 1L;

    public String toString() {
        return super.toString();
    }

    public abstract List<BaseMessage> toMessages();
}
