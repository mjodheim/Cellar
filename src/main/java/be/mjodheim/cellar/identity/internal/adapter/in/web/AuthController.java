package be.mjodheim.cellar.identity.internal.adapter.in.web;

import be.mjodheim.cellar.identity.internal.application.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * HTTP entry point for registration, authentication, token rotation and account profile.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentification, jetons et profil utilisateur")
class AuthController {

    private final AuthenticationService authenticationService;
    private final UserAccountService userAccountService;

    AuthController(
            AuthenticationService authenticationService,
            UserAccountService userAccountService
    ) {
        this.authenticationService = authenticationService;
        this.userAccountService = userAccountService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un compte utilisateur")
    AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return AuthResponse.from(authenticationService.register(
                request.email(),
                request.displayName(),
                request.password()
        ));
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter")
    AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return AuthResponse.from(authenticationService.login(
                request.email(),
                request.password()
        ));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renouveler les jetons et faire tourner le refresh token")
    AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return AuthResponse.from(authenticationService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Révoquer un refresh token")
    void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authenticationService.logout(request.refreshToken());
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Consulter le profil authentifié")
    UserProfileResponse me(Authentication authentication) {
        return UserProfileResponse.from(userAccountService.findCurrent(authentication.getName()));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Supprimer logiquement le compte authentifié")
    void deleteMe(Authentication authentication) {
        userAccountService.deleteCurrent(authentication.getName());
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    ResponseEntity<ProblemDetail> handleEmailConflict(EmailAlreadyRegisteredException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Email already registered");
        problem.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidRefreshTokenException.class})
    ResponseEntity<ProblemDetail> handleUnauthorized(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Authentication failed");
        problem.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ProblemDetail> handleNotFound(UserNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("User not found");
        problem.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}
