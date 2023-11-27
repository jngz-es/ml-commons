/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.opensearch.ml.engine.algorithms.agent.prompt.value.PromptValue;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public abstract class BasePromptTemplate {
    protected List<String> inputVariables;

    public abstract PromptValue formatPrompt(Map<String, Object> kwargs);

    public abstract String format(Map<String, Object> kwargs);
}
