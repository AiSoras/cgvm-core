package ru.etu.cgvm.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.nodes.Actor;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.objects.nodes.Relation;

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

    public static Node copyNode(Node node) {
        if (node instanceof Concept) {
            return new Concept((Concept) node);
        } else if (node instanceof Relation) {
            return new Relation((Relation) node);
        } else if (node instanceof Actor) {
            return new Actor((Actor) node);
        }
        throw new IllegalArgumentException("Creating copy error! Unsupported node class: " + node.getClass().getSimpleName());
    }
}