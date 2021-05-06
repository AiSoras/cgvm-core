package ru.etu.cgvm.objects.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.BaseRelation;
import ru.etu.cgvm.objects.base.GraphObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ToString(callSuper = true)
@NoArgsConstructor
public class Actor extends BaseRelation {

    @JacksonXmlElementWrapper(localName = "output")
    @JacksonXmlProperty(localName = "arc")
    private final List<Arc> outputArcs = new LinkedList<>();

    public Actor(Actor actor) {
        super(actor);
        outputArcs.addAll(actor.getOutputArcs().stream().map(Arc::new).collect(Collectors.toList()));
    }

    public void addOutputArc(Arc arc) {
        outputArcs.add(arc);
    }

    @JsonIgnore
    public List<Arc> getOutputArcs() {
        return new LinkedList<>(outputArcs);
    }

    @JsonIgnore
    public Collection<Arc> getArcs() {
        return getArcs(outputArcs);
    }

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Actor) {
            var otherActor = (Actor) other;
            if (Objects.equals(type, otherActor.getType())) {
                return isInputIdentical(otherActor)
                        && isOutputIdentical(otherActor);
            }
        }
        return false;
    }

    @Override
    protected boolean isOutputIdentical(BaseRelation relation) {
        if (!(relation instanceof Actor)) return false;
        var otherActor = (Actor) relation;
        return outputArcs.size() == otherActor.getOutputArcs().size()
                && IntStream.range(0, outputArcs.size())
                .allMatch(index -> outputArcs.get(index).isIdentical(otherActor.getOutputArcs().get(index),
                        owner.getOutermostGraph(), otherActor.getOwner().getOutermostGraph()));
    }
}
