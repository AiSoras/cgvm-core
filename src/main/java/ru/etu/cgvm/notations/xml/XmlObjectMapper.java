package ru.etu.cgvm.notations.xml;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlObjectMapper {

    private static final XmlMapper mapper = new XmlMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // for pretty printing
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true); // for header <?xml version="1.0" encoding="UTF-8"?>
    }

    public static <T> String convertObjectToXmlString(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("The exception is occurred during object serialization... ", e);
        }
    }

    public static <T> File convertObjectToXmlFile(T object, String fileName) {
        try {
            var file = new File(fileName);
            mapper.writeValue(file, object);
            return file;
        } catch (IOException e) {
            throw new IllegalStateException("The exception is occurred during object serialization... ", e);
        }
    }

    public static <T> T convertXmlStringToObject(String xml, Class<T> objectClass) {
        try {
            return mapper.readValue(xml, objectClass);
        } catch (IOException e) {
            throw new IllegalStateException("The exception is occurred during object deserialization... ", e);
        }
    }

    public static <T> T convertXmlFileToObject(String fileName, Class<T> objectClass) {
        try {
            return mapper.readValue(new File(fileName), objectClass);
        } catch (IOException e) {
            throw new IllegalStateException("The exception is occurred during object deserialization... ", e);
        }
    }
}