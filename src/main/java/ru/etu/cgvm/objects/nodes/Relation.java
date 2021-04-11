package ru.etu.cgvm.objects.nodes;

import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.Node;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
public class Relation extends Node {

    private final List<Arc> arcs = new LinkedList<>();

    public void addArc(Arc arc) {
        arcs.add(arc);
    }

    public List<Arc> getArcs() {
        return new LinkedList<>(arcs);
    }
}
