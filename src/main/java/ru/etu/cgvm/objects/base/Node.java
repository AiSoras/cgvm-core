package ru.etu.cgvm.objects.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.Type;

import java.util.Optional;

@ToString(callSuper = true)
@NoArgsConstructor
public abstract class Node extends GraphObject {

    @Getter
    @Setter
    protected Type type;

    protected Node(Node node) {
        type = Optional.ofNullable(node.getType()).map(Type::new).orElse(null);
    }

    @Override
    public String getStringRepresentation() {
        return Optional.ofNullable(type).orElse(new Type()).toString();
    }
}