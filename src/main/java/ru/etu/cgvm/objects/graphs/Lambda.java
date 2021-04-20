package ru.etu.cgvm.objects.graphs;

import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import ru.etu.cgvm.objects.SignatureParameter;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static ru.etu.cgvm.objects.Constant.*;

@ToString(callSuper = true)
public class Lambda extends Graph {

    public Lambda() {
    }

    private final Collection<SignatureParameter> signatureParameters = new LinkedList<>();

    public Collection<SignatureParameter> getSignatureParameters() {
        return new LinkedList<>(signatureParameters);
    }

    public void addSignatureParameter(SignatureParameter parameter) {
        signatureParameters.add(parameter);
    }

    @Override
    public String getStringRepresentation() {
        return LAMBDA + SPACE + signatureParameters.stream()
                .map(SignatureParameter::getStringRepresentation)
                .collect(Collectors.joining(", ", LEFT_PARENTHESIS, RIGHT_PARENTHESIS));
    }

    @Override
    public boolean isIdentical(GraphObject other) {
        if (other == null) return false;
        if (other instanceof Lambda) {
            Lambda otherLambda = (Lambda) other;
            return signatureParameters.size() == otherLambda.getSignatureParameters().size()
                    && signatureParameters.stream().allMatch(
                    signatureParameter ->
                            otherLambda.getSignatureParameters().stream()
                                    .anyMatch(signatureParameter::isIdentical)
            ); // Проверяем только сигнатуру, не залезая внутрь
        }
        return false;
    }

    public boolean isSignatureParameter(String coreferenceLink) {
        return signatureParameters.stream()
                .anyMatch(signatureParameter -> StringUtils.equalsIgnoreCase(signatureParameter.getVariable(), coreferenceLink));
    }
}