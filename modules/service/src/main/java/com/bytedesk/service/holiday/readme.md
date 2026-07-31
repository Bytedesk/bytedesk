# Holiday

## Purpose

HolidayEntity is the shared date model for holidays, makeup workdays, and custom special dates at platform or organization scope.

It is already connected to the unified WorktimeService holiday evaluation path, where it is used to decide whether a specific day is an official holiday, a makeup workday, or an organization-specific special date.

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

HolidayEntity has been narrowed from a template-like entity into a runnable date-based model, primarily to support unified worktime evaluation across service and call-center scenarios.

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

## Relationship To Unified Worktime

HolidayService reads HolidayEntity through HolidayRepository and resolves the effective holiday set for platform and organization scope. WorktimeService then uses that result to decide whether the current date should follow regular worktimes or special holiday worktimes.

The current convention is:

- offDay=true means the date should be treated as a holiday or rest day,
- offDay=false means the date is a makeup workday and should not automatically switch into holiday hours,
- organization-level rows override same-day platform rows when holiday scope includes both.

So HolidayEntity is not only admin-side reference data. It is a direct runtime input for unified service-time evaluation.

## Typical Use Cases

### Switch To Holiday Hours

Configure WorktimeSettingEntity with holiday-aware specialWorktimes.

This lets the system switch to holiday-specific service hours and route calls or chats to holiday IVR, announcement, voicemail, or non-worktime handling.

### Keep Working On Makeup Workdays

For dates defined by the government as makeup workdays, HolidayEntity stores offDay=false.

That prevents the date from being treated as a holiday and allows normal regularWorktimes to continue matching.

## Recommendations

- Keep official holiday seed data at platform scope and avoid overriding it directly in business logic.
- If an organization needs its own service-stop dates, add organization-level HolidayEntity rows as supplements.
- If more countries are added later, keep the same countryCode + holidayDate + holidayKey structure for consistency and efficient lookup.
