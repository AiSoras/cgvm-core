package ru.etu.cgvm.objects.nodes;

import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ToString(callSuper = true)
@NoArgsConstructor
public class Actor extends Node {

    private final List<Arc> inputArcs = new LinkedList<>();
    private final List<Arc> outputArcs = new LinkedList<>();

    public Actor(Actor actor) {
        super(actor);
        inputArcs.addAll(actor.getInputArcs().stream().map(Arc::new).collect(Collectors.toList()));
        outputArcs.addAll(actor.getOutputArcs().stream().map(Arc::new).collect(Collectors.toList()));
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

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Actor) {
            var otherActor = (Actor) other;
            if (Objects.equals(type, otherActor.getType())
                    && inputArcs.size() == otherActor.getInputArcs().size()
                    && outputArcs.size() == otherActor.getInputArcs().size()) {
                return IntStream.range(0, inputArcs.size())
                        .allMatch(index -> inputArcs.get(index).isIdentical(otherActor.getInputArcs().get(index),
                                owner.getOutermostGraph(), otherActor.getOwner().getOutermostGraph()))
                        && IntStream.range(0, outputArcs.size())
                        .allMatch(index -> outputArcs.get(index).isIdentical(otherActor.getOutputArcs().get(index),
                                owner.getOutermostGraph(), otherActor.getOwner().getOutermostGraph()));
            }
        }
        return false;
    }
}
