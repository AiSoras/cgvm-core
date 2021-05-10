package ru.etu.cgvm.objects.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.Constant;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.nodes.Actor;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.objects.nodes.Relation;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Graph extends GraphObject {

    @JacksonXmlElementWrapper(localName = "objects")
    @JacksonXmlProperty(localName = "object")
    private final Collection<GraphObject> objectStore = new LinkedList<>();
    @Getter
    private final TypeHierarchy typeHierarchy = new TypeHierarchy();

    protected Graph(Graph graph) {
        super(graph);
    }

    @JsonIgnore
    public Collection<GraphObject> getObjects() {
        return new LinkedList<>(objectStore);
    }

    @JsonIgnore
    public boolean isOutermost() {
        return owner == null;
    }

    public void addObject(GraphObject object) {
        object.setOwner(this);
        objectStore.add(object);
    }

    @JsonIgnore
    public Collection<Arc> getArcs() {
        Collection<Arc> arcs = GraphObjectUtils.getAllObjects(this, Relation.class).stream()
                .flatMap(relation -> relation.getArcs().stream()).collect(Collectors.toList());
        arcs.addAll(GraphObjectUtils.getAllObjects(this, Actor.class).stream()
                .flatMap(actor -> actor.getArcs().stream()).collect(Collectors.toList()));
        return arcs;
    }

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
    public Optional<GraphObject> getObjectById(String id) {
        if (this.id.equals(id)) {
            return Optional.of(this);
        }

        Optional<GraphObject> desiredObject = objectStore.stream().filter(object -> object.getId().equals(id)).findFirst();
        if (desiredObject.isPresent()) {
            return desiredObject;
        }

        Collection<Context> contexts = GraphObjectUtils.getNonNestedObjects(this, Context.class);
        for (Context context : contexts) {
            desiredObject = context.getObjectById(id);
            if (desiredObject.isPresent()) {
                return desiredObject;
            }
        }
        return Optional.empty();
    }
}