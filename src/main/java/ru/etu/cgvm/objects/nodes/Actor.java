package ru.etu.cgvm.objects.nodes;

import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.Node;

import java.util.LinkedList;
import java.util.List;

@ToString
public class Actor extends Node {

    private final List<Arc> inputArcs = new LinkedList<>();
    private final List<Arc> outputArcs = new LinkedList<>();

    public Actor() {
        super(Kind.ACTOR);
    }

    public void addInputArc(Arc arc) {
        inputArcs.add(arc);
    }

    public List<Arc> getInputArcs() {
        return new LinkedList<>(inputArcs);
    }

    public void addOutputArc(Arc arc) {
        outputArcs.add(arc);
    }

    public List<Arc> getOutputArcs() {
        return new LinkedList<>(outputArcs);
    }
}
