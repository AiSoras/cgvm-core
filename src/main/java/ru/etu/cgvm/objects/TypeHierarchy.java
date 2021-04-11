package ru.etu.cgvm.objects;

import ru.etu.cgvm.objects.nodes.Graph;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
public class TypeHierarchy {

    public enum Order {
        EQ,
        GT,
        LT
    }

    private final List<Triple<String, Order, String>> typeOrders = new LinkedList<>();
    private final List<Pair<String, Graph>> typeDefinitions = new LinkedList<>();

    public void addTypeOrder(String firstType, String secondType, String order) {
        typeOrders.add(new ImmutableTriple<>(firstType, Order.valueOf(order), secondType));
    }

    public void addTypeDefinition(String firstType, Graph typeDefinition) {
        typeDefinitions.add(new ImmutablePair<>(firstType, typeDefinition));
    }

    public List<Triple<String, Order, String>> getTypeOrders() {
        return new LinkedList<>(typeOrders);
    }

    public List<Pair<String, Graph>> getTypeDefinitions() {
        return new LinkedList<>(typeDefinitions);
    }
}