/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.engine.indices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.search.SearchScrollRequestBuilder;
import org.opensearch.client.Client;
import org.opensearch.core.action.ActionListener;
import org.opensearch.index.query.ExistsQueryBuilder;
import org.opensearch.ml.common.dataframe.DataFrame;
import org.opensearch.ml.common.dataframe.DataFrameBuilder;
import org.opensearch.ml.common.dataframe.DefaultDataFrame;
import org.opensearch.ml.common.dataset.DataFrameInputDataset;
import org.opensearch.ml.common.dataset.MLInputDataType;
import org.opensearch.ml.common.dataset.MLInputDataset;
import org.opensearch.ml.common.dataset.ScrollSearchQueryInputDataset;
import org.opensearch.ml.common.dataset.SearchQueryInputDataset;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

/**
 * Convert MLInputDataset to Dataframe
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Log4j2
public class MLInputDatasetHandler {
    public static Logger logger = LogManager.getLogger(MLInputDatasetHandler.class);
    Client client;

    /**
     * Create DataFrame based on given search query
     * @param mlInputDataset MLInputDataset
     * @param listener ActionListener
     */
    public void parseSearchQueryInput(MLInputDataset mlInputDataset, ActionListener<MLInputDataset> listener) {
        if (!mlInputDataset.getInputDataType().equals(MLInputDataType.SEARCH_QUERY)) {
            throw new IllegalArgumentException("Input dataset is not SEARCH_QUERY type.");
        }
        if (mlInputDataset instanceof ScrollSearchQueryInputDataset) {
            parseScrollSearchQueryInput((ScrollSearchQueryInputDataset) mlInputDataset, listener);
        } else {
            parseDefaultSearchQueryInput((SearchQueryInputDataset) mlInputDataset, listener);
        }
    }

    private void parseDefaultSearchQueryInput(SearchQueryInputDataset inputDataset, ActionListener<MLInputDataset> listener) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(inputDataset.getSearchSourceBuilder());
        List<String> indicesList = inputDataset.getIndices();
        String[] indices = new String[indicesList.size()];
        indices = indicesList.toArray(indices);
        searchRequest.indices(indices);

        client.search(searchRequest, ActionListener.wrap(r -> {
            if (r == null || r.getHits() == null || r.getHits().getTotalHits() == null || r.getHits().getTotalHits().value == 0) {
                listener.onFailure(new IllegalArgumentException("No document found"));
                return;
            }
            SearchHits hits = r.getHits();
            List<Map<String, Object>> input = new ArrayList<>();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                input.add(hit.getSourceAsMap());
            }
            DataFrame dataFrame = DataFrameBuilder.load(input);
            MLInputDataset dfInputDataset = new DataFrameInputDataset(dataFrame);
            listener.onResponse(dfInputDataset);
            return;
        }, e -> {
            log.error("Failed to search" + e);
            listener.onFailure(e);
        }));
    }

    private void parseScrollSearchQueryInput(ScrollSearchQueryInputDataset inputDataset, ActionListener<MLInputDataset> listener) {

        SearchScrollRequestBuilder searchScrollRequestBuilder = createSearchScrollRequestBuilder(inputDataset.getScrollTime());

        ActionListener<SearchResponse> paginationListener = new PaginationListener(
                client,
                listener,
                searchScrollRequestBuilder
        );

        String indexName = inputDataset.getIndices().get(0);
        String scrollTime = inputDataset.getScrollTime();
        SearchSourceBuilder searchSourceBuilder = inputDataset.getSearchSourceBuilder();
        createSearchRequestBuilder(indexName, scrollTime, searchSourceBuilder).execute(paginationListener);
    }

    private SearchRequestBuilder createSearchRequestBuilder(String indexName, String scrollTime, SearchSourceBuilder searchSourceBuilder) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
        searchRequestBuilder.setScroll(scrollTime);
        searchRequestBuilder.request().source(searchSourceBuilder);
        searchRequestBuilder.addSort("_doc", SortOrder.ASC);

        return searchRequestBuilder;
    }

    private SearchScrollRequestBuilder createSearchScrollRequestBuilder(String scrollTime) {
        SearchScrollRequestBuilder searchScrollRequestBuilder = client.prepareSearchScroll(null);
        searchScrollRequestBuilder.setScroll(scrollTime);
        return searchScrollRequestBuilder;
    }

    private static class PaginationListener<T> implements ActionListener<SearchResponse> {

        final Client client;
        final ActionListener<MLInputDataset> listener;
        SearchScrollRequestBuilder searchScrollRequestBuilder;
        DataFrame dataFrame = null;

        public PaginationListener(
                Client client,
                ActionListener<MLInputDataset> listener,
                SearchScrollRequestBuilder searchScrollRequestBuilder
        ) {
            this.client = client;
            this.listener = listener;
            this.searchScrollRequestBuilder = searchScrollRequestBuilder;
        }

        @Override
        public void onResponse(SearchResponse searchResponse) {
            List<Map<String, Object>> input = new ArrayList<>();
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit hit : searchHits) {
                input.add(hit.getSourceAsMap());
            }

            if (searchHits == null || searchHits.length == 0) {
                // Scroll is finished, clear the scroll context, build the final dataset and return it.
                String scrollId = searchResponse.getScrollId();
                MLInputDataset dfInputDataset = new DataFrameInputDataset(dataFrame);

                if (scrollId != null) {
                    client.prepareClearScroll()
                            .addScrollId(scrollId)
                            .execute(ActionListener.wrap(clearScrollResponse -> listener.onResponse(dfInputDataset), listener::onFailure));
                } else {
                    listener.onResponse(dfInputDataset);
                }

            } else {
                DataFrame pageDataFrame = DataFrameBuilder.loadFlat(input);
                if (dataFrame == null) {
                    dataFrame = pageDataFrame;
                } else {
                    for (int i=0; i<pageDataFrame.size(); ++i) {
                        dataFrame.appendRow(pageDataFrame.getRow(i));
                    }
                }
                // Create a new search that starts where the last search left off
                searchScrollRequestBuilder.setScrollId(searchResponse.getScrollId());
                searchScrollRequestBuilder.execute(this);
            }
        }

        @Override
        public void onFailure(Exception e) {
            // Clear scroll context
            String scrollId = searchScrollRequestBuilder.request().scrollId();

            if (scrollId != null) {
                client.prepareClearScroll()
                        .addScrollId(scrollId)
                        .execute(ActionListener.wrap(clearScrollResponse -> listener.onFailure(e), listener::onFailure));
            } else {
                listener.onFailure(e);
            }
        }
    }
}
