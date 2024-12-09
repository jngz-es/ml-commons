/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.common.input.parameter.clustering;

import static org.opensearch.core.xcontent.XContentParserUtils.ensureExpectedToken;
import static org.opensearch.ml.common.utils.StringUtils.getParameterMap;

import java.io.IOException;
import java.util.Map;

import org.opensearch.core.ParseField;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.ml.common.FunctionName;
import org.opensearch.ml.common.annotation.MLAlgoParameter;
import org.opensearch.ml.common.input.parameter.MLAlgoParams;

import lombok.Builder;
import lombok.Data;

@Data
@MLAlgoParameter(algorithms = { FunctionName.KMEANS })
public class KMeansParams implements MLAlgoParams {

    public static final String PARSE_FIELD_NAME = FunctionName.KMEANS.name();
    public static final NamedXContentRegistry.Entry XCONTENT_REGISTRY = new NamedXContentRegistry.Entry(
        MLAlgoParams.class,
        new ParseField(PARSE_FIELD_NAME),
        it -> parse(it)
    );

    public static final String CENTROIDS_FIELD = "centroids";
    public static final String ITERATIONS_FIELD = "iterations";
    public static final String DISTANCE_TYPE_FIELD = "distance_type";
    public static final String EXTENSION = "extension";

    // The number of centroids to use.
    private Integer centroids;
    // The maximum number of iterations
    private Integer iterations;
    // The distance function.
    private DistanceType distanceType;
    // TODO: expose number of thread and seed?

    private Map<String, String> extension;

    @Builder(toBuilder = true)
    public KMeansParams(Integer centroids, Integer iterations, DistanceType distanceType, Map<String, String> extension) {
        this.centroids = centroids;
        this.iterations = iterations;
        this.distanceType = distanceType;
        this.extension = extension;
    }

    public KMeansParams(StreamInput in) throws IOException {
        this.centroids = in.readOptionalInt();
        this.iterations = in.readOptionalInt();
        if (in.readBoolean()) {
            this.distanceType = in.readEnum(DistanceType.class);
        }
        if (in.readBoolean()) {
            extension = in.readMap(StreamInput::readString, StreamInput::readOptionalString);
        }
    }

    public static MLAlgoParams parse(XContentParser parser) throws IOException {
        Integer k = null;
        Integer iterations = null;
        DistanceType distanceType = null;
        Map<String, String> extension = null;

        ensureExpectedToken(XContentParser.Token.START_OBJECT, parser.currentToken(), parser);
        while (parser.nextToken() != XContentParser.Token.END_OBJECT) {
            String fieldName = parser.currentName();
            parser.nextToken();

            switch (fieldName) {
                case CENTROIDS_FIELD:
                    k = parser.intValue(false);
                    break;
                case ITERATIONS_FIELD:
                    iterations = parser.intValue(false);
                    break;
                case DISTANCE_TYPE_FIELD:
                    distanceType = DistanceType.from(parser.text());
                    break;
                case EXTENSION:
                    extension = getParameterMap(parser.map());
                    break;
                default:
                    parser.skipChildren();
                    break;
            }
        }
        return new KMeansParams(k, iterations, distanceType, extension);
    }

    @Override
    public String getWriteableName() {
        return PARSE_FIELD_NAME;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeOptionalInt(centroids);
        out.writeOptionalInt(iterations);
        if (distanceType != null) {
            out.writeBoolean(true);
            out.writeEnum(distanceType);
        } else {
            out.writeBoolean(false);
        }
        if (extension != null && !extension.isEmpty()) {
            out.writeBoolean(true);
            out.writeMap(extension, StreamOutput::writeString, StreamOutput::writeOptionalString);
        } else {
            out.writeBoolean(false);
        }
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, ToXContent.Params params) throws IOException {
        builder.startObject();
        if (centroids != null) {
            builder.field(CENTROIDS_FIELD, centroids);
        }
        if (iterations != null) {
            builder.field(ITERATIONS_FIELD, iterations);
        }
        if (distanceType != null) {
            builder.field(DISTANCE_TYPE_FIELD, distanceType.name());
        }
        if (extension != null && !extension.isEmpty()) {
            builder.field(EXTENSION, extension);
        }
        builder.endObject();
        return builder;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    public enum DistanceType {
        EUCLIDEAN,
        COSINE,
        L1;

        public static DistanceType from(String value) {
            try {
                return DistanceType.valueOf(value);
            } catch (Exception e) {
                throw new IllegalArgumentException("Wrong distance type");
            }
        }
    }
}
