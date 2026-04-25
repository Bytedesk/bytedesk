# public_account

This package manages public account records, account type metadata, and public account lifecycle APIs.

## Implementation Notes

- Core models include PublicAccountEntity, PublicAccountRequest, PublicAccountResponse, and PublicAccountTypeEnum.
- PublicAccountRepository, PublicAccountSpecification, PublicAccountRestController, and PublicAccountRestService provide persistence, filtering, and management endpoints for public-account records.
- PublicAccountInitializer, PublicAccountEntityListener, and PublicAccountEventListener provide bootstrap support and lifecycle integration for public-account records.
