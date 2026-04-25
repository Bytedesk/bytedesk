# user

This package manages user profiles, security identity adapters, organization-role mappings, and user lifecycle APIs.

## Implementation Notes

- Core models include UserEntity, UserRequest, UserResponse, UserResponseSimple, UserResponseContact, UserExtra, UserTypeEnum, and UserOrganizationRoleEntity for profile and membership data.
- UserRepository, UserSpecification, UserRestController, UserRestService, and UserService provide persistence, filtering, REST endpoints, and user-domain orchestration.
- UserDetailsImpl and UserDetailsServiceImpl integrate user data with Spring Security authentication and authorization flows.
- UserProtobuf, UserConvertUtils, UserTools, and UserUtils provide protobuf mapping, conversion helpers, and shared utility logic.
- UserEntityListener, UserEventListener, and the event subpackage handle create, update, login, and logout events, while UserInitializer and UserPermissions provide bootstrap data and permission metadata.
