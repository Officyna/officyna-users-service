package br.com.officyna.infrastructure.persistence.config;

import br.com.officyna.domain.user.controller.UserControllerAdapter;
import br.com.officyna.domain.user.mapper.UserMapper;
import br.com.officyna.domain.user.presenter.UserPresenter;
import br.com.officyna.domain.user.repository.UserRepository;
import br.com.officyna.domain.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PersistenceBeanConfig {

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, passwordEncoder);
    }

    @Bean
    public UserControllerAdapter userControllerAdapter(UserService userService,
                                                       UserMapper userMapper,
                                                       UserPresenter userPresenter) {
        return new UserControllerAdapter(userService, userMapper, userPresenter);
    }
}

