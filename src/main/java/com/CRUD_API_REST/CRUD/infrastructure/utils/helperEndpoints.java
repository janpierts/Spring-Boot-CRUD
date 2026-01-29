package com.CRUD_API_REST.CRUD.infrastructure.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class helperEndpoints {
    //region Build Response Methods
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

    public static <T> Map<String, Object> buildResponse(int state, String message, T Body) {
        return buildResponse(state, message, state == 1 ? Body : null, state == -1 ? Body : null, null);
    }
    //endregion

    //region List Utility Methods
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
    //endregion
    
    //region String Utility Methods
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    private static final Pattern ZIP_CODE_PATTERN = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$");
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    private static final Pattern IPV6_PATTERN = Pattern.compile("^(?:[a-fA-F0-9]{1,4}:){7}[a-fA-F0-9]{1,4}$");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#?([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$");
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|6(?:011|5[0-9]{2})[0-9]{12}|(?:2131|1800|35\\d{3})\\d{11})$");
    private static final Pattern SSN_PATTERN = Pattern.compile("^(?!000|666|9\\d{2})\\d{3}-(?!00)\\d{2}-(?!0000)\\d{4}$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>");
    private static final Pattern JSON_PATTERN = Pattern.compile("^(\\{.*\\}|\\[.*\\])$");
    private static final Pattern XML_PATTERN = Pattern.compile("^<\\?xml.*\\?>.*</.*>$", Pattern.DOTALL);
    private static final Pattern BASE64_PATTERN = Pattern.compile("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
    private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");
    private static final Pattern BINARY_PATTERN = Pattern.compile("^[01]+$");
    private static final Pattern OCTAL_PATTERN = Pattern.compile("^[0-7]+$");
    private static final Pattern LATITUDE_PATTERN = Pattern.compile("^(-?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?))$");
    private static final Pattern LONGITUDE_PATTERN = Pattern.compile("^(-?((1[0-7]\\d(\\.\\d+)?|180(\\.0+)?|[1-9]?\\d(\\.\\d+)?)))$");
    private static final Pattern HTML_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(97(8|9))?\\d{9}(\\d|X)$");
    private static final Pattern URL_SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&[a-zA-Z]{2,6};");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("^\\s+$");
    private static final Pattern NON_ALPHANUMERIC_PATTERN = Pattern.compile("^[^a-zA-Z0-9]+$");
    private static final Pattern PRINTABLE_ASCII_PATTERN = Pattern.compile("^[\\x20-\\x7E]+$");
    private static final Pattern NON_PRINTABLE_ASCII_PATTERN = Pattern.compile("^[\\x00-\\x1F\\x7F]+$");
    private static final Pattern VOWELS_PATTERN = Pattern.compile("^[AEIOUaeiou]+$");
    private static final Pattern CONSONANTS_PATTERN = Pattern.compile("^[BCDFGHJKLMNPQRSTVWXYZbcdfghjklmnpqrstvwxyz]+$");   
    private static final Pattern PALINDROME_PATTERN = Pattern.compile("^(?:(.)(?:(?:(?<=\\1).*(?=\\1))?\\1))$");
    private static final Pattern ANAGRAM_PATTERN = Pattern.compile("^(?=(?:.*(.)(?=.*\\1)){2,}).+$");
    private static final Pattern URL_WITH_QUERY_PATTERN = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*\\?.+$");
    private static final Pattern HTML_COMMENT_PATTERN = Pattern.compile("<!--(.*?)-->", Pattern.DOTALL);
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile(" {2,}");
    private static final Pattern LEADING_TRAILING_SPACES_PATTERN = Pattern.compile("^\\s+|\\s+$");
    private static final Pattern NUMERIC_STRING_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern ALPHABETIC_STRING_PATTERN = Pattern.compile("^[A-Za-z]+$");
    private static final Pattern ALPHANUMERIC_STRING_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("^[!@#$%^&*(),.?\":{}|<>]+$");
    private static final Pattern LONG_PATTERN = Pattern.compile("^-?\\d+$");

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    public static boolean isValidURL(String url) {
        return URL_PATTERN.matcher(url).matches();
    }
    public static boolean isValidZipCode(String zipCode) {
        return ZIP_CODE_PATTERN.matcher(zipCode).matches();
    }
    public static boolean isValidDate(String date) {
        return DATE_PATTERN.matcher(date).matches();
    }
    public static boolean isValidTime(String time) {
        return TIME_PATTERN.matcher(time).matches();
    }
    public static boolean isValidIPv4(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }
    public static boolean isValidIPv6(String ip) {
        return IPV6_PATTERN.matcher(ip).matches();
    }
    public static boolean isValidHexColor(String color) {
        return HEX_COLOR_PATTERN.matcher(color).matches();
    }
    public static boolean isValidCreditCard(String cardNumber) {
        return CREDIT_CARD_PATTERN.matcher(cardNumber).matches();
    }
    public static boolean isValidSSN(String ssn) {  
        return SSN_PATTERN.matcher(ssn).matches();
    }
    public static boolean isValidUUID(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }
    public static boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    public static boolean containsHTMLTags(String input) {
        return HTML_TAG_PATTERN.matcher(input).find();
    }
    public static boolean isValidJSON(String json) {
        return JSON_PATTERN.matcher(json).matches();
    }
    public static boolean isValidXML(String xml) {
        return XML_PATTERN.matcher(xml).matches();
    }
    public static boolean isValidBase64(String base64) {
        return BASE64_PATTERN.matcher(base64).matches();
    }
    public static boolean isValidHexadecimal(String hex) {
        return HEXADECIMAL_PATTERN.matcher(hex).matches();
    }
    public static boolean isValidBinary(String binary) {
        return BINARY_PATTERN.matcher(binary).matches();
    }
    public static boolean isValidOctal(String octal) {
        return OCTAL_PATTERN.matcher(octal).matches();
    }
    public static boolean isValidLatitude(String latitude) {
        return LATITUDE_PATTERN.matcher(latitude).matches();
    }
    public static boolean isValidLongitude(String longitude) {
        return LONGITUDE_PATTERN.matcher(longitude).matches();
    }
    public static boolean isValidHTMLColor(String color) {
        return HTML_COLOR_PATTERN.matcher(color).matches();
    }
    public static boolean isValidMacAddress(String mac) {
        return MAC_ADDRESS_PATTERN.matcher(mac).matches();
    }
    public static boolean isValidISBN(String isbn) {
        return ISBN_PATTERN.matcher(isbn).matches();
    }
    public static boolean isValidUrlSlug(String slug) {
        return URL_SLUG_PATTERN.matcher(slug).matches();
    }
    public static boolean containsHTMLEntities(String input) {
        return HTML_ENTITY_PATTERN.matcher(input).find();
    }
    public static boolean containsOnlyWhitespace(String input) {
        return WHITESPACE_PATTERN.matcher(input).matches();
    }
    public static boolean containsOnlyNonAlphanumeric(String input) {
        return NON_ALPHANUMERIC_PATTERN.matcher(input).matches();
    }
    public static boolean containsOnlyPrintableASCII(String input) {
        return PRINTABLE_ASCII_PATTERN.matcher(input).matches();
    }
    public static boolean containsOnlyNonPrintableASCII(String input) {
        return NON_PRINTABLE_ASCII_PATTERN.matcher(input).matches();
    }
    public static boolean containsOnlyVowels(String input) {
        return VOWELS_PATTERN.matcher(input).matches();
    }
    public static boolean containsOnlyConsonants(String input) {
        return CONSONANTS_PATTERN.matcher(input).matches();
    }
    public static boolean isPalindrome(String input) {
        return PALINDROME_PATTERN.matcher(input).matches();
    }
    public static boolean isAnagram(String input) {
        return ANAGRAM_PATTERN.matcher(input).matches();
    }
    public static boolean isValidURLWithQuery(String url) {
        return URL_WITH_QUERY_PATTERN.matcher(url).matches();
    }
    public static boolean containsHTMLComments(String input) {
        return HTML_COMMENT_PATTERN.matcher(input).find();
    }
    public static String removeMultipleSpaces(String input) {   
        return MULTIPLE_SPACES_PATTERN.matcher(input).replaceAll(" ");
    }
    public static String trimLeadingTrailingSpaces(String input) {          
        return LEADING_TRAILING_SPACES_PATTERN.matcher(input).replaceAll("");
    }
    public static boolean isNumericString(String input) {
        return NUMERIC_STRING_PATTERN.matcher(input).matches();
    }
    public static boolean isAlphabeticString(String input) {
        return ALPHABETIC_STRING_PATTERN.matcher(input).matches();
    }
    public static boolean isAlphanumericString(String input) {
        return ALPHANUMERIC_STRING_PATTERN.matcher(input).matches();
    }
    public static boolean containsOnlySpecialCharacters(String input) { 
        return SPECIAL_CHARACTERS_PATTERN.matcher(input).matches();
    }
    public static boolean isLongString(String input) {
        return LONG_PATTERN.matcher(input).matches();
    }
    public static String sanitizeForSearch(String input) {
        if (input == null) return "";
        String clean = trimLeadingTrailingSpaces(input);
        clean = removeMultipleSpaces(clean);
        return HTML_TAG_PATTERN.matcher(clean).replaceAll("");
    }
    public static boolean isValidForSearch(String input, SearchType type) {
        if (input == null || input.isBlank()) return false;

        return switch (type) {
            case ONLY_ALPHABETIC -> ALPHABETIC_STRING_PATTERN.matcher(input).matches();
            case ALPHANUMERIC -> ALPHANUMERIC_STRING_PATTERN.matcher(input).matches();
            case NUMERIC_LONG -> isLongString(input) && isValidLongRange(input);
            case SAFE_TEXT -> !containsHTMLTags(input) && !containsHTMLComments(input);
        };
    }

    public enum SearchType {
        ONLY_ALPHABETIC, ALPHANUMERIC, NUMERIC_LONG, SAFE_TEXT
    }

    private static boolean isValidLongRange(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    //endregion
}