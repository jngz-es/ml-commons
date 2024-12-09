/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.ml.common.dataframe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.opensearch.core.common.io.stream.StreamInput;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataFrameBuilder {

    /**
     * Build empty data frame without any real data.
     * @param columnMetas column metas
     * @return empty data frame
     */
    public DataFrame emptyDataFrame(final ColumnMeta[] columnMetas) {
        if (columnMetas == null || columnMetas.length == 0) {
            throw new IllegalArgumentException("columnMetas array is null or empty");
        }
        return new DefaultDataFrame(columnMetas);
    }

    /**
     * Load data frame based on list of map objects. It will use the first object to build the ColumnMeta, and all of the
     * map objects should have same key set, other wise it will throw exception.
     * @param input input list of map objects
     * @return data frame
     */
    public DataFrame load(final List<Map<String, Object>> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input is null or empty");
        }

        Map<String, Object> element = input.get(0);
        ColumnMeta[] columnMetas = new ColumnMeta[element.size()];

        int index = 0;
        for (Map.Entry<String, Object> entry : element.entrySet()) {
            ColumnMeta columnMeta = ColumnMeta.builder().name(entry.getKey()).columnType(ColumnType.from(entry.getValue())).build();
            columnMetas[index++] = columnMeta;
        }

        return load(columnMetas, input);
    }

    public DataFrame loadFlat1(final List<Map<String, Object>> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input is null or empty");
        }

//        List<Map<String, Float>> flatInput = input.stream()
//                .flatMap(map -> map.entrySet().stream()
//                        .flatMap(entry -> {
//                            AtomicInteger counter = new AtomicInteger(0);
//                            return ((List<Float>)entry.getValue()).stream()
//                                    .map(value -> new AbstractMap.SimpleEntry<>(
//                                            entry.getKey() + "_" + counter.getAndIncrement(),
//                                            value
//                                    ));
//                        })
//                )
//                .collect(Collectors.groupingBy(
//                        entry -> entry.getKey().substring(0, entry.getKey().lastIndexOf('_')),
//                        LinkedHashMap::new,
//                        Collectors.toMap(
//                                Map.Entry::getKey,
//                                Map.Entry::getValue,
//                                (v1, v2) -> v1,
//                                LinkedHashMap::new
//                        )
//                ))
//                .values()
//                .stream()
//                .collect(Collectors.toList());

        List<Map<String, Object>> flatInput = new ArrayList<>();
        input.forEach(stringObjectMap -> {
            Map<String, Object> innerMap = new HashMap<>();
            for (Map.Entry entry : stringObjectMap.entrySet()) {
                String key = (String) entry.getKey();
                if (entry.getValue() instanceof List<?>) {
                    List<Object> v = (List<Object>) entry.getValue();
                    for (int i=0; i<v.size(); i++) {
                        innerMap.put(key+i, v.get(i));
                    }
                } else {
                    innerMap.put(key, entry.getValue());
                }
            }
            flatInput.add(innerMap);
        });

        return load(flatInput);
    }

    public DataFrame loadFlat(final List<Map<String, Object>> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input is null or empty");
        }

        List<Map<String, Object>> flatInput = new ArrayList<>();
        input.forEach(stringObjectMap -> {
            Map<String, Object> innerMap = new HashMap<>();
            for (Map.Entry entry : stringObjectMap.entrySet()) {
                List<Object> flatList = new ArrayList<>();
                String key = (String) entry.getKey();
                AtomicInteger suffixNumber = new AtomicInteger(0);
                flatNestedListDFS(entry.getValue(), flatList);
                flatList.forEach(v -> {
                    innerMap.put(key + "_" + suffixNumber.getAndIncrement(), v);
                });
            }
            flatInput.add(innerMap);
        });

        return load(flatInput);
    }

    private void flatNestedListDFS(Object input, List<Object> output) {
        if (input == null) {
            return;
        }
        if (input instanceof List<?>) {
            ((List<?>) input).forEach(o -> {flatNestedListDFS(o, output);});
        } else {
            output.add(input);
        }
    }

    /**
     * Load data frame given columnMetas, and list of map objects. and all of the
     * map objects should have same key set, otherwise it will throw exception.
     * @param columnMetas array of ColumnMeta
     * @param input input list of map objects
     * @return data frame
     */
    public DataFrame load(final ColumnMeta[] columnMetas, final List<Map<String, Object>> input) {
        if (columnMetas == null || columnMetas.length == 0) {
            throw new IllegalArgumentException("columnMetas array is null or empty");
        }
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("input data list is null or empty");
        }

        int columnSize = columnMetas.length;

        Map<String, Integer> columnsMap = new HashMap<>();
        for (int i = 0; i < columnSize; i++) {
            columnsMap.put(columnMetas[i].getName(), i);
        }

        List<Row> rows = input.stream().map(item -> {
            Row row = new Row(columnSize);
            if (item.size() != columnSize) {
                throw new IllegalArgumentException("input item map size is different in the map");
            }

            for (Map.Entry<String, Object> entry : item.entrySet()) {
                if (!columnsMap.containsKey(entry.getKey())) {
                    throw new IllegalArgumentException("field of input item doesn't exist in columns, filed:" + entry.getKey());
                }
                String columnName = entry.getKey();
                int index = columnsMap.get(columnName);
                ColumnType columnType = columnMetas[index].getColumnType();
                ColumnValue value = ColumnValueBuilder.build(entry.getValue());
                if (columnType != value.columnType()) {
                    throw new IllegalArgumentException("the same field has different data type");
                }
                row.setValue(index, value);
            }
            return row;
        }).collect(Collectors.toList());

        return new DefaultDataFrame(columnMetas, rows);
    }

    /**
     * Load data frame from stream input.
     * @param input stream input
     * @return data frame
     * @throws IOException
     */
    public DataFrame load(StreamInput input) throws IOException {
        final DataFrameType dataFrameType = input.readEnum(DataFrameType.class);
        switch (dataFrameType) {
            case DEFAULT:
                return new DefaultDataFrame(input);
            default:
                throw new IllegalStateException("Unexpected value: " + dataFrameType);
        }
    }
}
