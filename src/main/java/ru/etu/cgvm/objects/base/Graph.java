package ru.etu.cgvm.objects.base;

import lombok.Getter;
import lombok.ToString;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@ToString
public abstract class Graph extends GraphObject {

    private final Collection<GraphObject> objectStore = new LinkedList<>();
    @Getter
    private final TypeHierarchy typeHierarchy = new TypeHierarchy();

    protected Graph() {
    }

    protected Graph(Graph enclosingGraph) {
        if (enclosingGraph != null)
            enclosingGraph.addObject(this);
    }

    public Collection<GraphObject> getObjects() {
        return new LinkedList<>(objectStore);
    }

    public boolean isOutermost() {
        return owner == null;
    }

    public void addObject(GraphObject object) {
        object.setOwner(this);
        objectStore.add(object);
    }

    public Optional<Concept> getConceptByCoreferenceLink(String coreferenceLink) {
        Optional<Concept> desiredConcept = GraphObjectUtils.getNonNestedObjects(this, Concept.class).stream()
                .filter(concept -> concept.getCoreferenceLinks().stream()
                        .anyMatch(link -> link.equalsIgnoreCase("*" + coreferenceLink)))
                .findFirst();
        if (desiredConcept.isPresent()) {
            return desiredConcept;
        }
        for (Graph object : GraphObjectUtils.getNonNestedObjects(this, Context.class)) {
            desiredConcept = object.getConceptByCoreferenceLink(coreferenceLink);
            if (desiredConcept.isPresent()) {
                return desiredConcept;
            }
        }
        return Optional.empty();
    }

    public Graph getOutermostGraph() {
        if (isOutermost()) {
            return this;
        } else {
            Graph graph = this.getOwner();
            while (!isOutermost()) {
                graph = graph.getOwner();
            }
            return graph;
        }

    }

    public Collection<Context> getNestedContexts() { // Сохраняется порядок: первые = внешние контексты ! Но не включает самый верхний
        Collection<Context> contexts = GraphObjectUtils.getNonNestedObjects(this, Context.class);
        Collection<Context> copy = new LinkedList<>(contexts);
        if (!contexts.isEmpty()) {
            copy.forEach(context -> contexts.addAll(context.getNestedContexts()));
        }
        return contexts;
    }
}