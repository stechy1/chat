package cz.stechy.chat.plugins.auth.service;

import com.google.inject.Singleton;
import cz.stechy.chat.core.connection.IClient;
import cz.stechy.chat.plugins.auth.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

@Singleton
class AuthService implements IAuthService {

    private final Map<IClient, User> users = new HashMap<>();

    @Override
    public Optional<User> login(String username, IClient client) {
        final Optional<User> optionalUser = users.values().stream()
            .filter(user -> Objects.equals(username, user.name))
            .findFirst();

        if (optionalUser.isPresent()) {
            return Optional.empty();
        }

        final User user = new User(username);
        users.put(client, user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> logout(String id) {
        IClient client = null;
        for (Entry<IClient, User> userEntry : users.entrySet()) {
            if (Objects.equals(id, userEntry.getValue().id)) {
                client = userEntry.getKey();
                break;
            }
        }

        if (client != null) {
            return logout(client);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> logout(IClient client) {
        final User user = users.get(client);
        users.remove(client);

        return Optional.of(user);
    }
}
