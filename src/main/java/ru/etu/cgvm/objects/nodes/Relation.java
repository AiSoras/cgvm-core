package ru.etu.cgvm.objects.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.BaseRelation;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@ToString(callSuper = true)
@Setter
@Getter
@NoArgsConstructor
public class Relation extends BaseRelation {

    private Arc output;

    public Relation(Relation relation) {
        super(relation);
        output = new Arc(relation.getOutput());
    }

    @JsonIgnore
    public Collection<Arc> getArcs() {
        return getArcs(Collections.singleton(output));
    }

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Relation
                && Objects.equals(type, ((Node) other).getType())) {
            var otherRelation = (Relation) other;
            return isInputIdentical(otherRelation)
                    && isOutputIdentical(otherRelation);
        }
        return false;
    }

    @Override
    protected boolean isOutputIdentical(BaseRelation relation) {
        if (!(relation instanceof Relation)) return false;
        var otherRelation = (Relation) relation;
        return output.isIdentical(otherRelation.getOutput(), owner.getOutermostGraph(), otherRelation.getOwner().getOutermostGraph());
    }
}