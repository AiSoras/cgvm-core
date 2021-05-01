package ru.etu.cgvm.objects.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.graphs.Lambda;
import ru.etu.cgvm.objects.nodes.Actor;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.objects.nodes.Relation;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Context.class, name = "context"),
        @JsonSubTypes.Type(value = Lambda.class, name = "lambda"),
        @JsonSubTypes.Type(value = Actor.class, name = "actor"),
        @JsonSubTypes.Type(value = Concept.class, name = "concept"),
        @JsonSubTypes.Type(value = Relation.class, name = "relation")
})

@EqualsAndHashCode
// Не переопределяется в наследниках, так как в нашем случае основное суждение об эквивалетности = по ID
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GraphObject {

    @Getter
    @Setter
    @JacksonXmlProperty(isAttribute = true)
    protected String id = UUID.randomUUID().toString();
    @Getter
    @Setter
    @JsonIgnore
    protected Graph owner;

    protected GraphObject(GraphObject graphObject) {
        id = graphObject.getId();
    }

    @JsonIgnore
    public abstract String getStringRepresentation();

    @JsonIgnore
    public abstract boolean isIdentical(GraphObject other);
}