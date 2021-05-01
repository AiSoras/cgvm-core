package ru.etu.cgvm.notations.xml;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.etu.cgvm.objects.base.GraphObject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlGenerator {

    public static String convert(GraphObject graphObject) {
        return XmlObjectMapper.convertObjectToXmlString(graphObject);
    }
}
