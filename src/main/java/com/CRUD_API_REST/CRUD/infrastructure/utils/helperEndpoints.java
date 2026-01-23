package com.CRUD_API_REST.CRUD.infrastructure.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class helperEndpoints {
    public static <T> Map<String, Object> buildResponse(int state, String message, T successBody, T errorBody, T updateBody) {
        Map<String, Object> response = new HashMap<>();
        response.put("state", state);
        response.put("message", message);
        response.put("successBody", successBody);
        response.put("errorBody", errorBody);
        response.put("updateBody", updateBody);
        return response;
    }

    public static <T> Map<String, Object> buildResponse(int state, String message, T successBody, T errorBody) {
        return buildResponse(state, message, successBody, errorBody, null);
    }

    public static <T> Map<String, Object> buildResponse(int state, String message) {
        return buildResponse(state, message, null, null, null);
    }

    public static <T> Map<String, Object> buildResponse(int state, String message, T successBody) {
        return buildResponse(state, message, successBody, null, null);
    }

    public static <T> Map<String, List<T>> splitByDuplicates(List<T> inputList, Function<? super T, ?> keyExtractor) {
        Set<Object> seen = new HashSet<>();
        return inputList.stream()
                .collect(Collectors.groupingBy(
                    element -> seen.add(keyExtractor.apply(element)) ? "successBody" : "errorBody"
                ));
    }

    public static <T, R> List<T> getDifference(List<T> listA, List<T> listB, Function<? super T, ? extends R> keyExtractor) {
        Set<R> setB = listB.stream()
            .map(keyExtractor)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        return listA.stream()
                .filter(element -> !setB.contains(keyExtractor.apply(element)))
                .toList();
    }
}
