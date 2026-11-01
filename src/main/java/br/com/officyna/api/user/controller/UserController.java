package br.com.officyna.api.user.controller;

import br.com.officyna.api.user.UserApi;
import br.com.officyna.api.user.resources.UserRequest;
import br.com.officyna.api.user.resources.UserResponse;
import br.com.officyna.api.user.resources.UserUpdateRequest;
import br.com.officyna.domain.user.controller.UserControllerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserControllerAdapter userControllerAdapter;

    @Override
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userControllerAdapter.findAll());
    }

    @Override
    public ResponseEntity<UserResponse> findById(String id) {
        return ResponseEntity.ok(userControllerAdapter.findById(id));
    }

    @Override
    public ResponseEntity<UserResponse> findByEmail(String email) {
        return ResponseEntity.ok(userControllerAdapter.findByEmail(email));
    }

    @Override
    public ResponseEntity<UserResponse> create(UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userControllerAdapter.create(request));
    }

    @Override
    public ResponseEntity<UserResponse> update(String id, UserUpdateRequest request) {
        return ResponseEntity.ok(userControllerAdapter.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        userControllerAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}

