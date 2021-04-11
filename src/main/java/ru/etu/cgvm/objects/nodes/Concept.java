package ru.etu.cgvm.objects.nodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.etu.cgvm.objects.Referent;
import ru.etu.cgvm.objects.base.Node;

import java.util.*;

@NoArgsConstructor
public class Concept extends Node {

    @Setter
    @Getter
    private Referent referent = new Referent();
    @Getter
    private final Collection<String> coreferenceLinks = new LinkedList<>();

    public Concept(Kind kind) {
        super(kind);
    }

    public void addCoreferenceLink(String coreferenceLink) {
        if (Optional.ofNullable(coreferenceLink).isPresent()) {
            coreferenceLinks.add(coreferenceLink.replaceFirst("^[*?]", ""));
        }
    }
}