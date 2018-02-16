package com.udacity.sandwichclub.utils;

import com.udacity.sandwichclub.model.Sandwich;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {

    private static final Pattern SINGLE_VALUE_PATTERN =
            Pattern.compile("\"([^\"]*?)\":\"(([^\"]|(\\\\\"))*[^\\\\]{1}?)\",");
    private static final Pattern ARRAY_VALUE_PATTERN =
            Pattern.compile("\"([^\"]*?)\":\\[([^\\]]*?)\\]");
    private static final int KEY_NAME_POSITION = 1;
    private static final int KEY_VALUE_POSITION = 2;

    /**
     * Parses a json-object to a sandwich-object by using regex.
     *
     * @param json  the json-data of the sandwich.
     * @return      the parsed json data as a sandwich object.
     */
    public static Sandwich parseSandwichJson(String json) {

        Sandwich sandwich = new Sandwich();

        HashMap<String, String> singleValues = parseJsonFor(json, true);

        sandwich.setMainName(singleValues.get("mainName"));
        sandwich.setPlaceOfOrigin(singleValues.get("placeOfOrigin"));
        sandwich.setDescription(singleValues.get("description"));
        sandwich.setImage(singleValues.get("image"));

        HashMap<String, String> arrayValues = parseJsonFor(json, false);

        sandwich.setAlsoKnownAs(parseValueToArray(arrayValues.get("alsoKnownAs")));
        sandwich.setIngredients(parseValueToArray(arrayValues.get("ingredients")));

        return sandwich;
    }

    /**
     * Parses the json-string into a keyValueList by using regex.
     *
     * @param json              the json-data of the sandwiches
     * @param isSinglePattern   TRUE if only single-value variables should be returned.
     *                          FALSE if only array-value variables should be returned.
     * @return                  the parsed json as a keyValueList.
     */
    private static HashMap<String, String> parseJsonFor(String json, boolean isSinglePattern) {
        Pattern pattern = isSinglePattern ? SINGLE_VALUE_PATTERN : ARRAY_VALUE_PATTERN;
        HashMap<String, String> parsedJson = new HashMap<>();

        Matcher m = pattern.matcher(json);
        while (m.find()) {
            String keyName = m.group(KEY_NAME_POSITION);
            String value = m.group(KEY_VALUE_POSITION);

            // if the key has an empty value then remove the value.
            if (value.equals("")) {
                value = null;
            }

            // remove the string-identifier from arrays. OR
            // change \" into " in Strings for better readability.
            if (!isSinglePattern && value != null) {
                value = value.replace("\"", "").trim();
            } else if (value != null){
                value = value.replace("\\\"", "\"").trim();
            }

            // save json KeyValue-Pair.
            parsedJson.put(keyName, value);
        }

        return parsedJson;
    }

    /**
     * Convert a string containing commas into a List-object.
     *
     * @param value     the string containing a list separated by commas.
     * @return          the list as a List-object.
     */
    private static List<String> parseValueToArray(String value) {
        return value == null ? null : Arrays.asList(value.split(","));
    }
}
