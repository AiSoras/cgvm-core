package ru.etu.cgvm.objects.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.etu.cgvm.objects.Constant;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Graph extends GraphObject {

    private final Collection<GraphObject> objectStore = new LinkedList<>();
    @Getter
    private final TypeHierarchy typeHierarchy = new TypeHierarchy();

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
                        .anyMatch(link -> link.equalsIgnoreCase(Constant.STAR_MARK + coreferenceLink)))
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
            var graph = this.getOwner();
            while (!graph.isOutermost()) {
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