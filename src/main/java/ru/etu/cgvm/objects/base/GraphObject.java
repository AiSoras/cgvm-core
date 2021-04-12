package ru.etu.cgvm.objects.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public abstract class GraphObject {

    @Getter
    @Setter
    protected ObjectID id = new ObjectID();
    @Getter
    protected final Kind kind;
    @Getter
    @Setter
    protected Graph owner;

    public enum Kind {
        ACTOR,
        RELATION,
        CONCEPT,
        GRAPH
    }

    protected GraphObject(Kind kind) {
        this.kind = kind;
    }

    public abstract String getStringRepresentation();
}
