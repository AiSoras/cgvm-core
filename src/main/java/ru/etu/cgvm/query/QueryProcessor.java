package ru.etu.cgvm.query;

import ru.etu.cgvm.notations.cgif.parser.CgifParser;
import ru.etu.cgvm.notations.cgif.parser.ParseException;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryProcessor {

    public Collection<Context> select(final Context originalGraph, final Context query) {
        Collection<Context> result = new LinkedList<>();
        final Map<Context, Map<Context, Map<GraphObject, Collection<GraphObject>>>> overlaps = getGraphProjectionByContext(originalGraph, query);

        final Context context = new Context();

        return result;
    }


    public static void main(String[] args) throws ParseException {
        Context originalGraph = new CgifParser().parse("[Go *x]\n" +
                "    (Agnt ?x [Person: Ivanov])\n" +
                "    (Dest ?x [Work])\n" +
                "    (Inst ?x [Metro])");
        Graph query = new CgifParser().parse("[SELECT (Agnt [Go] [Person: {*}])]");
        final Map<Context, Map<Context, Map<GraphObject, Collection<GraphObject>>>> overlaps = getGraphProjectionByContext(originalGraph, (Context) query.getObjects().iterator().next());
        System.out.println();
    }

    // Map<Context, Map<Context, Map<GraphObject, Collection<GraphObject>>>>
    //     ^ контекст из запроса
    //                  ^ контекст исходного графа (может несколько подходить)
    //                              ^ объект контекста из запроса и соотвествующие ему элементы контекста исходного графа
    private static Map<Context, Map<Context, Map<GraphObject, Collection<GraphObject>>>> getGraphProjectionByContext(final Context originalGraph, final Context targetGraph) {
        Map<Context, Map<Context, Map<GraphObject, Collection<GraphObject>>>> result = new HashMap<>();
        Collection<Context> contexts = new LinkedList<>();
        contexts.add(targetGraph); // Учитываем основной контекст
        contexts.addAll(GraphObjectUtils.getAllObjects(originalGraph, Context.class));
        contexts.forEach(targetContext -> result.put(targetContext, getContextsWithFullProjection(originalGraph, targetContext)));
        return result;
    }

    private static Map<Context, Map<GraphObject, Collection<GraphObject>>> getContextsWithFullProjection(final Context originalGraph, final Context targetContext) {
        Map<Context, Map<GraphObject, Collection<GraphObject>>> result = new HashMap<>();
        Collection<Context> contexts = new LinkedList<>();
        contexts.add(originalGraph); // Учитываем основной контекст
        contexts.addAll(GraphObjectUtils.getAllObjects(originalGraph, Context.class));
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
