# Changelog

All notable changes to this project will be documented in this file.

## Unreleased

### Added

- Add `InstantRange`, a half-open instant interval record with duration, containment, overlap, intersection, abutment, and span helpers.
- Add `ZonedDay`, a record for DST-safe local-day boundaries, actual day duration, transition-day classification, instant containment, and fixed-duration interval operations.
- Add `Instants.toUtcMidnight(...)` for normalizing common temporal types to UTC midnight.
- Add epoch-aligned instant interval rounding helpers with `Instants.floorToInterval(...)` and `Instants.ceilToInterval(...)`.

### Changed

- Replace the generated sample library code with focused time utilities in `hora-core`.
- Keep time behavior in record-based domain primitives instead of duplicate static utility APIs.
