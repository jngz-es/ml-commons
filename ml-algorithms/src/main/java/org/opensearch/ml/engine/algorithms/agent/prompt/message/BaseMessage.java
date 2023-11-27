/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public abstract class BaseMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private String type;
}
