# auth

This package manages login requests, token-based authentication, auth filtering, and login success events.

## Implementation Notes

- AuthController, AuthRequest, and AuthResponse define the login-facing API contract for authentication entry.
- AuthService, AuthToken, and AuthTypeEnum implement login orchestration, token payload handling, and authentication type modeling.
- AuthTokenFilter and AuthEntryPoint integrate with the security chain for token parsing and unauthorized access responses.
- AuthLoginRetryHelper and AuthEventListener support login retry control and authentication-side event handling.
- The event subpackage currently provides AuthSuccessEvent for post-login event publication.
