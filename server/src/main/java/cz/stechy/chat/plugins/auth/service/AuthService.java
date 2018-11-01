package cz.stechy.chat.plugins.auth.service;

import com.google.inject.Singleton;
import cz.stechy.chat.plugins.auth.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Singleton
class AuthService implements IAuthService {

    private final List<User> users = new ArrayList<>();

    @Override
    public Optional<User> login(String username) {
        final Optional<User> optionalUser = users.stream()
            .filter(user -> Objects.equals(username, user.name))
            .findFirst();

        if (optionalUser.isPresent()) {
            return Optional.empty();
        }

        final User user = new User(username);
        users.add(user);
        return Optional.of(user);
    }

    @Override
    public void logout(String id) {
        users.removeIf(user -> Objects.equals(id, user.id));
    }
}
