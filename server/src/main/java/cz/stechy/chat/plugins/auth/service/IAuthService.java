package cz.stechy.chat.plugins.auth.service;

import com.google.inject.ImplementedBy;
import cz.stechy.chat.core.connection.IClient;
import cz.stechy.chat.plugins.auth.User;
import java.util.Optional;

@ImplementedBy(AuthService.class)
public interface IAuthService {

    Optional<User> login(String username, IClient client);

    void logout(String id);

    void logout(IClient client);
}
