/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.message;

public class AIMessage extends BaseMessage {
    public AIMessage(String content) {
        super(content, "ai");
    }

    public AIMessage(String content, String type) {
        super(content, type);
    }
}
