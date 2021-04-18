package ru.etu.cgvm.objects.graphs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.base.Graph;

import java.util.Arrays;
import java.util.Optional;

@ToString
@Getter
public class Context extends Graph {

    private boolean isSpecialContext;
    private String name; // if null => outermost graph

    @Setter
    private boolean isNegated;

    public Context(Graph enclosingGraph) {
        super(enclosingGraph);
    }

    public Context() {
        super(Kind.CONTEXT);
    }

    public void setName(String name) {
        this.name = name;
        this.isSpecialContext = Arrays.stream(SpecialContext.values()).anyMatch(value -> value.name().equalsIgnoreCase(name));
    }

    @Override
    public String getStringRepresentation() {
        return isNegated ? "~" : "" +
                Optional.ofNullable(name).orElse("");
    }
}