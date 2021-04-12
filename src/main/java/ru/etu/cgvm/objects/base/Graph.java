package ru.etu.cgvm.objects.base;

import lombok.Getter;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Graph extends GraphObject {

    private final Map<ObjectID, GraphObject> objectStore = new HashMap<>();
    @Getter
    private final TypeHierarchy typeHierarchy = new TypeHierarchy();

    protected Graph() {
        super(Kind.GRAPH);
    }

    protected Graph(Graph enclosingGraph) {
        this();
        if (enclosingGraph != null)
            enclosingGraph.addObject(this);
    }

    public Map<ObjectID, GraphObject> getObjects() {
        return new HashMap<>(objectStore);
    }

    /**
     * Attaches the given graph object to a CharGer graph, but doesn't do any
     * consistency check. Doesn't attach any arcs or anything else.
     */
    public void addObject(GraphObject object) {
        object.setOwner(this);
        objectStore.put(object.getId(), object);
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