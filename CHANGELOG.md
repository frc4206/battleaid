# Change Log
All notable changes to this project will be documented in this file.
 
The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [0.2.0] - 2025-01-11

### Fixed
- Improper scaling in `TunedJoystick` due to not using polar coordinates.  `TunedJoystick` now uses polar coordinates with a periodicity to reduce computational overhead.  

### Changed
- Moved from `2024.3.2` libraries to `2025.2.1`.

## [0.1.2] - 2024-12-03

### Fixed
- Bug with configuration directory, now uses WPILib filesystem to obtain deploy directory.

## [0.1.1] - 2024-11-11

### Added
- Tests for `TunedJoystick`!
- Documentation added.

### Fixed
- Fixed a bug in `TunedJoystick` where the map function performed scaling outside the bounds of the input range.
 
## [0.1.0] - 2024-11-10
 
Initial features added to this project.
 
### Added
- `LoadableConfig`: Dynamically load POJOs from a _*.toml_
- `TunedJoystick`: Lets user customize feedback response from controller joysticks
 