# Change Log
All notable changes to this project will be documented in this file.

This project adheres to [Semantic Versioning](http://semver.org/).

The format is based on [Keep a Changelog](http://keepachangelog.com/).

## 2.9.1 - 2017-06-29

### Fixed
 - Minor fixes in CORs.
 - Introduce CORs feature in README.md.
 
## 2.9.0 - 2017-06-27

### Added
 - Support for CORs functionality.

## 2.8.2 - 2017-06-13

### Fixed
 - Fix cancel request.
 - Fix logout in dynamic routing.

## 2.8.1 - 2017-06-01

### Fixed
 - Fixes in documentation of dynamic routing and troubleshooting section.
 - Fix logout when using websocket.

## 2.8.0 - 2017-04-26

### Added
 - Introduce table of contents in README.md.
 - Added JWT refresh in websocket connections.
 - Significant performance improvements via adopting @sap/logging version 3

## 2.7.1 - 2017-03-20

### Fixed
 - Add username to logs.
 - Minor fixes in websockets and session handling.

## 2.7.0 - 2017-02-13

### Added
- Replacements from services.
- Start approuter on https
- Show warning when a route is explicitly both public and csrf protected.

### Fixed
- Should not escape client cookies.
- Redirect to welcome page if not CSRF token fetch request.
- Wrong basic authentication status codes.

## 2.6.1 - 2017-01-25

### Changed
- Rename package to use @sap scope

## 2.6.0 - 2017-01-25

### Added
- `REQUEST_TRACE` environment variable for enhanced request tracing.
- Support for PATCH in router configuration.
- New extensions - see extending.md.

### Removed
- Customizable UAA config resolution.

### Fixed
- Fixes in documentation.
- Handling of request protocol.
- Removed npm 2 restriction.

## 2.5.0 - 2016-12-13

### Added
- Enable customizable UAA config resolution
- Support for custom error pages (errorPage in xs-app.json)
- Extend sizing guide

### Fixed
- Crash in error handler due to missing logger.
- Does not cache login responses.
- Does not log UAA missing when not needed.
- In case of parallel logins Approuter may use wrong user.
- Does not send basic credentials to backend, unless route is public.

## 2.4.0 - 2016-11-16

### Added
- Introduce SECURE_SESSION_COOKIE environment variable - enforces the secure flag of application router's session cookie.
- Additional checks for regular expressions during startup.

### Changed
- Previous component name in sap passport has been changed to 'XSA Approuter'.

### Fixed
 - Missing logging context in error handler when using extensions.

## 2.3.4 - 2016-11-04

### Fixed
- The _x-csrf-token_ header is no longer forwarded to backend in case a path requires authentication and CSRF token protection.
- Set the _Secure_ flag of the session cookie depending on the environment application router runs in.
- Some of the links in README.md were broken.

## 2.3.3 - 2016-11-02

### Added
-	Add COMPRESSION env var to be able to configure compression.

### Fixed
- Do not cache wsAllowedOrigins across requests.
- Favor UAA config from default-env.json over default-services.json.
-	Extend error message for proxy settings problem.
-	Enable compression by default when custom setting is provided.
-	Propagate errors to handler.
- Avoid session resave at the end of request. Fix session overwrite.

## 2.3.2 - 2016-09-30

### Fixed
- Cookie locationAfterLogin clash in port based routing.

## 2.3.1 - 2016-09-28

### Fixed
- Unverified redirect via locationAfterLogin cookie.
- Fallback to default UAA if no tenant captured.
- Fix X-Frame-Options header overwriting.
- Session cookie name - use application_id instead of instance_id.
- Fix port validation for approuter.start().

## 2.3.0 - 2016-09-02

### Added
- Multitenancy support.
- Matching route by both URL path and HTTP method.

### Fixed
- Fixed race condition while CSRF token generation.

## 2.2.0 - 2016-08-17

### Added
- Start approuter with xs-app.json passed as an object.
- Follow symlinks in localDir config.
- Document the Content-Security-Policy header as a best practice.

## 2.1.3 - 2016-08-13

### Added
- Genarate CSRF token once per session.

## 2.1.2 - 2016-08-06

### Fixed
- Remove instance cookies from client request.
- Fix locatioinAfterLogin cookie path.

## 2.1.1 - 2016-07-24

### Fixed
- Support to host welcome page externally.
- Fix logout path matching.
- Fix 500 sent in case locationAfterLogin cookie is missing.


## 2.1.0 - 2016-07-17

### Added
- Allow source of route to be matched in case-insensitive way.
- New configuration for maximum client connection timeout.
- Add support for approuter extensions (custom middleware).
- Allow fetching CSRF token with HEAD request.

## 2.0.0 - 2016-05-12

### Added
- Configuration for the Cache-Control header in xs-app.json. The header is used when serving static resources.

### Removed
- local-* files (e.g. local-destinations, local-plugins) can no longer be used in the approuter during local development. Instead of these the approuter reads a single file located in the working directory (default-env.json), which contains the corresponding environment variables (e.g. destinations, plugins) and their values.
