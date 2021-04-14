package ru.etu.cgvm.objects.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode
@ToString
public abstract class GraphObject {

    @Getter
    @Setter
    protected String id = UUID.randomUUID().toString();
    @Getter
    protected final Kind kind;
    @Getter
    @Setter
    protected Graph owner;

    public enum Kind {
        ACTOR,
        RELATION,
        CONCEPT,
        GRAPH,
        CONTEXT,
        LAMBDA,
    }

    protected GraphObject(Kind kind) {
        this.kind = kind;
    }

    public abstract String getStringRepresentation();
}
