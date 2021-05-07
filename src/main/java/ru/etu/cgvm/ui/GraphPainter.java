package ru.etu.cgvm.ui;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.Constant;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.graphs.Lambda;
import ru.etu.cgvm.objects.nodes.Actor;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.objects.nodes.Relation;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiPredicate;

public class GraphPainter {

    private static final Map<String, Object> ARROW_STYLE = Map.of(
            mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));

    private static final Map<String, Object> CONCEPT_STYLE = Map.of(
            mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE),
            mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK),
            mxConstants.STYLE_STROKEWIDTH, 1.5,
            mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE,
            mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);

    private static final Map<String, Object> CONTEXT_STYLE = Map.of(
            mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE),
            mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK),
            mxConstants.STYLE_STROKEWIDTH, 1.5,
            //mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT,
            //mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP,
            mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE,
            mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RECTANGLE);

    private static final Map<String, Object> RELATION_STYLE = Map.of(
            mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE),
            mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK),
            mxConstants.STYLE_STROKEWIDTH, 1.5,
            mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE,
            mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);

    private static final Map<String, Object> ACTOR_STYLE = Map.of(
            mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE),
            mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK),
            mxConstants.STYLE_STROKEWIDTH, 1.5,
            mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RHOMBUS,
            mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RHOMBUS);

    private static final Map<String, Object> COREFERENCE_LINK_STYLE = Map.of(
            mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK),
            mxConstants.STYLE_ENDARROW, mxConstants.SHAPE_LINE,
            mxConstants.STYLE_DASHED, true);

    private final Map<String, Object> conceptObjects = new HashMap<>();
    private final Map<String, Object> relationObjects = new HashMap<>();
    private final Map<String, Object> actorObjects = new HashMap<>();
    private final Map<String, Object> graphObjects = new LinkedHashMap<>();

    private void addContextObjects(mxGraph graphFrame, Object parent, Graph context) {
        addConcepts(graphFrame, parent, context);
        addRelations(graphFrame, parent, context, (graph, arc) -> true);
        addActors(graphFrame, parent, context);
    }

    private void addContextObjects(mxGraph graphFrame, Object parent, Graph context, BiPredicate<Graph, Arc> additionalCheck) {
        addConcepts(graphFrame, parent, context);
        addRelations(graphFrame, parent, context, additionalCheck);
        addActors(graphFrame, parent, context);
    }

    private void addConcepts(mxGraph graphFrame, Object parent, Graph context) {
        Collection<Concept> conceptList = GraphObjectUtils.getNonNestedObjects(context, Concept.class);
        conceptList.forEach(concept -> conceptObjects.put(concept.getId(), insertVertex(graphFrame, parent, concept.getStringRepresentation(), Style.CONCEPT.name())));
    }

    private void addActors(mxGraph graphFrame, Object parent, Graph context) {
        Collection<Actor> actorList = GraphObjectUtils.getNonNestedObjects(context, Actor.class);
        actorList.forEach(actor -> actorObjects.put(actor.getId(), insertVertex(graphFrame, parent, actor.getStringRepresentation(), Style.ACTOR.name())));

        actorList.forEach(actor -> {
            var actorVertex = actorObjects.get(actor.getId());

            Arc arc;
            List<Arc> inputArcs = actor.getInputArcs();
            for (var order = 1; order <= inputArcs.size(); order++) {
                arc = inputArcs.get(order - 1);
                insertArrow(graphFrame, parent, order, conceptObjects.get(arc.findConcept(context).get().getId()), actorVertex);
            }

            List<Arc> outputArcs = actor.getOutputArcs();
            for (var order = 1; order <= outputArcs.size(); order++) {
                arc = outputArcs.get(order - 1);
                insertArrow(graphFrame, parent, order, actorVertex, conceptObjects.get(arc.findConcept(context).get().getId()));
            }
        });
    }

    // TODO: refactor
    private void addRelations(mxGraph graphFrame, Object parent, Graph context, BiPredicate<Graph, Arc> additionalCheck) {
        Collection<Relation> relationList = GraphObjectUtils.getNonNestedObjects(context, Relation.class);
        relationList.forEach(relation -> relationObjects.put(relation.getId(), insertVertex(graphFrame, parent, relation.getStringRepresentation(), Style.RELATION.name())));

        relationList.forEach(relation -> {
            var relationVertex = relationObjects.get(relation.getId());
            Optional<Concept> desiredConcept;

            Arc arc;
            List<Arc> inputArcs = relation.getInputArcs();
            for (var order = 1; order <= inputArcs.size(); order++) {
                arc = inputArcs.get(order - 1);
                if (additionalCheck.test(context, arc)) {
                    desiredConcept = arc.findConcept(context);
                    if (desiredConcept.isPresent()) {
                        if (Objects.equals(desiredConcept.get().getOwner(), relation.getOwner())) { // То есть лежат в одном контексте
                            insertArrow(graphFrame, parent, conceptObjects.get(desiredConcept.get().getId()), relationVertex);
                        } else {
                            Object additionalConcept = insertVertex(graphFrame, parent, desiredConcept.get().getStringRepresentation(), Style.CONCEPT.name());
                            insertArrow(graphFrame, parent, additionalConcept, relationVertex);
                            insertCoreferenceLink(graphFrame, parent, conceptObjects.get(desiredConcept.get().getId()), additionalConcept);
                        }
                    } else {
                        insertArrow(graphFrame, parent, graphObjects.get(arc.getContext().getId()), relationVertex);

                    }
                } else {
                    Object additionalConcept = insertVertex(graphFrame, parent, arc.getCoreferenceLink(), Style.CONCEPT.name());
                    insertArrow(graphFrame, parent, additionalConcept, relationVertex);
                }
            }

            arc = relation.getOutput();
            if (additionalCheck.test(context, arc)) {
                desiredConcept = arc.findConcept(context);
                if (desiredConcept.isPresent()) {
                    if (Objects.equals(desiredConcept.get().getOwner(), relation.getOwner())) {
                        insertArrow(graphFrame, parent, relationVertex, conceptObjects.get(desiredConcept.get().getId()));
                    } else {
                        Object additionalConcept = insertVertex(graphFrame, parent, desiredConcept.get().getStringRepresentation(), Style.CONCEPT.name());
                        insertArrow(graphFrame, parent, relationVertex, additionalConcept);
                        insertCoreferenceLink(graphFrame, parent, conceptObjects.get(desiredConcept.get().getId()), additionalConcept);
                    }
                } else {
                    insertArrow(graphFrame, parent, relationVertex, graphObjects.get(arc.getContext().getId()));
                }
            } else {
                Object additionalConcept = insertVertex(graphFrame, parent, arc.getCoreferenceLink(), Style.CONCEPT.name());
                insertArrow(graphFrame, parent, relationVertex, additionalConcept);
            }
        });
    }

    private void addTypeHierarchy(mxGraph graphFrame, Object parent, Graph context) {
        List<Triple<String, TypeHierarchy.Order, String>> typeOrders = context.getTypeHierarchy().getTypeOrders();
        List<Pair<String, Lambda>> typeDefinitions = context.getTypeHierarchy().getTypeDefinitions();

        if (!typeDefinitions.isEmpty() || !typeOrders.isEmpty()) {
            Object typeHierarchyContext = insertVertex(graphFrame, parent, "TypeHierarchy", Style.CONTEXT.name());

            typeOrders.forEach(typeOrder -> {
                Object order = insertVertex(graphFrame, typeHierarchyContext, typeOrder.getMiddle().name(), Style.RELATION.name());
                Object from = insertVertex(graphFrame, typeHierarchyContext, typeOrder.getLeft(), Style.CONCEPT.name());
                Object to = insertVertex(graphFrame, typeHierarchyContext, typeOrder.getRight(), Style.CONCEPT.name());
                insertArrow(graphFrame, typeHierarchyContext, from, order);
                insertArrow(graphFrame, typeHierarchyContext, order, to);
            });

            typeDefinitions.forEach(typeDefinition -> {
                Object type = insertVertex(graphFrame, typeHierarchyContext, typeDefinition.getKey(), Style.CONCEPT.name());
                Object def = insertVertex(graphFrame, typeHierarchyContext, Constant.DEF, Style.RELATION.name());
                Object lambda = insertVertex(graphFrame, typeHierarchyContext, typeDefinition.getValue().getStringRepresentation(), Style.CONCEPT.name());
                graphObjects.put(typeDefinition.getValue().getId(), lambda);
                insertArrow(graphFrame, typeHierarchyContext, type, def);
                insertArrow(graphFrame, typeHierarchyContext, def, lambda);
                addContextObjects(graphFrame, lambda, typeDefinition.getValue(), (graph, arc) -> !typeDefinition.getValue().isSignatureParameter(arc.getCoreferenceLink()));
            });
        }
    }

    private Object insertVertex(mxGraph graphFrame, Object parent, String label, String styleName) {
        return graphFrame.insertVertex(parent, null, label, 20, 20, 80, 30, styleName);
    }

    private Object insertArrow(mxGraph graphFrame, Object parent, Object label, Object from, Object to) {
        return graphFrame.insertEdge(parent, null, label, from, to, Style.ARROW.name());
    }

    private Object insertArrow(mxGraph graphFrame, Object parent, Object from, Object to) {
        return graphFrame.insertEdge(parent, null, "", from, to, Style.ARROW.name());
    }

    private Object insertCoreferenceLink(mxGraph graphFrame, Object parent, Object from, Object to) {
        return graphFrame.insertEdge(parent, null, "", from, to, Style.COREFERENCE_LINK.name());
    }

    private mxGraph initGraphFrame() {
        var graph = new mxGraph();
        configureGraph(graph);
        mxStylesheet stylesheet = graph.getStylesheet();
        stylesheet.putCellStyle(Style.RELATION.name(), RELATION_STYLE);
        stylesheet.putCellStyle(Style.CONTEXT.name(), CONTEXT_STYLE);
        stylesheet.putCellStyle(Style.CONCEPT.name(), CONCEPT_STYLE);
        stylesheet.putCellStyle(Style.ARROW.name(), ARROW_STYLE);
        stylesheet.putCellStyle(Style.ACTOR.name(), ACTOR_STYLE);
        stylesheet.putCellStyle(Style.COREFERENCE_LINK.name(), COREFERENCE_LINK_STYLE);
        return graph;
    }

    private void addContexts(mxGraph graphFrame, Graph outermostGraph) {
        graphObjects.put(outermostGraph.getId(), graphFrame.getDefaultParent());

        Collection<Context> nestedContext = GraphObjectUtils.getAllObjects(outermostGraph, Context.class);
        nestedContext.forEach(context ->
                graphObjects.put(context.getId(),
                        insertVertex(graphFrame, getParentObject(graphFrame, context), context.getStringRepresentation(), Style.CONTEXT.name()))
        );

        Collection<Context> contextList = new LinkedList<>(Collections.singletonList((Context) outermostGraph));
        contextList.addAll(nestedContext);
        contextList.forEach(context -> {
            addContextObjects(graphFrame, graphObjects.get(context.getId()), context);
            addTypeHierarchy(graphFrame, graphObjects.get(context.getId()), context);
        });
    }

    private Object getParentObject(mxGraph graphFrame, Context context) {
        return Optional.ofNullable(
                !context.isOutermost()
                        ? graphObjects.get(context.getOwner().getId())
                        : graphFrame.getDefaultParent())
                .orElseThrow(() -> new IllegalStateException("No object found for context: " + context));
    }

    public mxGraphComponent drawGraph(Graph conceptualGraph) {
        mxGraph graphFrame = initGraphFrame();

        graphFrame.getModel().beginUpdate();
        addContexts(graphFrame, conceptualGraph);
        graphFrame.getModel().endUpdate();
        optimizeObjectsLayout(graphFrame);

        var graphComponent = new mxGraphComponent(graphFrame);
        configureGraphComponent(graphComponent);
        return graphComponent;
    }

    private void optimizeObjectsLayout(mxGraph graphFrame) {
        var layout = new mxCompactTreeLayout(graphFrame);
        List<Map.Entry<String, Object>> entries = new LinkedList<>(graphObjects.entrySet());
        Collections.reverse(entries);
        entries.stream().map(Map.Entry::getValue).forEach(layout::execute);
    }

    private void configureGraph(mxGraph graph) {
        graph.setEnabled(false);
        graph.setCellsResizable(true);
        graph.setConstrainChildren(true);
        graph.setExtendParents(true);
        graph.setExtendParentsOnAdd(true);
        graph.setDefaultOverlap(0);
        graph.setAutoOrigin(true);
        graph.setAutoSizeCells(true);
    }

    private void configureGraphComponent(mxGraphComponent graphComponent) {
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);
        graphComponent.setAutoScroll(false);
        graphComponent.setCenterZoom(false);
        graphComponent.setConnectable(false);
    }

    private enum Style {
        RELATION,
        CONTEXT,
        CONCEPT,
        ARROW,
        ACTOR,
        COREFERENCE_LINK
    }
}