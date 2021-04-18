package ru.etu.cgvm.objects.nodes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.Node;

@ToString
@Setter
@Getter
public class Relation extends Node {

    private Arc input;
    private Arc output;

    public Relation() {
        super(Kind.RELATION);
    }
}