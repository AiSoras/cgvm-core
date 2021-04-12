package ru.etu.cgvm.objects.nodes;

import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.Node;

import java.util.LinkedList;
import java.util.List;

@ToString
public class Relation extends Node {

    private final List<Arc> arcs = new LinkedList<>();

    public Relation() {
        super(Kind.RELATION);
    }

    public void addArc(Arc arc) {
        arcs.add(arc);
    }

    public List<Arc> getArcs() {
        return new LinkedList<>(arcs);
    }
}
