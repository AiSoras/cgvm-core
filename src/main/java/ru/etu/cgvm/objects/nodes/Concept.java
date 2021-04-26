package ru.etu.cgvm.objects.nodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.Referent;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

import static ru.etu.cgvm.objects.Constant.EMPTY;

@ToString(callSuper = true)
@NoArgsConstructor
public class Concept extends Node {

    @Setter
    @Getter
    private Referent referent;
    @Getter
    private final Collection<String> coreferenceLinks = new LinkedList<>();

    public Concept(Concept concept) {
        super(concept);
        referent = Optional.ofNullable(concept.getReferent()).map(Referent::new).orElse(null);
        coreferenceLinks.addAll(concept.getCoreferenceLinks());
    }

    public void addCoreferenceLink(String coreferenceLink) {
        if (Optional.ofNullable(coreferenceLink).isPresent()) {
            coreferenceLinks.add(coreferenceLink); // Сохраняем ?/*
        }
    }

    public boolean isAny() {
        return Optional.ofNullable(referent).orElse(new Referent()).isAny();
    }

    @Override
    public String getStringRepresentation() {
        return super.getStringRepresentation() + (referent == null ? EMPTY : (": " + referent));
    }

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Concept) {
            var otherConcept = (Concept) other;
            return Objects.equals(type, otherConcept.getType()) // Проверяем тип концептов
                    && ((isAny() || otherConcept.isAny()) // Если {*}/<*>, то не смотрим на референт
                    || Objects.equals(referent, otherConcept.getReferent()));
        }
        return false;
    }
}