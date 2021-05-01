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

import java.util.LinkedList;
import java.util.List;

@ToString
@NoArgsConstructor
public class TypeHierarchy {

    public static final String DEF = Constant.DEF;

    public enum Order {
        EQ,
        GT,
        LT
    }

    @JacksonXmlElementWrapper(localName = "typeOrders")
    @JacksonXmlProperty(localName = "order")
    private final List<Triple<String, Order, String>> typeOrders = new LinkedList<>();
    @JacksonXmlElementWrapper(localName = "typeDefinitions")
    @JacksonXmlProperty(localName = "definition")
    private final List<Pair<String, Lambda>> typeDefinitions = new LinkedList<>();

    public void addTypeOrder(String firstType, String secondType, String order) {
        typeOrders.add(new ImmutableTriple<>(firstType, Order.valueOf(order), secondType));
    }

    public void addTypeDefinition(String firstType, Lambda typeDefinition) {
        typeDefinitions.add(new ImmutablePair<>(firstType, typeDefinition));
    }

    @JsonIgnore
    public List<Triple<String, Order, String>> getTypeOrders() {
        return new LinkedList<>(typeOrders);
    }

    @JsonIgnore
    public List<Pair<String, Lambda>> getTypeDefinitions() {
        return new LinkedList<>(typeDefinitions);
    }
}