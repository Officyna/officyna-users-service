package br.com.officyna.domain.user.presenter;

import br.com.officyna.api.user.resources.UserResponse;
import br.com.officyna.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserPresenter {

    public UserResponse toResponse(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .userRole(entity.getUserRole())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

