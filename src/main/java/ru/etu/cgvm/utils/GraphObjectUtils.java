package ru.etu.cgvm.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.nodes.Graph;

import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphObjectUtils {

    public static Collection<GraphObject> getShallowObjects(Graph graph, GraphObject.Kind kind) {
        return graph.getObjects()
                .values().stream()
                .filter(object -> object.getKind() == kind)
                .collect(Collectors.toList());
    }
}