# Longbox Backend

**Kotlin/Ktor backend powering Longbox, a comic discovery and tracking app.**

The backend sits between the Android client and Comic Vine, handling authentication, persistence, caching, rate limiting, response shaping, and observability.

**Stack:** Kotlin · Ktor (Netty) · MongoDB · Koin · Caffeine · JWT · kotlinx.serialization · Sentry · Railway

## Architecture

<img width="1256" alt="image" src="https://github.com/user-attachments/assets/abad0261-e078-4bb9-bd94-3788a698281b" />

Longbox treats Comic Vine as an **upstream dependency**, not part of the client-facing API. This keeps the Android app independent of the external API contract while the backend controls traffic, response shape, and failure behavior.

### Auth flow: refresh token rotation

```text
Client                         API                          MongoDB
  │── POST /auth/login ───────►│── verify credentials ─────►│
  │◄── access + refresh ───────│◄── store refresh token ────│
  │                            │                            │
  │── GET /comics/... ────────►│  (access token valid)      │
  │◄── 200 ────────────────────│                            │
  │                            │                            │
  │── GET /comics/... ────────►│  (access token expired)    │
  │◄── 401 ────────────────────│                            │
  │                            │                            │
  │── POST /auth/refresh ─────►│── validate old refresh ───►│
  │                            │── invalidate old, store new│
  │◄── new access + refresh ───│◄───────────────────────────│
  │                            │                            │
  │── POST /auth/refresh ─────►│  (old refresh token reused)│
  │◄── 401 ────────────────────│                            │
```

Every refresh issues a new pair and invalidates the previous refresh token, so a leaked token has a short useful life.

## API Endpoints

Base URL: `http://localhost:8080` (local)

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | No | Create a user account |
| `POST` | `/auth/login` | No | Returns access + refresh tokens |
| `POST` | `/auth/refresh` | Refresh token | Rotates the token pair |
| `GET` | `/comics/search` | JWT | Search comics (cached, rate-limited upstream) |
| `GET` | `/comics/{id}` | JWT | Comic details (cached, rate-limited upstream) |
| `GET` | `/users/me` | JWT | Current user profile |

Errors are returned in a consistent shape via Ktor `StatusPages` and a sealed `AppException` hierarchy.

## Engineering Highlights

| Concern | Approach |
|---|---|
| Authentication | JWT access + refresh token rotation |
| Persistence | MongoDB + repository layer |
| Caching | Caffeine cache-aside strategy |
| Rate limiting | Coordinated global upstream limiter (1 req/sec) |
| API integration | Response shaping / field filtering |
| Errors | Ktor `StatusPages` + sealed `AppException` |
| DI | Koin |
| Observability | Sentry + structured logging with trace IDs |
| Deployment | Railway |

## Design Decisions & Limitations

**Caffeine over Redis.** Comic Vine data is read-heavy and tolerant of staleness, and the app runs as a single instance. An in-process cache removes a network hop and an extra piece of infrastructure.

**Cache-aside.** Requests hit Caffeine first. Only a cache miss goes through the rate limiter to Comic Vine, which keeps upstream traffic well under the 1 req/sec budget.

**Limitation: state is per-instance.** The cache and the upstream limiter live in process memory. On a single instance the limiter is truly global; with multiple instances, each would have its own limiter and cache, and combined traffic could exceed Comic Vine's limit.

**Scaling path.** Move the cache and limiter state to Redis (shared cache, distributed token bucket) before running more than one instance.

## Local Development

### Requirements

- JDK 21+
- MongoDB (local or Atlas)
- Comic Vine API key

### Setup

1. **Get a Comic Vine API key:** sign up at [comicvine.gamespot.com/api](https://comicvine.gamespot.com/api/) and copy the key from your account.

2. **Start MongoDB locally** (skip if using Atlas):

   ```bash
   docker run -d --name longbox-mongo -p 27017:27017 mongo:7
   ```

3. **Configure environment variables:**

   ```env
   MONGODB_URI=mongodb://localhost:27017/longbox
   JWT_SECRET=<long random string>
   COMIC_VINE_API_KEY=<your key>
   SENTRY_DSN=<optional>
   ```

4. **Run:**

   ```bash
   ./gradlew run
   ```

   The server starts on `http://localhost:8080`.

### Test

```bash
./gradlew test
```
