package ru.etu.cgvm.notations.cgif;

import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.base.Node;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.graphs.Lambda;
import ru.etu.cgvm.objects.nodes.Actor;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.objects.nodes.Relation;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.etu.cgvm.objects.Constant.*;

public class CgifGenerator {

    private static final Set<String> translatedIds = new HashSet<>();

    private CgifGenerator() {
    }

    public static String convert(Context topContext) {
        translatedIds.clear();
        return translateContext(topContext);
    }

    private static String translateContext(final Context context) {
        translatedIds.add(context.getId());
        var builder = new StringBuilder();
        if (context.isNegated()) {
            builder.append(TILDA);
        }
        if (!context.isOutermost()) {
            builder.append(LEFT_BRACKET);
        }
        builder.append(Optional.ofNullable(context.getName()).orElse(EMPTY));
        // Для корректного отображения вложенных концептов и контектов в отношения
        GraphObjectUtils.getNonNestedObjects(context, Relation.class).forEach(relation -> builder.append(translateGraphObject(relation)));
        // Далее - все оставшиеся объекты
        context.getObjects().forEach(object -> builder.append(translateGraphObject(object)));
        if (!context.isOutermost()) {
            builder.append(RIGHT_BRACKET);
        }
        return builder.toString();
    }

    private static String translateLambda(final Lambda lambda) {
        translatedIds.add(lambda.getId());
        var builder = new StringBuilder(LEFT_PARENTHESIS);
        builder.append(lambda.getStringRepresentation());
        // Для корректного отображения вложенных концептов и контектов в отношения
        GraphObjectUtils.getNonNestedObjects(lambda, Relation.class).forEach(relation -> builder.append(translateGraphObject(relation)));
        // Далее - все оставшиеся объекты
        lambda.getObjects().forEach(object -> builder.append(translateGraphObject(object)));
        builder.append(RIGHT_PARENTHESIS);
        return builder.toString();
    }

    private static String translateConcept(final Concept concept) {
        return LEFT_BRACKET
                + getIfTypePresented(concept, concept.getType().toString())
                + (concept.getCoreferenceLinks().isEmpty() ? EMPTY : (getIfTypePresented(concept, SPACE) + String.join(SPACE, concept.getCoreferenceLinks())))
                + (concept.getReferent() == null ? EMPTY : (": " + concept.getReferent()))
                + RIGHT_BRACKET;
    }

    private static String getIfTypePresented(Node node, String desiredStrings) {
        return node.getType() == null ? EMPTY : desiredStrings;
    }

    private static String translateRelation(final Relation relation) {
        return LEFT_PARENTHESIS
                + relation.getStringRepresentation()
                + SPACE + translateArc(relation.getInput())
                + SPACE + translateArc(relation.getOutput())
                + RIGHT_PARENTHESIS;
    }

    private static String translateActor(final Actor actor) {
        return LEFT_CHEVRON
                + actor.getStringRepresentation()
                + actor.getInputArcs().stream().map(CgifGenerator::translateArc).collect(Collectors.joining(SPACE, SPACE, SPACE))
                + BAR
                + actor.getOutputArcs().stream().map(CgifGenerator::translateArc).collect(Collectors.joining(SPACE, SPACE, SPACE))
                + RIGHT_CHEVRON;
    }

    private static String translateArc(final Arc arc) {
        if (arc.getCoreferenceLink() != null) {
            return QUESTION_MARK + arc.getCoreferenceLink();
        }
        return translateGraphObject(arc.getConcept() != null ? arc.getConcept() : arc.getContext());
    }

    private static String translateGraphObject(final GraphObject graphObject) {
        if (translatedIds.contains(graphObject.getId())) {
            return EMPTY;
        }
        translatedIds.add(graphObject.getId());
        if (graphObject instanceof Concept) {
            return translateConcept((Concept) graphObject);
        }
        if (graphObject instanceof Context) {
            return translateContext((Context) graphObject);
        }
        if (graphObject instanceof Relation) {
            return translateRelation((Relation) graphObject);
        }
        if (graphObject instanceof Actor) {
            return translateActor((Actor) graphObject);
        }
        if (graphObject instanceof Lambda) {
            return translateLambda((Lambda) graphObject);
        }
        throw new IllegalArgumentException("Translation error! Unsupported object class: " + graphObject.getClass().getSimpleName());
    }
}