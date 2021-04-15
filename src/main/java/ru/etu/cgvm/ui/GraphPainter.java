package ru.etu.cgvm.ui;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.etu.cgvm.objects.Arc;
import ru.etu.cgvm.objects.TypeHierarchy;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.base.GraphObject;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.objects.graphs.Lambda;
import ru.etu.cgvm.objects.nodes.Actor;
import ru.etu.cgvm.objects.nodes.Concept;
import ru.etu.cgvm.objects.nodes.Relation;
import ru.etu.cgvm.utils.GraphObjectUtils;
import ru.etu.cgvm.utils.SettingManager;

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
    private final Map<String, Object> graphObjects = new HashMap<>();

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
        Collection<GraphObject> conceptList = GraphObjectUtils.getShallowObjects(context, GraphObject.Kind.CONCEPT);
        conceptList.forEach(concept -> conceptObjects.put(concept.getId(), insertVertex(graphFrame, parent, concept.getStringRepresentation(), "concept")));
    }

    private void addActors(mxGraph graphFrame, Object parent, Graph context) {
        Collection<GraphObject> actorList = GraphObjectUtils.getShallowObjects(context, GraphObject.Kind.ACTOR);
        actorList.forEach(actor -> actorObjects.put(actor.getId(), insertVertex(graphFrame, parent, actor.getStringRepresentation(), "actor")));

        actorList.forEach(actorObject -> {
            Actor actor = (Actor) actorObject;
            Object actorVertex = actorObjects.get(actor.getId());

            Arc arc;
            List<Arc> inputArcs = actor.getInputArcs();
            for (int order = 1; order <= inputArcs.size(); order++) {
                arc = inputArcs.get(order - 1);
                insertArrow(graphFrame, parent, order, conceptObjects.get(arc.findConcept(context).get().getId()), actorVertex);
            }

            List<Arc> outputArcs = actor.getOutputArcs();
            for (int order = 1; order <= outputArcs.size(); order++) {
                arc = outputArcs.get(order - 1);
                insertArrow(graphFrame, parent, order, actorVertex, conceptObjects.get(arc.findConcept(context).get().getId()));
            }
        });
    }

    private void addRelations(mxGraph graphFrame, Object parent, Graph context, BiPredicate<Graph, Arc> additionalCheck) {
        Collection<GraphObject> relationList = GraphObjectUtils.getShallowObjects(context, GraphObject.Kind.RELATION);
        relationList.forEach(relation -> relationObjects.put(relation.getId(), insertVertex(graphFrame, parent, relation.getStringRepresentation(), "relation")));

        relationList.forEach(relationObject -> {
            Relation relation = (Relation) relationObject;
            Object relationVertex = relationObjects.get(relation.getId());
            Optional<Concept> desiredConcept;

            Arc arc = relation.getInput();
            if (additionalCheck.test(context, arc)) {
                desiredConcept = arc.findConcept(context);
                if (desiredConcept.isPresent()) {
                    insertArrow(graphFrame, parent, conceptObjects.get(desiredConcept.get().getId()), relationVertex);
                } else {
                    desiredConcept = arc.findConcept(context.getOutermostGraph());
                    if (desiredConcept.isPresent()) {
                        Object additionalConcept = insertVertex(graphFrame, parent, desiredConcept.get().getStringRepresentation(), "concept");
                        insertArrow(graphFrame, parent, additionalConcept, relationVertex);
                        insertCoreferenceLink(graphFrame, parent, conceptObjects.get(desiredConcept.get().getId()), additionalConcept);
                    } else {
                        insertArrow(graphFrame, parent, graphObjects.get(arc.getContext().getId()), relationVertex);
                    }
                }
            } else {
                Object additionalConcept = insertVertex(graphFrame, parent, arc.getCoreferenceLink(), "concept");
                insertArrow(graphFrame, parent, additionalConcept, relationVertex);
            }

            arc = relation.getOutput();
            if (additionalCheck.test(context, arc)) {
                desiredConcept = arc.findConcept(context);
                if (desiredConcept.isPresent()) {
                    insertArrow(graphFrame, parent, relationVertex, conceptObjects.get(desiredConcept.get().getId()));
                } else {
                    desiredConcept = arc.findConcept(context.getOutermostGraph());
                    if (desiredConcept.isPresent()) {
                        Object additionalConcept = insertVertex(graphFrame, parent, desiredConcept.get().getStringRepresentation(), "concept");
                        insertArrow(graphFrame, parent, relationVertex, additionalConcept);
                        insertCoreferenceLink(graphFrame, parent, conceptObjects.get(desiredConcept.get().getId()), additionalConcept);
                    } else {
                        insertArrow(graphFrame, parent, relationVertex, graphObjects.get(arc.getContext().getId()));
                    }
                }
            } else {
                Object additionalConcept = insertVertex(graphFrame, parent, arc.getCoreferenceLink(), "concept");
                insertArrow(graphFrame, parent, relationVertex, additionalConcept);
            }
        });
    }

    private void addTypeHierarchy(mxGraph graphFrame, Object parent, Graph context) {
        List<Triple<String, TypeHierarchy.Order, String>> typeOrders = context.getTypeHierarchy().getTypeOrders();
        List<Pair<String, Lambda>> typeDefinitions = context.getTypeHierarchy().getTypeDefinitions();

        typeOrders.forEach(typeOrder -> {
            Object order = insertVertex(graphFrame, parent, typeOrder.getMiddle().name(), "relation");
            Object from = insertVertex(graphFrame, parent, typeOrder.getLeft(), "concept");
            Object to = insertVertex(graphFrame, parent, typeOrder.getRight(), "concept");
            insertArrow(graphFrame, parent, from, order);
            insertArrow(graphFrame, parent, order, to);
        });

        typeDefinitions.forEach(typeDefinition -> {
            Object type = insertVertex(graphFrame, parent, typeDefinition.getKey(), "concept");
            Object def = insertVertex(graphFrame, parent, TypeHierarchy.DEF, "relation");
            Object lambda = insertVertex(graphFrame, parent, typeDefinition.getValue().getStringRepresentation(), "concept");
            graphObjects.put(typeDefinition.getValue().getId(), lambda);
            insertArrow(graphFrame, parent, type, def);
            insertArrow(graphFrame, parent, def, lambda);
            addContextObjects(graphFrame, lambda, typeDefinition.getValue(), (graph, arc) -> !typeDefinition.getValue().isSignatureParameter(arc.getCoreferenceLink()));
        });
    }

    private Object insertVertex(mxGraph graphFrame, Object parent, String label, String styleName) {
        return graphFrame.insertVertex(parent, null, label, 20, 20, 80, 30, styleName);
    }

    private Object insertArrow(mxGraph graphFrame, Object parent, Object label, Object from, Object to) {
        return graphFrame.insertEdge(parent, null, label, from, to, "arrow");
    }

    private Object insertArrow(mxGraph graphFrame, Object parent, Object from, Object to) {
        return graphFrame.insertEdge(parent, null, "", from, to, "arrow");
    }

    private Object insertCoreferenceLink(mxGraph graphFrame, Object parent, Object from, Object to) {
        return graphFrame.insertEdge(parent, null, "", from, to, "coreferenceLink");
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
        graphObjects.put(outermostGraph.getId(), graphFrame.getDefaultParent());

        Collection<Context> nestedContext = outermostGraph.getNestedContexts();
        nestedContext.forEach(context ->
                graphObjects.put(context.getId(),
                        insertVertex(graphFrame, getParentObject(graphFrame, context), context.getStringRepresentation(), "context"))
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
                context.getOwner() != null
                        ? graphObjects.get(context.getOwner().getId())
                        : graphFrame.getDefaultParent())
                .orElseThrow(() -> new IllegalStateException("No object found for context: " + context));
    }

    public mxGraphComponent drawGraph(Graph conceptualGraph) {
        mxGraph graphFrame = initGraphFrame();
        graphFrame.getModel().beginUpdate();
        addContexts(graphFrame, conceptualGraph);
        graphFrame.getModel().endUpdate();
        mxGraphComponent graphComponent = new mxGraphComponent(graphFrame);
        configureGraphComponent(graphComponent);
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graphFrame);
        graphObjects.values().forEach(layout::execute);
        return graphComponent;
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
        graph.setMinimumGraphSize(new mxRectangle(0, 0,
                Double.parseDouble(SettingManager.getProperty("viewer.area.width")),
                Double.parseDouble(SettingManager.getProperty("viewer.area.height"))
        ));
    }

    private void configureGraphComponent(mxGraphComponent graphComponent) {
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);
        graphComponent.setAutoScroll(false);
        graphComponent.setCenterZoom(false);
        graphComponent.setConnectable(false);
    }
}