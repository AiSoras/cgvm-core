package ru.etu.cgvm.objects.nodes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;

import java.util.Objects;

@ToString(callSuper = true)
@Setter
@Getter
public class Relation extends Node {

    private Arc input;
    private Arc output;

    public Relation() {
    }

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Relation
                && Objects.equals(type, ((Node) other).getType())) {
            Relation otherRelation = (Relation) other;
            return input.isIdentical(otherRelation.getInput(), owner) // Проверяем и дуги, то есть вместо 1 объекта сразу 3
                    && output.isIdentical(otherRelation.getOutput(), owner);
        }
        return false;
    }
}