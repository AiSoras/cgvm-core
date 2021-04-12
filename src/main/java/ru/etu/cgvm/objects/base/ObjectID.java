package ru.etu.cgvm.objects.base;

import java.rmi.server.UID;
import java.util.Objects;
import java.util.Optional;

public final class ObjectID {
    public static final ObjectID NOT_ATTACHED = new ObjectID("0");

    private final String id;

    public ObjectID() {
        id = new UID().toString();
    }

    public ObjectID(String id) {
        this.id = Optional.ofNullable(id).orElse("-1");
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectID objectID = (ObjectID) o;
        return id.equals(objectID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}