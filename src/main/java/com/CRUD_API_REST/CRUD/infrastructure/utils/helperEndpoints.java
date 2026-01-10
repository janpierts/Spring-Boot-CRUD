package com.CRUD_API_REST.CRUD.infrastructure.utils;

import java.util.HashMap;
import java.util.Map;

public class helperEndpoints {
    public static <T> Map<String, Object> buildResponse(int state, String message, T successBody, T errorBody) {
        Map<String, Object> response = new HashMap<>();
        response.put("state", state);
        response.put("message", message);
        response.put("successBody", successBody);
        response.put("errorBody", errorBody);
        return response;
    }
}
