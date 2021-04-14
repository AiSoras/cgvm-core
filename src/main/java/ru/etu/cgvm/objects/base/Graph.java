package ru.etu.cgvm.objects.base;

import lombok.Getter;
import lombok.ToString;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@ToString
public abstract class Graph extends GraphObject {

    private final Map<String, GraphObject> objectStore = new HashMap<>();
    @Getter
    private final TypeHierarchy typeHierarchy = new TypeHierarchy();

    protected Graph() {
        super(Kind.GRAPH);
    }

    protected Graph(Kind kind) {
        super(kind);
    }

    protected Graph(Graph enclosingGraph) {
        this();
        if (enclosingGraph != null)
            enclosingGraph.addObject(this);
    }

    public Map<String, GraphObject> getObjects() {
        return new HashMap<>(objectStore);
    }

    public void addObject(GraphObject object) {
        object.setOwner(this);
        objectStore.put(object.getId(), object);
    }

    public Optional<GraphObject> findByID(String id) {
        // if the graph itself has that id, return it
        if (this.id.equals(id)) {
            return Optional.of(this);
        }
        // if object is directly in this graph, return it
        Optional<GraphObject> graphObject = Optional.ofNullable(objectStore.get(id));
        if (graphObject.isPresent()) {
            return graphObject;
        }
        // search through all contained ru.etu.cgvm.objects...
        for (GraphObject object : GraphObjectUtils.getShallowObjects(this, Kind.GRAPH)) {
            graphObject = ((Graph) object).findByID(id);
            if (graphObject.isPresent()) {
                return graphObject;
            }
        }
        return Optional.empty();
    }

    public Optional<Concept> getConceptByCoreferenceLink(String coreferenceLink) {
        Optional<Concept> desiredConcept = GraphObjectUtils.getShallowObjects(this, Kind.CONCEPT).stream()
                .map(Concept.class::cast)
                .filter(concept -> concept.getCoreferenceLinks().stream()
                        .anyMatch(link -> link.equalsIgnoreCase("*" + coreferenceLink)))
                .findFirst();
        if (desiredConcept.isPresent()) {
            return desiredConcept;
        }
        for (GraphObject object : GraphObjectUtils.getShallowObjects(this, Kind.CONTEXT)) { // TODO: Подумать насчет разделения контекстов и лямбд
            desiredConcept = ((Graph) object).getConceptByCoreferenceLink(coreferenceLink);
            if (desiredConcept.isPresent()) {
                return desiredConcept;
            }
        }
        return Optional.empty();
    }

    public Graph getOutermostGraph() {
        if (owner == null) {
            return this;
        } else {
            Graph graph = this.getOwner();
            while (graph.getOwner() != null) {
                graph = graph.getOwner();
            }
            return graph;
        }

    }

    public Collection<Context> getNestedContexts() { // Сохраняется порядок: первые = внешние контексты ! Но не включает самый верхний
        Collection<Context> contexts = GraphObjectUtils.getShallowObjects(this, Kind.CONTEXT)
                .stream().map(Context.class::cast).collect(Collectors.toCollection(LinkedList::new));
        Collection<Context> copy = new LinkedList<>(contexts);
        if (!contexts.isEmpty()) {
            copy.forEach(context -> contexts.addAll(context.getNestedContexts()));
        }
        return contexts;
    }
}