package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Public registration payload.
 *
 * @param email requested account email
 * @param displayName user-facing name
 * @param password clear-text password to hash immediately
 * @param passwordConfirm confirmation used only for request validation
 */
@PasswordMatches
record RegisterRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 150) String displayName,
        @NotBlank @Size(min = 12, max = 128) String password,
        @NotBlank @Size(min = 12, max = 128) String passwordConfirm
) {}
