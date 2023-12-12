/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.algorithms.agent.prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.StringSubstitutor;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.AIMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.BaseMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.HumanMessage;
import org.opensearch.ml.engine.algorithms.agent.prompt.message.SystemMessage;
import org.opensearch.ml.repackage.com.google.common.collect.ImmutableMap;

public class PromptHelper {
    public static String getBufferString(List<BaseMessage> messages, String humanPrefix, String aiPrefix) {
        return messages.stream().map(message -> {
            String role;
            if (message instanceof HumanMessage) {
                role = humanPrefix != null ? humanPrefix : "Human";
            } else if (message instanceof AIMessage) {
                role = aiPrefix != null ? aiPrefix : "AI";
            } else if (message instanceof SystemMessage) {
                role = "System";
            } else {
                throw new IllegalArgumentException("unsupported message role");
            }
            return new StringBuilder().append(role).append(": ").append(message.getContent());
        }).collect(Collectors.joining("\n"));
    }

    public static String getFormatInstructions(String formatInstructionsTemplate, String toolNames) {
        return renderTemplate(formatInstructionsTemplate, ImmutableMap.of("tool_names", toolNames));
    }

    public static String getHumanMessage(String humanMessageTemplate, String tools, String formatInstructions) {
        return renderTemplate(humanMessageTemplate, ImmutableMap.of("tools", tools, "format_instructions", formatInstructions));
    }

    public static String renderTemplate(String template, Map<String, Object> kwargs) {
        return new StringSubstitutor(kwargs, "{", "}").replace(template);
    }

    // Only single pair of curly braces in template.
    public static List<String> getVariables(String template) {
        List<String> res = new ArrayList<>();
        String regex = "\\{(.*?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            res.add(matcher.group(1));
        }
        return res;
    }
}
