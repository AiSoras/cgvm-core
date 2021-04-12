package ru.etu.cgvm.objects.nodes;

import lombok.ToString;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.objects.SignatureParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.ObjectID;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.*;

@Slf4j
@ToString
public class Graph extends Concept {

    @Setter
    @Getter
    private boolean isNegated;
    @Getter
    private final Collection<SignatureParameter> signatureParameters = new LinkedList<>(); // Graph is lambda if not empty
    @Setter
    @Getter
    private String context; // Graph is context if not null
    /**
     * Holds the list of ru.etu.cgvm.objects owned by this graph.
     */
    private final Map<ObjectID, GraphObject> objectStore = new HashMap<>();
    @Getter
    private final TypeHierarchy typeHierarchy = new TypeHierarchy();

    public Graph() {
        super(Kind.GRAPH);
    }


    public Graph(Graph enclosingGraph) {
        super();
        if (enclosingGraph != null)
            enclosingGraph.addObject(this);
    }

    public void addSignatureParameter(SignatureParameter parameter) {
        signatureParameters.add(parameter);
    }

    public boolean isLambda() {
        return !signatureParameters.isEmpty();
    }

    public boolean isContext() {
        return context != null || isNegated;
    }

    public Map<ObjectID, GraphObject> getObjects() {
        return new HashMap<>(objectStore);
    }

    /**
     * Attaches the given graph object to a CharGer graph, but doesn't do any
     * consistency check. Doesn't attach any arcs or anything else.
     */
    public void addObject(GraphObject object) {
        log.info("Adding the object '{}' to the graph '{}'...", object.getId(), this.getId());
        object.setOwner(this);
        objectStore.put(object.getId(), object);
    }

    /**
     * Determines whether this graph is nested (at any level) within a given graph.
     * Forbids overlapping contexts; i.e., every graph has at most one enclosing
     * graph.
     *
     * @param potentialOwnerGraph is the potential outermost graph
     * @return true if target graph is nested logically (at any level) within
     * gouter; false otherwise or if gouter is the same as this object.
     */
    public boolean isNestedWithin(Graph potentialOwnerGraph) {
        if (potentialOwnerGraph == null || this.getOwner() == null) {
            return false;
        }
        if (this.getOwner().equals(potentialOwnerGraph)) {
            return true;
        }
        return this.getOwner().isNestedWithin(potentialOwnerGraph);
    }

    /**
     * Find an object by its ID at any level in the target graph. Also checks
     * the target graph itself.
     *
     * @param id a graph ID
     * @return The object if it is found within the graph; null otherwise.
     */
    public Optional<GraphObject> findByID(ObjectID id) {
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
}