/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class BaseMessagePromptTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract List<BaseMessage> formatMessages(Map<String, Object> kwargs);

    public abstract List<String> inputVariables();
}
