package ru.etu.cgvm.objects.graphs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.objects.base.Graph;

import java.util.Optional;

@NoArgsConstructor
public class Context extends Graph {

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
        return Optional.ofNullable(name).orElse("");
    }
}