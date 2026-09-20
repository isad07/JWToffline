# JWTOffline

A cross-platform, offline desktop tool for decoding, encoding, and verifying JSON Web Tokens (JWTs). Built with Java.

## About

JWTOffline gives developers a fast, private, and reliable way to work with JWTs without relying on browser-based tools like jwt.io. All operations happen locally on your machine with zero data transmitted externally.

## Features (Planned)

- **Token Decoding** : Paste a JWT and view the decoded header, payload, and signature with syntax highlighting and human-readable timestamps.
- **Token Encoding** : Build and sign new JWTs from scratch using HMAC or RSA algorithms.
- **Signature Verification** : Verify token signatures using a secret or public key.
- **Workspace & History** : Save, label, and revisit tokens locally across sessions.

## Tech Stack

- **Language:** Java
- **GUI:** JavaFX
- **Testing:** JUnit 5, JaCoCo
- **Build:** Maven

## Project Structure

```
JWTOffline/
├── src/
│   ├── main/java/com/jwtoffline/    # Application source code
│   └── test/java/com/jwtoffline/    # Unit tests
├── docs/                             # Project documentation
├── pom.xml                           # Maven build config
└── README.md
```

## Branching Strategy

| Branch | Purpose |
|--------|---------|
| `main` | Stable, tagged releases for each deliverable |
| `development` | Active development and integration |
| `feature/*` | Individual feature branches (e.g., feature/decode, feature/encode) |

## Team

- Isa Din
- Steven Passynkov
- Osmond Jiang

**Course:** DIGT2107 - Practice of Software Development  
**Instructor:** Dr. May Haidar  
**Term:** Fall 2026 / Winter 2027
