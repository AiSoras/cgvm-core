package ru.etu.cgvm.objects.nodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@ToString(callSuper = true)
@Setter
@Getter
@NoArgsConstructor
public class Relation extends Node {

    private Arc input;
    private Arc output;

    public Relation(Relation relation) {
        super(relation);
        input = new Arc(relation.getInput());
        output = new Arc(relation.getOutput());
    }

    public Collection<Arc> getArcs() {
        return Arrays.asList(input, output);
    }

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Relation
                && Objects.equals(type, ((Node) other).getType())) {
            var otherRelation = (Relation) other;
            return input.isIdentical(otherRelation.getInput(), owner.getOutermostGraph(), otherRelation.getOwner().getOutermostGraph())
                    && output.isIdentical(otherRelation.getOutput(), owner.getOutermostGraph(), otherRelation.getOwner().getOutermostGraph());
        }
        return false;
    }
}