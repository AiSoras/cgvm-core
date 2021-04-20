package ru.etu.cgvm.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.graphs.Context;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphObjectUtils {

    // Не заглядывает во вложенные контексты
    public static <T extends GraphObject> Collection<T> getNonNestedObjects(final Graph graph, final Class<T> objectClass) {
        return graph.getObjects()
                .stream()
                .filter(objectClass::isInstance)
                .map(objectClass::cast)
                .collect(Collectors.toList());
    }

    public static <T extends GraphObject> Collection<T> getAllObjects(final Graph graph, final Class<T> objectClass) {
        Collection<T> objects = new LinkedList<>(getNonNestedObjects(graph, objectClass));
        getNonNestedObjects(graph, Context.class)
                .forEach(context -> objects.addAll(getAllObjects(context, objectClass)));
        return objects;
    }
}