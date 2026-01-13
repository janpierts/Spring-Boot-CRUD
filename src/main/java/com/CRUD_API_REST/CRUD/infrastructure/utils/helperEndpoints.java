package com.CRUD_API_REST.CRUD.infrastructure.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class helperEndpoints {
    public static <T> Map<String, Object> buildResponse(int state, String message, T successBody, T errorBody) {
        Map<String, Object> response = new HashMap<>();
        response.put("state", state);
        response.put("message", message);
        response.put("successBody", successBody);
        response.put("errorBody", errorBody);
        return response;
    }

    public static <T> Map<String, List<T>> splitByDuplicates(List<T> inputList, Function<? super T, ?> keyExtractor) {
        Set<Object> seen = new HashSet<>();
        return inputList.stream()
                .collect(Collectors.groupingBy(
                    element -> seen.add(keyExtractor.apply(element)) ? "successBody" : "errorBody"
                ));
    }

    public static <T> List<T> getDifference(List<T> listA, List<T> listB) {
        Set<T> setB = new HashSet<>(listB);

        return listA.stream()
                .filter(element -> !setB.contains(element))
                .toList();
    }
}
