package ru.etu.cgvm.objects;

import lombok.ToString;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.nodes.Concept;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@NoArgsConstructor
@ToString
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
