package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Login payload.
 *
 * <p>Error responses remain generic so the API does not reveal whether an email
 * address exists.</p>
 *
 * @param email account email
 * @param password clear-text password supplied for this authentication attempt
 */
record LoginRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Utf8Size String password
) {}
