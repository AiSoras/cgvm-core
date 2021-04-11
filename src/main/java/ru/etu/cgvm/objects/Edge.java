package ru.etu.cgvm.objects;

import lombok.Getter;
import lombok.Setter;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;

public class Edge extends GraphObject {

    @Getter
    @Setter
    private Node source;
    @Getter
    @Setter
    private Node destination;

    public enum Direction {
        FROM,
        TO,
        UNLINKED
    }

    public Edge() {
        super(Kind.EDGE);
    }

    public Edge(Node source, Node destination) {
        this();
        this.source = source;
        this.destination = destination;
    }

    public Direction howLinked(Node node) {
        if (node.equals(source)) {
            return Direction.FROM;
        } else if (node.equals(destination)) {
            return Direction.TO;
        } else {
            return Direction.UNLINKED;
        }
    }

    public Node isLinkedTo(Node node) {
        if (node.equals(source)) {
            return destination;
        }
        if (node.equals(destination)) {
            return source;
        }
        return null;
    }

    /**
     * Determines whether the two charger nodes are linked together, by any
     * GEdge.
     *
     * @param node one node
     * @param go2  another node
     * @return true if there is any GEdge connecting the two; false otherwise.
     */
    public static boolean areLinked(Node node, Node go2) {
        for (Object theEdge : node.getEdges()) {
            if (((Edge) theEdge).isLinkedTo(go2) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does everything but the actual removal (de-allocation)
     */
    @Override
    public void abandonObject() {
        if ((source != null) || (destination != null)) {
            detachFromNodes();
        }
    }

    /**
     * Finds the endpoint nodes of the edge, and removes the edge from each of
     * their lists
     */
    public void detachFromNodes() {
        if (source != null) {
            ((Node) source).deleteEdge(this);
        }
        if (destination != null) {
            ((Node) destination).deleteEdge(this);
        }
    }
}
