package ru.etu.cgvm.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.nodes.Concept;

import java.util.Optional;

@Getter
@NoArgsConstructor
@ToString
public class Arc {

    // Взаимоисключающие поля
    @Setter
    private Concept concept;
    private String coreferenceLink;
    @Setter
    private Context context; // В отношениях. В акторах используются только концепты

    public Arc(Arc arc) {
        if (arc.getConcept() != null) {
            concept = new Concept(arc.getConcept());
        } else if (arc.getContext() != null) {
            context = new Context(arc.getContext());
        } else {
            coreferenceLink = arc.getCoreferenceLink();
        }
    }

    public void setCoreferenceLink(String coreferenceLink) {
        if (Optional.ofNullable(coreferenceLink).isPresent()) {
            this.coreferenceLink = coreferenceLink.replace("?", ""); //Всегда связанные метки
        }
    }

    public Optional<Concept> findConcept(Graph owner) { // Ищет по всему графу, включая внешние контексты
        if (concept != null) {
            return Optional.of(concept);
        } else {
            return owner == null
                    ? Optional.empty()
                    : owner.getOutermostGraph().getConceptByCoreferenceLink(coreferenceLink);
        }
    }

    public boolean isIdentical(Arc other, Graph firstOwner, Graph secondOwner) {
        if (other == null) return false;
        Optional<Concept> firstConcept = findConcept(firstOwner);
        Optional<Concept> secondConcept = other.findConcept(secondOwner);
        if (firstConcept.isPresent() && secondConcept.isPresent()) {
            return firstConcept.get().isIdentical(secondConcept.get());
        } else {
            return context != null && other.getContext() != null
                    && context.isIdentical(other.getContext());
        }
    }
}