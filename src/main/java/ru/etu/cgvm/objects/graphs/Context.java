package ru.etu.cgvm.objects.graphs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;

import java.util.Arrays;
import java.util.Optional;

import static ru.etu.cgvm.objects.Constant.TILDA;

@ToString
@Getter
public class Context extends Graph {

    private boolean isSpecialContext;
    private String name; // if null or owner is null <=> outermost graph

    @Setter
    private boolean isNegated;

    public Context(Graph enclosingGraph) {
        super(enclosingGraph);
    }

    public Context() {
    }

    public void setName(String name) {
        this.name = name;
        this.isSpecialContext = Arrays.stream(SpecialContext.values()).anyMatch(value -> value.name().equalsIgnoreCase(name));
    }

    @Override
    public String getStringRepresentation() {
        return isNegated ? TILDA : "" +
                Optional.ofNullable(name).orElse("");
    }

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Context) {
            Context otherContext = (Context) other;
            return isNegated == otherContext.isNegated()
                    && StringUtils.equalsIgnoreCase(name, otherContext.getName()); // Не проверяем объекты
        }
        return false;
    }
}