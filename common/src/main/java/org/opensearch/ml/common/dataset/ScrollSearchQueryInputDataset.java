/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.common.dataset;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Search query based input data. The client just need give the search query, and ML plugin will read the data based on it,
 * and build the data frame for algorithm execution.
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScrollSearchQueryInputDataset extends SearchQueryInputDataset {
    private String scrollTime;

    public ScrollSearchQueryInputDataset(@NonNull List<String> indices, @NonNull SearchSourceBuilder searchSourceBuilder, @NonNull String scrollTime) {
        super(indices, searchSourceBuilder);
        this.scrollTime = scrollTime;
    }

    public ScrollSearchQueryInputDataset(StreamInput streaminput) throws IOException {
        super(streaminput);
        this.scrollTime = streaminput.readString();
    }

    @Override
    public void writeTo(StreamOutput streamOutput) throws IOException {
        super.writeTo(streamOutput);
        streamOutput.writeString(this.scrollTime);
    }
}
