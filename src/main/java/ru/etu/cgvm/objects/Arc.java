package ru.etu.cgvm.objects;

import ru.etu.cgvm.objects.nodes.Concept;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.etu.cgvm.objects.nodes.Graph;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class Arc {

    @Setter
    private Concept concept;
    @Setter
    private Graph context;
    private String coreferenceLink;

    public void setCoreferenceLink(String coreferenceLink) {
        if (Optional.ofNullable(coreferenceLink).isPresent()) {
            this.coreferenceLink = coreferenceLink.replaceFirst("^[*?]", "");
        }
    }
}
