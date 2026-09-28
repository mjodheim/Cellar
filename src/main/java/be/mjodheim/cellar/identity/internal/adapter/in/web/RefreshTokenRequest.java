package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload carrying an opaque refresh token.
 */
record RefreshTokenRequest(@NotBlank String refreshToken) {}
