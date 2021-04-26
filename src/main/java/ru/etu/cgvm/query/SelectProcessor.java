package ru.etu.cgvm.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectProcessor {

    public static Collection<Context> select(final Context originalGraph, final Context query) {
        Collection<Context> result = new LinkedList<>();
        Map<Context, Map<GraphObject, Collection<GraphObject>>> overlaps = getContextsWithFullProjection(originalGraph, query);
        overlaps.forEach((originalContext, overlap) -> result.addAll(getObjectsContexts(originalContext, overlap)));
        return result;
    }

    private static Collection<Context> getObjectsContexts(final Context context, final Map<GraphObject, Collection<GraphObject>> overlap) {
        overlap.entrySet().forEach(entry -> {
            if (entry.getKey() instanceof Context) {
                entry.setValue(entry.getValue().stream()
                        .flatMap(desiredContext -> getObjectsContexts((Context) entry.getKey(),
                                getContextProjection((Context) desiredContext, (Context) entry.getKey())).stream())
                        .collect(Collectors.toList()));
            }
        });

        int occurrenceCount = getOccurrenceCount(overlap);
        List<Context> contexts = new ArrayList<>(occurrenceCount);
        IntStream.range(0, occurrenceCount).forEach(index -> contexts.add(new Context(context)));
        overlap.forEach((queryObject, identicalObjects) -> {
            int identicalObjectCount = identicalObjects.size();
            for (var i = 0; i < identicalObjectCount; i++) {
                var graphObject = ((List<GraphObject>) identicalObjects).get(i);
                // Имеется occurrenceCount контекстов, добавляем в него объект occurrenceCount/identicalObjectCount раз по определенной логике:
                // Начинаем с i и, пропуская (identicalObjectCount - 1) элемент, добавляем в список
                // Таким образом, получим все возможные наборы элементов
                IntStream.iterate(i, index -> index + identicalObjectCount)
                        .limit(occurrenceCount / identicalObjectCount)
                        .forEach(index -> contexts.get(index).addObject(getClonedObject(graphObject)));
            }
        });
        return contexts;
    }

    private static GraphObject getClonedObject(GraphObject graphObject) {
        if (graphObject instanceof Node) {
            return GraphObjectUtils.copyNode((Node) graphObject);
        } else if (graphObject instanceof Context) {
            return graphObject;
        }
        throw new IllegalArgumentException("Getting cloned object error! Unsupported object class: " + graphObject.getClass().getSimpleName());
    }

    // П(Collection<GraphObject> sizes)
    private static int getOccurrenceCount(Map<GraphObject, Collection<GraphObject>> overlap) {
        return overlap.values().stream().mapToInt(Collection::size).reduce((a, b) -> a * b).orElse(0);
    }

    // Map<Context, Map<GraphObject, Collection<GraphObject>>>
    //     ^ контекст исходного графа (может несколько подходить)
    //                 ^ объект контекста из запроса и соотвествующие ему элементы контекста исходного графа
    // Получаем, что количество подходящих результатов равно: сумме П(Collection<GraphObject> sizes) по каждому контексту-ключу в словаре
    // Общее число = умножению сумм П(Collection<GraphObject> sizes)
    private static Map<Context, Map<GraphObject, Collection<GraphObject>>> getContextsWithFullProjection(final Context originalGraph, final Context targetContext) {
        Map<Context, Map<GraphObject, Collection<GraphObject>>> result = new HashMap<>();
        Collection<Context> contexts = new LinkedList<>(GraphObjectUtils.getAllObjects(originalGraph, Context.class));
        contexts.add(originalGraph); // Учитываем основной контекст
        for (Context context : contexts) {
            Map<GraphObject, Collection<GraphObject>> projection = getContextProjection(context, targetContext);
            if (isProjectionFull(projection)) {
                result.put(context, projection);
            }
        }
        return result;
    }

    private static Map<GraphObject, Collection<GraphObject>> getContextProjection(final Graph originalContext, final Graph targetContext) {
        final Map<GraphObject, Collection<GraphObject>> overlap = new HashMap<>();
        targetContext.getObjects().forEach(targetObject ->
                overlap.put(targetObject,
                        originalContext.getObjects().stream()
                                .filter(originalObject -> originalObject.isIdentical(targetObject))
                                .collect(Collectors.toList())));
        return overlap;
    }

    private static boolean isProjectionFull(final Map<GraphObject, Collection<GraphObject>> overlap) {
        return overlap.entrySet().stream().noneMatch(entry -> entry.getValue().isEmpty());
    }
}