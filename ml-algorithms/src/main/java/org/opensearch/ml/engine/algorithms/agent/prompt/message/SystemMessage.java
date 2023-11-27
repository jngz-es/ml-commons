/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.message;

public class SystemMessage extends BaseMessage {
    public SystemMessage(String content) {
        this(content, "system");
    }

    public SystemMessage(String content, String type) {
        super(content, type);
    }
}
