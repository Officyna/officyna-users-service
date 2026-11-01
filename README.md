# officyna-users-service

Microsserviço responsável pela **gestão de usuários internos** (`ADMIN`, `MANAGER`, `ATTENDANT`, `MECHANIC`) e **autenticação centralizada (emissão de JWT)** do ecossistema **Officyna**.

---

## Arquitetura e Estrutura de Pacotes

O projeto segue os princípios de Clean Architecture / Arquitetura Hexagonal adotados na Fase 4:

- `br.com.officyna.api.user`: Contratos OpenAPI (`UserApi`), controllers REST (`UserController`), handlers de exceção (`UserExceptionHandler`) e DTOs (`UserRequest`, `UserUpdateRequest`, `UserResponse`).
- `br.com.officyna.domain.user`: Entidades de domínio (`User`, `UserRole`), casos de uso/regras de negócio (`UserService`), adaptadores (`UserControllerAdapter`), mappers, presenters e porta de saída (`UserRepository`).
- `br.com.officyna.infrastructure`:
  - `auth`: Endpoint de login (`POST /api/auth/login`) e emissão de token JWT.
  - `persistence`: Implementação do gateway MongoDB (`UserRepositoryGateway`, `UserMongoRepository`, `UserDocument`).
  - `security`: Configuração stateless com Spring Security e validação JWT (`JwtService`, `JwtAuthenticationFilter`).
- `br.com.officyna.seed`: Carga inicial do usuário administrador padrão (`UserSeeder`).

---

## Endpoints Principais

| Método | Rota | Descrição | Autenticação |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Autentica usuário interno e retorna token JWT | Pública |
| `GET` | `/api/users` | Lista todos os usuários ativos | Bearer JWT |
| `GET` | `/api/users/{id}` | Busca usuário por ID | Bearer JWT |
| `GET` | `/api/users/email/{email}` | Busca usuário por e-mail | Bearer JWT |
| `POST` | `/api/users` | Cria novo usuário interno (requer `ADMIN` ou `MANAGER`) | Bearer JWT |
| `PUT` | `/api/users/{id}` | Atualiza dados do usuário (senha opcional) | Bearer JWT |
| `DELETE` | `/api/users/{id}` | Desativa logicamente o usuário (*soft delete*) | Bearer JWT |

---

## Executando os Testes e Cobertura (JaCoCo)

```bash
./mvnw clean verify
```
