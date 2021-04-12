package ru.etu.cgvm.objects.graphs;

import lombok.Getter;
import ru.etu.cgvm.objects.SignatureParameter;
import ru.etu.cgvm.objects.base.Graph;

import java.util.Collection;
import java.util.LinkedList;

public class Lambda extends Graph {

    @Getter
    private final Collection<SignatureParameter> signatureParameters = new LinkedList<>();

    public void addSignatureParameter(SignatureParameter parameter) {
        signatureParameters.add(parameter);
    }

    @Override
    public String getStringRepresentation() {
        return null;
    }
}