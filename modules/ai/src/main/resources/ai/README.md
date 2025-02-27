# Robots Configuration Documentation

This document describes the structure and usage of the `robots.json` configuration file for the AI chatbot system.

## File Structure

The configuration file consists of the following main sections:

1. **Version Control**
   - `version`: Semantic version number of the configuration

2. **JSON Schema**
   - Defines the structure and validation rules for the configuration
   - Ensures data integrity and consistency

3. **Categories**
   - Standard category definitions for robots
   - Mapped to internationalized module paths

4. **Robots Array**
   - Collection of robot configurations
   - Each robot has a unique identifier and multilingual support

## Robot Configuration

Each robot entry contains:

### Basic Information

- `uid`: Unique identifier for the robot
- `name`: Technical name used in the system
- `type`: Robot type (currently only "LLM")
- `category`: Robot category (must match defined categories)

### Internationalization (i18n)

Each robot supports multiple languages (en, zh_cn, zh_tw) with the following fields:

- `nickname`: Display name in the respective language
- `prompt`: Instruction template for the AI
- `description`: Functional description of the robot

## Prompt Variables

Some prompts support variable substitution:

- `{query}`: User's input query
- `{context}`: Context information or search results
- `{history}`: Chat history
- `{chunk}`: Text chunk for processing (used in FAQ generation)

## Category Types

1. **Service Module** (`i18n.module.service`)
   - General customer service related bots
   - Includes Q&A, analysis, and support functions

2. **Ticket Module** (`i18n.module.ticket`)
   - Ticket management related bots
   - Handles ticket generation and processing

3. **Void Module** (`i18n.module.void`)
   - Basic empty bots
   - Used for creating custom implementations

## Version History

- 1.0.0: Initial release with i18n support
  - Added JSON Schema validation
  - Standardized categories
  - Implemented multilingual support

## Usage Example

```json
{
  "uid": "1",
  "name": "customer_service",
  "type": "LLM",
  "category": "i18n.module.service",
  "i18n": {
    "en": {
      "nickname": "Customer Service Q&A",
      "prompt": "Based on the provided document...",
      "description": "Create a customer service Q&A bot"
    },
    "zh_cn": {
      "nickname": "客服问答",
      "prompt": "根据提供的文档信息...",
      "description": "创建一个客服问答机器人"
    }
  }
}
```

## Best Practices

1. Always validate new entries against the JSON schema
2. Ensure all required translations are provided
3. Use consistent formatting for prompts
4. Test variable substitution in prompts
5. Keep descriptions clear and concise

## Development Guidelines

When adding new robots:

1. Assign a unique UID
2. Provide complete translations for all supported languages
3. Use appropriate category from standard categories
4. Include clear and detailed prompts
5. Test the configuration before deployment

## Error Handling

The system will validate:

- Schema compliance
- Required fields
- Category validity
- Language support completeness

Invalid configurations will be rejected with appropriate error messages.
