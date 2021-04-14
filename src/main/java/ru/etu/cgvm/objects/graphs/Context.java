package ru.etu.cgvm.objects.graphs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.base.Graph;

import java.util.Optional;

@ToString
public class Context extends Graph {

    public Context() {
        super(Kind.CONTEXT);
    }

    @Setter
    @Getter
    private boolean isNegated;

    @Setter
    @Getter
    private String name; // if null => outermost graph

    public Context(Graph enclosingGraph) {
        super(enclosingGraph);
    }

    @Override
    public String getStringRepresentation() {
        return isNegated ? "~" : "" +
                Optional.ofNullable(name).orElse("");
    }
}