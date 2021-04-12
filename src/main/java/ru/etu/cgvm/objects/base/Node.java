package ru.etu.cgvm.objects.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.Edge;
import ru.etu.cgvm.objects.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString
public abstract class Node extends GraphObject {

    @Getter
    @Setter
    protected Type type;
    @Getter
    protected List<Edge> edges = new LinkedList<>();

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

    public void deleteEdge(Edge ge) {
        edges.remove(ge);
    }
}