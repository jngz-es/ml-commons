/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.message;

public class HumanMessage extends BaseMessage {
    public HumanMessage(String content) {
        super(content, "human");
    }

    public HumanMessage(String content, String type) {
        super(content, type);
    }
}
