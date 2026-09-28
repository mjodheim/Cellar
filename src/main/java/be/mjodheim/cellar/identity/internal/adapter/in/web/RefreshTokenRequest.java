package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload carrying an opaque refresh token.
 *
 * @param refreshToken raw refresh-token value received from the client
 */
record RefreshTokenRequest(@NotBlank String refreshToken) {}
