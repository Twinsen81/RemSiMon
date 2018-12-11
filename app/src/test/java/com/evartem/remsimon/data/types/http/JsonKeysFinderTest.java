package com.evartem.remsimon.data.types.http;

import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class JsonKeysFinderTest {

    private static final String JSON_SIMPLE = "{\"name\": \"Evgeniy\", \"age\":\"37\"}";
    private static final String JSON_SIMPLE_NUMBER = "{\"name\": \"Evgeniy\", \"age\":37}";
    private static final String JSON_NESTED = "{\"Person\": {\"Human\": {\"name\": \"Alex\",\"age\": \"23\"}}}";
    private static final String JSON_ARRAY = "{ \"Person\":{ \"Humans\":[ { \"name\":\"Alex\", \"age\":23 }, { \"name\":\"Joy\", \"age\":24 }, { \"name\":\"Dan\", \"age\":27 } ] } }";

    @Test
    public void shouldReturnEmptyResultOnIncorrectJsonString() {
        // Given two fields to find and an improperly formatted, NULL or empty JSON data string
        // When looking for those fields
        // Then an empty result is returned
        assertTrue(findValuesOfFieldsInJson("name,age", null).isEmpty());
        assertTrue(findValuesOfFieldsInJson("name,age", "").isEmpty());
        assertTrue(findValuesOfFieldsInJson("name,age", "    ").isEmpty());
        assertTrue(findValuesOfFieldsInJson("name,age", "{abc").isEmpty());
        assertTrue(findValuesOfFieldsInJson("name,age", "{abc}").isEmpty());
    }

    @Test
    public void shouldReturnEmptyResultOnIncorrectFieldsString() {
        // Given the incorrect Fields parameter, and a properly formatted JSON data string
        // When looking for those fields
        // Then an empty result is returned
        assertTrue(findValuesOfFieldsInJson(null, JSON_SIMPLE).isEmpty());
        assertTrue(findValuesOfFieldsInJson("", JSON_SIMPLE).isEmpty());
        assertTrue(findValuesOfFieldsInJson("    ", JSON_SIMPLE).isEmpty());
        assertTrue(findValuesOfFieldsInJson(",,,", JSON_SIMPLE).isEmpty());
        assertTrue(findValuesOfFieldsInJson(" , , , ,", JSON_SIMPLE).isEmpty());
    }

    @Test
    public void shouldFindValuesInAnyKindOfJson() {
        // Given the Fields parameter, and a properly formatted JSON data string
        // When looking for those fields
        // Then the expected number of values is found in the JSON

        assertEquals(1, findValuesOfFieldsInJson("name", JSON_SIMPLE).size());
        assertEquals(1, findValuesOfFieldsInJson("name", JSON_SIMPLE_NUMBER).size());
        assertEquals(1, findValuesOfFieldsInJson("person.human.name", JSON_NESTED).size());
        assertEquals(1, findValuesOfFieldsInJson("person.humans.name", JSON_ARRAY).size());

        assertEquals(2, findValuesOfFieldsInJson("name,age", JSON_SIMPLE).size());
        assertEquals(2, findValuesOfFieldsInJson("name,age", JSON_SIMPLE_NUMBER).size());
        assertEquals(2, findValuesOfFieldsInJson("person.human.name,person.human.age", JSON_NESTED).size());
        assertEquals(2, findValuesOfFieldsInJson("person.humans.name,person.humans.age", JSON_ARRAY).size());
    }

    @Test
    public void shouldReturnFirstFoundValueIfMultipleFieldsWithTheSameNameExist() {

        // Given JSON which has several values with the same name
        // When looking for those names (fields)
        Map<String, String> result = findValuesOfFieldsInJson("person.humans.name,person.humans.age", JSON_ARRAY);

        //Then the first found values are returned
        assertEquals(2, result.size());
        assertTrue(result.containsKey("person.humans.name"));
        assertEquals("Alex", result.get("person.humans.name"));
        assertTrue(result.containsKey("person.humans.age"));
        assertEquals("23", result.get("person.humans.age"));
    }

    private Map<String, String> findValuesOfFieldsInJson(String fields, String jsonData) {
        return (new JsonKeysFinder(fields)).getKeysAndValues(jsonData);
    }

}