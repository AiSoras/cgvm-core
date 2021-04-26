package ru.etu.cgvm.objects.graphs;

import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Lambda extends Graph {

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
            var otherLambda = (Lambda) other;
            if (signatureParameters.size() == otherLambda.getSignatureParameters().size()
                    && signatureParameters.stream().allMatch(
                    signatureParameter ->
                            otherLambda.getSignatureParameters().stream()
                                    .anyMatch(signatureParameter::isIdentical))) {
                if (getObjects().size() < otherLambda.getObjects().size()) {
                    return getObjects().stream().allMatch(thisObject -> otherLambda.getObjects().stream().anyMatch(thisObject::isIdentical));
                } else {
                    return otherLambda.getObjects().stream().allMatch(otherObject -> getObjects().stream().anyMatch(otherObject::isIdentical));
                }
            }
        }
        return false;
    }

    public boolean isSignatureParameter(String coreferenceLink) {
        return signatureParameters.stream()
                .anyMatch(signatureParameter -> StringUtils.equalsIgnoreCase(signatureParameter.getVariable(), coreferenceLink));
    }
}