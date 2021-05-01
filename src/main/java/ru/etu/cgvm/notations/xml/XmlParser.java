package ru.etu.cgvm.notations.xml;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.NoSuchElementException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlParser {

    public static <T> T parse(String xml, Class<T> objectClass) {
        var object = XmlObjectMapper.convertXmlStringToObject(xml, objectClass);
        if (Graph.class.isAssignableFrom(objectClass)) {
            establishReferences((Graph) object);
        }
        return object;
    }

    private static void establishArcReferences(Graph graph) {
        graph.getArcs().forEach(arc -> {
            String id = arc.getGraphObjectId();
            if (id != null) {
                var graphObject = graph.getObjectById(id).orElseThrow(() -> new NoSuchElementException("Не найден элемент с id: " + id));
                if (Concept.class.isAssignableFrom(graphObject.getClass())) {
                    arc.setConcept((Concept) graphObject);
                } else if (Context.class.isAssignableFrom(graphObject.getClass())) {
                    arc.setContext((Context) graphObject);
                } else {
                    throw new IllegalArgumentException(String.format("Объект с id '%s' не является концентом или контекстом!", id));
                }
            } else if (arc.getCoreferenceLink() == null || arc.findConcept(graph).isEmpty()) {
                throw new IllegalArgumentException(String.format("Дуга '%s' должна быть соединена с двумя объектами!", arc));
            }
        });
    }

    private static void establishOwnerReferences(Graph graph) {
        graph.getObjects().forEach(object -> object.setOwner(graph));
    }

    private static void establishReferences(Graph graph) {
        establishArcReferences(graph);
        establishOwnerReferences(graph);
        GraphObjectUtils.getNonNestedObjects(graph, Context.class).forEach(XmlParser::establishReferences);
        graph.getTypeHierarchy().getTypeDefinitions().forEach(pair -> establishReferences(pair.getValue()));
    }
}