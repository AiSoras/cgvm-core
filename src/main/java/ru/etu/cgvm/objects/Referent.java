package ru.etu.cgvm.objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.etu.cgvm.objects.Constant.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Referent {

    // Взаимоисключающие поля
    private Descriptor descriptor;
    private Designation designation;
    private boolean isNegated;

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static final class Designation {
        private String additionalInfo;
        // Взаимоисключающие поля
        private String literal;
        private String locator;
        private String quantifier;

        @Override
        public String toString() {
            return
                    (additionalInfo == null ? EMPTY : (additionalInfo + SPACE))
                            + (literal == null ? EMPTY : (literal))
                            + (locator == null ? EMPTY : (locator))
                            + (quantifier == null ? EMPTY : (quantifier));

        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static final class Descriptor {
        private String additionalInfo;
        private String structure;

        public List<String> getStructureMembers() {
            return Arrays.stream(getElementEnumeration()
                    .split(",")).map(String::trim).collect(Collectors.toList());
        }

        private boolean isAnyElement() {
            return getElementEnumeration()
                    .equals(Constant.ALL_SET_ELEMENTS);
        }

        private String getElementEnumeration() {
            return Optional.ofNullable(structure)
                    .orElse(EMPTY)
                    .replaceAll("[<>{}]", EMPTY);
        }

        @Override
        public String toString() {
            return (additionalInfo == null ? EMPTY : (additionalInfo + SPACE))
                    + (structure == null ? EMPTY : structure);
        }
    }

    public boolean isAny() {
        return Optional.ofNullable(descriptor).orElse(new Descriptor()).isAnyElement();
    }

    @Override
    public String toString() {
        if (isNegated) {
            return TILDA;
        } else {
            return (descriptor == null ? EMPTY : (descriptor + SPACE))
                    + (designation == null ? EMPTY : designation);
        }
    }
}