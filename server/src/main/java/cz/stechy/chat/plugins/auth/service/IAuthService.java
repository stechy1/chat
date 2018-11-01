package cz.stechy.chat.plugins.auth.service;

import com.google.inject.ImplementedBy;
import cz.stechy.chat.plugins.auth.User;
import java.util.Optional;

@ImplementedBy(AuthService.class)
public interface IAuthService {

    Optional<User> login(String username);

    void logout(String id);
}
