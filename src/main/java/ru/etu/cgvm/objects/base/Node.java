package ru.etu.cgvm.objects.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.Type;

import java.util.Optional;

@ToString
public abstract class Node extends GraphObject {

    @Getter
    @Setter
    protected Type type;

    protected Node(Kind kind) {
        super(kind);
    }

    @Override
    public String getStringRepresentation() {
        return Optional.ofNullable(type).orElse(new Type()).getName();
    }
}