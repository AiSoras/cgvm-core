package ru.etu.cgvm.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.etu.cgvm.objects.graphs.Lambda;

import java.util.Collection;
import java.util.LinkedList;

@ToString
@NoArgsConstructor
public class TypeHierarchy {

    public enum Order {
        EQ,
        GT,
        LT
    }

    @JacksonXmlElementWrapper(localName = "typeOrders")
    @JacksonXmlProperty(localName = "order")
    private final Collection<Triple<String, Order, String>> typeOrders = new LinkedList<>();
    @JacksonXmlElementWrapper(localName = "typeDefinitions")
    @JacksonXmlProperty(localName = "definition")
    private final Collection<Pair<String, Lambda>> typeDefinitions = new LinkedList<>();

    public void addTypeOrder(String firstType, String secondType, String order) {
        typeOrders.add(new ImmutableTriple<>(firstType, Order.valueOf(order), secondType));
    }

    public void addTypeDefinition(String firstType, Lambda typeDefinition) {
        typeDefinitions.add(new ImmutablePair<>(firstType, typeDefinition));
    }

    @JsonIgnore
    public Collection<Triple<String, Order, String>> getTypeOrders() {
        return new LinkedList<>(typeOrders);
    }

    @JsonIgnore
    public Collection<Pair<String, Lambda>> getTypeDefinitions() {
        return new LinkedList<>(typeDefinitions);
    }
}