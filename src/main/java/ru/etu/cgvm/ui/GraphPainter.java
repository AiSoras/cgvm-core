package ru.etu.cgvm.ui;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import javafx.embed.swing.SwingNode;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.nodes.Actor;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.objects.nodes.Relation;
import ru.etu.cgvm.utils.GraphObjectUtils;

import java.awt.*;
import java.util.List;
import java.util.*;

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
            mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT,
            mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP,
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
    private final Map<String, Object> contextObjects = new HashMap<>();

    private void addContextObjects(mxGraph graphFrame, Object parent, Graph context) {
        Collection<GraphObject> conceptList = GraphObjectUtils.getShallowObjects(context, GraphObject.Kind.CONCEPT);
        Collection<GraphObject> relationList = GraphObjectUtils.getShallowObjects(context, GraphObject.Kind.RELATION);
        Collection<GraphObject> actorList = GraphObjectUtils.getShallowObjects(context, GraphObject.Kind.ACTOR);

        conceptList.forEach(concept -> conceptObjects.put(concept.getId(), graphFrame.insertVertex(parent, null, concept.getStringRepresentation(), 20, 20, 80, 30, "concept")));
        relationList.forEach(relation -> relationObjects.put(relation.getId(), graphFrame.insertVertex(parent, null, relation.getStringRepresentation(), 20, 20, 80, 30, "relation")));
        actorList.forEach(actor -> actorObjects.put(actor.getId(), graphFrame.insertVertex(parent, null, actor.getStringRepresentation(), 20, 20, 80, 30, "actor")));

        relationList.forEach(relationObject -> {
            Relation relation = (Relation) relationObject;
            Object relationVertex = relationObjects.get(relation.getId());

            Optional<Concept> desiredConcept = relation.getInput().findConcept(context);

            if (desiredConcept.isPresent()) {
                graphFrame.insertEdge(parent, null, "", conceptObjects.get(desiredConcept.get().getId()), relationVertex, "arrow");
            } else {
                desiredConcept = relation.getInput().findConcept(context.getOutermostGraph());
                if (desiredConcept.isPresent()) {
                    Object additionalConcept = graphFrame.insertVertex(parent, null, desiredConcept.get().getStringRepresentation(), 20, 20, 80, 30, "concept");
                    graphFrame.insertEdge(parent, null, "", additionalConcept, relationVertex, "arrow");
                    graphFrame.insertEdge(parent, null, "", conceptObjects.get(desiredConcept.get().getId()), additionalConcept, "coreferenceLink");
                } else {
                    graphFrame.insertEdge(parent, null, "", contextObjects.get(relation.getInput().getContext().getId()), relationVertex, "arrow");
                }
            }

            desiredConcept = relation.getOutput().findConcept(context);
            if (desiredConcept.isPresent()) {
                graphFrame.insertEdge(parent, null, "", relationVertex, conceptObjects.get(desiredConcept.get().getId()), "arrow");
            } else {
                desiredConcept = relation.getOutput().findConcept(context.getOutermostGraph());
                if (desiredConcept.isPresent()) {
                    Object additionalConcept = graphFrame.insertVertex(parent, null, desiredConcept.get().getStringRepresentation(), 20, 20, 80, 30, "concept");
                    graphFrame.insertEdge(parent, null, "", relationVertex, additionalConcept, "arrow");
                    graphFrame.insertEdge(parent, null, "", conceptObjects.get(desiredConcept.get().getId()), additionalConcept, "coreferenceLink");
                } else {
                    graphFrame.insertEdge(parent, null, "", relationVertex, contextObjects.get(relation.getOutput().getContext().getId()), "arrow");
                }
            }
        });

        actorList.forEach(actorObject -> {
            Actor actor = (Actor) actorObject;
            Object actorVertex = actorObjects.get(actor.getId());

            Arc arc;
            java.util.List<Arc> inputArcs = actor.getInputArcs();
            for (int order = 1; order <= inputArcs.size(); order++) {
                arc = inputArcs.get(order - 1);
                graphFrame.insertEdge(parent, null, order, conceptObjects.get(arc.findConcept(context).get().getId()), actorVertex, "arrow");
            }

            List<Arc> outputArcs = actor.getOutputArcs();
            for (int order = 1; order <= outputArcs.size(); order++) {
                arc = outputArcs.get(order - 1);
                graphFrame.insertEdge(parent, null, order, actorVertex, conceptObjects.get(arc.findConcept(context).get().getId()), "arrow");
            }
        });
    }

    private mxGraph initGraphFrame() {
        mxGraph graph = new mxGraph();
        configureGraph(graph);
        mxStylesheet stylesheet = graph.getStylesheet();
        stylesheet.putCellStyle("relation", RELATION_STYLE);
        stylesheet.putCellStyle("context", CONTEXT_STYLE);
        stylesheet.putCellStyle("concept", CONCEPT_STYLE);
        stylesheet.putCellStyle("arrow", ARROW_STYLE);
        stylesheet.putCellStyle("actor", ACTOR_STYLE);
        stylesheet.putCellStyle("coreferenceLink", COREFERENCE_LINK_STYLE);
        return graph;
    }

    private void addContexts(mxGraph graphFrame, Graph outermostGraph) {
        contextObjects.put(outermostGraph.getId(), graphFrame.getDefaultParent());

        Collection<Context> nestedContext = outermostGraph.getNestedContexts();
        nestedContext.forEach(context ->
                contextObjects.put(context.getId(),
                        graphFrame.insertVertex(getParentObject(graphFrame, context), null, context.getStringRepresentation(), 20, 20, 80, 30, "context"))
        );

        Collection<Context> contextList = new LinkedList<>(Collections.singletonList((Context) outermostGraph));
        contextList.addAll(nestedContext);
        contextList.forEach(context -> addContextObjects(graphFrame, contextObjects.get(context.getId()), context));
    }

    private Object getParentObject(mxGraph graphFrame, Context context) {
        return Optional.ofNullable(
                context.getOwner() != null
                        ? contextObjects.get(context.getOwner().getId())
                        : graphFrame.getDefaultParent())
                .orElseThrow(() -> new IllegalStateException("No object found for context: " + context));
    }

    public void drawGraph(Graph conceptualGraph, SwingNode swingNode) {
        mxGraph graphFrame = initGraphFrame();
        graphFrame.getModel().beginUpdate();
        addContexts(graphFrame, conceptualGraph);
        graphFrame.getModel().endUpdate();
        mxGraphComponent graphComponent = new mxGraphComponent(graphFrame);
        configureGraphComponent(graphComponent);
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graphFrame);
        contextObjects.values().forEach(layout::execute);
        swingNode.setContent(graphComponent);
    }

    private void configureGraph(mxGraph graph) {
        graph.setEnabled(false);
        graph.setCellsResizable(true);
        graph.setConstrainChildren(true);
        graph.setExtendParents(true);
        graph.setExtendParentsOnAdd(true);
        graph.setDefaultOverlap(0);
    }

    private void configureGraphComponent(mxGraphComponent graphComponent) {
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);
        graphComponent.setAutoScroll(true);
        graphComponent.setConnectable(false);
    }
}