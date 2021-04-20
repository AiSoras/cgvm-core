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
    private Context context; // В отношениях, в акторах используются только концепты

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

    public boolean isIdentical(Arc other, Graph owner) {
        if (other == null) return false;
        Optional<Concept> firstConcept = findConcept(owner);
        Optional<Concept> secondConcept = other.findConcept(owner);
        if (firstConcept.isPresent() && secondConcept.isPresent()) {
            return firstConcept.get().isIdentical(secondConcept.get());
        } else {
            return context != null && other.getContext() != null
                    && context.isIdentical(other.getContext());
        }
    }
}