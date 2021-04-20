package ru.etu.cgvm.objects.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode
// Не переопределяется в наследниках, так как в нашем случае основное суждение об эквивалетности = по ID
@ToString
public abstract class GraphObject {

    @Getter
    @Setter
    protected String id = UUID.randomUUID().toString();
    @Getter
    @Setter
    protected Graph owner;

    public abstract String getStringRepresentation();

    public abstract boolean isIdentical(GraphObject other);
}