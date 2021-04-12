package ru.etu.cgvm.objects.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.nodes.Graph;

@EqualsAndHashCode
@ToString
public abstract class GraphObject {

    public enum Kind {
        NODE,
        EDGE,
        GRAPH
    }

    @Getter
    @Setter
    protected ObjectID id = new ObjectID();
    @Getter
    protected final Kind kind;
    @Getter
    @Setter
    protected Graph owner;

    protected GraphObject(Kind kind) {
        this.kind = kind;
    }

    /**
     * Find the graph that is the outermost graph of the given object.
     *
     * @return outermost graph, self if this one is the outermost
     */
    @JsonIgnore
    public Graph getOutermostGraph() {
        if (owner == null) {
            if (this instanceof Graph) {
                return (Graph) this;
            } else {
                return null;
            }
        } else {
            Graph graph = this.getOwner();        // we've already checked to ensure that g isn't null
            while (graph.getOwner() != null) {
                graph = graph.getOwner();
            }
            return graph;
        }
    }
}
