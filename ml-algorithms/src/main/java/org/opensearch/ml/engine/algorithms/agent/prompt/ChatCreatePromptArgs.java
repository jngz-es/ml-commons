/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatCreatePromptArgs {
    private String systemMessageTemplate;
    private String humanMessageTemplate;
    private String formatInstructionsTemplate;
    private List<String> inputVariables;
}
