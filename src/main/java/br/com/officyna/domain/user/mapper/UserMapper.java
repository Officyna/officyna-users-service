package br.com.officyna.domain.user.mapper;

import br.com.officyna.api.user.resources.UserRequest;
import br.com.officyna.api.user.resources.UserUpdateRequest;
import br.com.officyna.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .userRole(request.userRole())
                .active(true)
                .build();
    }

    public User toEntity(UserUpdateRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .userRole(request.userRole())
                .active(true)
                .build();
    }
}

