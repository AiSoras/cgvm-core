package ru.etu.cgvm.objects.graphs;

import lombok.Getter;
import lombok.ToString;
import ru.etu.cgvm.objects.SignatureParameter;
import ru.etu.cgvm.objects.base.Graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

@ToString(callSuper = true)
public class Lambda extends Graph {

    public Lambda() {
        super(Kind.LAMBDA);
    }

    @Getter
    private final Collection<SignatureParameter> signatureParameters = new LinkedList<>();

    public void addSignatureParameter(SignatureParameter parameter) {
        signatureParameters.add(parameter);
    }

    @Override
    public String getStringRepresentation() {
        return "lambda " + signatureParameters.stream()
                .map(signatureParameter -> signatureParameter.getType().getName() + " " + signatureParameter.getVariable())
                .collect(Collectors.joining(", ", "(", ")"));
    }

    public boolean isSignatureParameter(String coreferenceLink) {
        return signatureParameters.stream().anyMatch(signatureParameter -> signatureParameter.getVariable().equalsIgnoreCase(coreferenceLink));
    }
}