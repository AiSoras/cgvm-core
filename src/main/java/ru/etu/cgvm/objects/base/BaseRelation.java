package ru.etu.cgvm.objects.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.etu.cgvm.objects.Arc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ToString(callSuper = true)
@NoArgsConstructor
public abstract class BaseRelation extends Node {

    @JacksonXmlElementWrapper(localName = "input")
    @JacksonXmlProperty(localName = "arc")
    protected final List<Arc> inputArcs = new LinkedList<>();

    protected BaseRelation(BaseRelation relation) {
        super(relation);
        inputArcs.addAll(relation.getInputArcs().stream().map(Arc::new).collect(Collectors.toList()));
    }

    public void addInputArc(Arc arc) {
        inputArcs.add(arc);
    }


    public void addInputArcs(Collection<Arc> arcs) {
        inputArcs.addAll(arcs);
    }

    @JsonIgnore
    public List<Arc> getInputArcs() {
        return new LinkedList<>(inputArcs);
    }

    @JsonIgnore
    protected Collection<Arc> getArcs(Collection<Arc> outputArcs) {
        Collection<Arc> arcs = getInputArcs();
        arcs.addAll(outputArcs);
        return arcs;
    }

    protected boolean isInputIdentical(BaseRelation relation) {
        return inputArcs.size() == relation.getInputArcs().size()
                && IntStream.range(0, inputArcs.size())
                .allMatch(index -> inputArcs.get(index).isIdentical(relation.getInputArcs().get(index),
                        owner.getOutermostGraph(), relation.getOwner().getOutermostGraph()));
    }

    protected abstract boolean isOutputIdentical(BaseRelation relation);
}