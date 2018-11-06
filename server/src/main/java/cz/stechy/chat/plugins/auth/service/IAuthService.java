package cz.stechy.chat.plugins.auth.service;

import com.google.inject.ImplementedBy;
import cz.stechy.chat.core.connection.IClient;
import cz.stechy.chat.plugins.auth.User;
import java.util.Optional;

@ImplementedBy(AuthService.class)
public interface IAuthService {

    Optional<User> login(String username, IClient client);

    Optional<User> logout(String id);

    Optional<User> logout(IClient client);
}
