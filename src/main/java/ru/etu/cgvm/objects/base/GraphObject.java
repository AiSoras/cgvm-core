package ru.etu.cgvm.objects.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.etu.cgvm.objects.nodes.Graph;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphObject that = (GraphObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Convenience method for telling the object to tell its graph to forget it.
     */
    public void forgetObject() {
        Graph g = this.getOwner();
        g.forgetObject(this);
    }

    /**
     * Abstract method that disconnects object from any other ru.etu.cgvm.objects. Doesn't
     * delete from its owning graph, from Notio, or destroy itself.
     */
    public abstract void abandonObject();

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
