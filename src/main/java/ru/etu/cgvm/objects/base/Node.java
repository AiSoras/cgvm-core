package ru.etu.cgvm.objects.base;

import lombok.Getter;
import lombok.Setter;
import ru.etu.cgvm.objects.Edge;
import ru.etu.cgvm.objects.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Node extends GraphObject {

    @Getter
    @Setter
    protected Type type;
    @Getter
    protected List<Edge> edges = new LinkedList<>();
    protected Mark mark = new Mark();

    protected Node() {
        super(Kind.NODE);
    }

    protected Node(Kind kind) {
        super(kind);
    }

    /**
     * Returns a list of NODEs depending on whether they are "from" or "to"
     * the calling node.
     *
     * @param direction either <code>EDGE.Direction.FROM</code> (for nodes linked with
     *                  arrows directed toward this node, or <code>EDGE.Direction.TO</code> (for
     *                  nodes linked with arrows directed away from this node).
     * @return the list of nodes that are connected in the appropriate direction
     */
    public List<Node> getLinkedNodes(Edge.Direction direction) {
        Edge.Direction desiredDirection;
        Function<Edge, Node> desiredNodeGetter;
        switch (direction) {
            case TO -> {
                desiredDirection = Edge.Direction.FROM;
                desiredNodeGetter = Edge::getDestination;
            }
            case FROM -> {
                desiredDirection = Edge.Direction.TO;
                desiredNodeGetter = Edge::getSource;
            }
            default -> throw new IllegalArgumentException("Direction of edge must be equal 'FROM' or 'TO'!");
        }
        return edges.stream()
                .filter(edge -> edge.howLinked(this) == desiredDirection)
                .map(desiredNodeGetter)
                .collect(Collectors.toList());
    }

    /**
     * Disconnects itself from any other GNodes, by telling each of its edges to
     * erase itself.
     */
    @Override
    public void abandonObject() {
        while (!edges.isEmpty()) {
            Edge toBeRemoved = edges.get(0);
            toBeRemoved.getOwner().forgetObject(toBeRemoved);
        }
    }

    public void deleteEdge(Edge ge) {
        edges.remove(ge);
    }


    /**
     * Does the marker indicate object has changed?
     *
     * @return true if ready
     */
    public boolean isChanged() {
        return mark.isChanged();
    }

    /**
     * Sets this node's mark to indicate that it's been changed.
     *
     * @param b whether to tell the mark that the node is changed or not.
     */
    public void setChanged(boolean b) {
        mark.setChanged(b);
    }

    /**
     * Does the marker indicate an active input concept to an actor?
     */
    public boolean isActive() {
        return mark.isActive();
    }

    /**
     * Used when activating CG actors to mark this node as active in a sequence
     * of firings.
     *
     * @param b
     */
    public void setActive(boolean b) {
        mark.setActive(b);
    }

    /**
     * Contains the information used for the marking algorithm for actor updating.
     */
    private static class Mark {
        /**
         * Whether the node we're marking has been changed during this activation
         */
        @Setter
        @Getter
        boolean changed;
        /**
         * Whether this node is currently participating in an actor's firing
         */
        @Setter
        @Getter
        boolean active;
    }
}