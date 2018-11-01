package cz.stechy.chat.plugins.auth;

import java.util.Objects;
import java.util.UUID;

public final class User {

    public final String id;
    public final String name;

    public User(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) &&
            Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}