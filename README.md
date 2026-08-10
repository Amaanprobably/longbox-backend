# Longbox

Backend for **Longbox**, a comic-reader app. This is a Ktor + MongoDB service that handles auth and proxies/caches data from the Comic Vine API so the Android client can browse and search 1,000+ comics without hammering Comic Vine's rate limits.

The Android client (Kotlin, Jetpack Compose, Paging 3 + Room) lives in a separate repo.

## Features

- **JWT auth** with access + refresh token rotation
- **MongoDB** for user data, wired through Koin-injected repositories
- **Comic Vine proxy** with response shaping — `field_list` filtering trims payloads from ~125KB down to ~2KB per call
- **Custom rate limiter** — Mutex-based, with coordinated global backoff (`nextAllowedTime` + `penalize()`) to keep every concurrent request under Comic Vine's 1 req/sec cap
- **Caffeine caching** (2,000-entry, stats enabled) to cut down on upstream calls
- **Centralized error handling** via Status Pages, backed by a sealed `AppException` hierarchy
- **Sentry monitoring** with structured trace-ID logging for production visibility
- **`/admin/cache-stats`** — Basic Auth–protected endpoint with its own rate limiter, for inspecting cache hit rates
- Deployed on **Railway**

## Tech stack

Kotlin · Ktor · MongoDB · Koin (DI) · Caffeine · Sentry · Comic Vine API

## API overview

| Group | Description |
|---|---|
| `/auth/*` | Signup, login, token refresh |
| `/comics/*` | Browse and search, proxied and cached from Comic Vine — JWT-protected |
| `/admin/cache-stats` | Cache hit/miss stats — Basic Auth–protected |

> Route paths above are illustrative — update this table with your actual endpoint names before publishing.

## Running locally

```bash
./gradlew run
```

You'll need the following environment variables set:

```
MONGODB_URI=
JWT_SECRET=
COMIC_VINE_API_KEY=
SENTRY_DSN=
```

> Fill in the exact variable names your app reads from — update the block above to match `application.conf` / your config loader before publishing.

On successful start:

```
Application - Application started in 0.303 seconds.
Application - Responding at http://0.0.0.0:8080
```

## Testing

Not yet included — planned as a follow-up.

## Roadmap

- [ ] Unit/integration test coverage
- [ ] Public API documentation
