# Holiday

## Purpose

HolidayEntity is the shared date model for holidays, makeup workdays, and custom special dates at platform or organization scope.

It is already connected to the enterprise/call time-condition holiday rule, where it is used to decide whether a specific day is an official holiday or a named holiday.

## Core Entity

HolidayEntity maps to the bytedesk_service_holiday table. The current model focuses on day-level decision making. The main fields are:

- name: holiday name, such as New Year's Day, Spring Festival, or Qingming Festival.
- description: description text.
- type: holiday type, currently used to distinguish official and custom sources.
- holidayDate: the exact date.
- holidayYear: the year, useful for yearly queries.
- countryCode: country or region code, default is CN.
- offDay: whether the date is a rest day. false can represent a makeup workday.
- official: whether the row comes from official seed data.
- sourceUrl: the source of the data.
- holidayKey: a business key used for exact holiday matching.

## Current Modeling Boundary

HolidayEntity has been narrowed from a template-like entity into a runnable date-based model, primarily to support call-center time-condition evaluation.

The current focus is:

- whether a specific day is an official off-day,
- whether a specific day is a named holiday,
- whether a specific day is a makeup workday.

Older template fields such as startDate, endDate, repeatType, and customerNotice are intentionally not active right now. If future business cases require richer leave or service-pause behavior, they should be remodeled from actual requirements instead of reviving the template directly.

## Initialization

HolidayInitializer runs two initialization steps after application startup:

- initialize HOLIDAY_* permissions,
- initialize 2026 China statutory holidays and makeup workdays.

The current seed source is defined in HolidayInitData, with sourceUrl pointing to:

- <https://github.com/NateScarlet/holiday-cn/blob/master/2026.json>

Initialization skips any existing countryCode + holidayDate row to avoid duplicates.

## Relationship To Time Conditions

ChinaHolidayProvider in enterprise/call reads HolidayEntity through HolidayRepository and provides holiday matching support to TimeConditionMatcher.

The current convention is:

- blank holiday value, CN, CN-OFFICIAL, and CN-NATIONAL all mean any official Chinese off-day,
- the pattern CN:{holiday-name} means a specific holiday in China, for example CN:Spring Festival.

So HolidayEntity is not only admin-side reference data. It is also a direct runtime input for time-based call routing.

## Typical Use Cases

### Switch Call Flow On Holidays

Configure the following rule in TimeCondition:

- field=holiday, value=CN

This lets the system match official holidays and route calls to a holiday IVR, announcement, or voicemail flow.

### Special Behavior For One Holiday

Configure the following rule in TimeCondition:

- field=holiday, value=CN:Spring Festival

This enables special greetings or routing only during Spring Festival.

### Keep Working On Makeup Workdays

For dates defined by the government as makeup workdays, HolidayEntity stores offDay=false.

That prevents the date from being treated as a holiday and allows normal working-hours rules to continue matching.

## Recommendations

- Keep official holiday seed data at platform scope and avoid overriding it directly in business logic.
- If an organization needs its own service-stop dates, add organization-level HolidayEntity rows as supplements.
- If more countries are added later, keep the same countryCode + holidayDate + holidayKey structure for consistency and efficient lookup.
