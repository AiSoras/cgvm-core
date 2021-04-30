package ru.etu.cgvm.objects.base;

import lombok.*;

import java.util.UUID;

@EqualsAndHashCode
// Не переопределяется в наследниках, так как в нашем случае основное суждение об эквивалетности = по ID
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GraphObject {

    @Getter
    @Setter
    protected String id = UUID.randomUUID().toString();
    @Getter
    @Setter
    protected Graph owner;

    protected GraphObject(GraphObject graphObject) {
        id = graphObject.getId();
    }

    public abstract String getStringRepresentation();

    public abstract boolean isIdentical(GraphObject other);
}