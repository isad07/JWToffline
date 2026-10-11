# JWTOffline

A cross-platform, offline desktop tool for decoding, encoding, and verifying JSON Web Tokens (JWTs). Built with Java.

## About

JWTOffline gives developers a fast, private, and reliable way to work with JWTs without relying on browser-based tools like jwt.io. All operations happen locally on your machine with zero data transmitted externally.

## Features

| Feature | Status (ITR1.2 prototype) |
|---------|---------------------------|
| **Decode**: header, payload, readable exp/iat/nbf, expired banner | Working |
| **Encode**: build and sign with HS256/384/512 and RS256/384/512 | Working |
| **Verify**: check signature with a secret or RSA public key | Working |
| **Workspace**: save, label, search, rename, delete tokens | Working on mock in memory data |
| **History**: recent tokens on the Home screen | Working (session only) |
| Saving the workspace to disk, syntax highlighting, PKCS#1 keys | Planned for later iterations |

## Running the prototype

You need JDK 17+ and Maven.

```
mvn javafx:run      # start the app
mvn test            # run the JUnit 5 tests, coverage report in target/site/jacoco
```

Sample tokens in the Workspace are signed with the secret `jwtoffline-demo-secret`, so you can try Verify straight away.

## Tech Stack

- **Language:** Java
- **GUI:** JavaFX
- **Testing:** JUnit 5, JaCoCo
- **Build:** Maven

## Project Structure

```
JWTOffline/
├── src/
│   ├── main/java/com/jwtoffline/
│   │   ├── core/        # JWT logic: decode, encode, verify, claims, JSON (no UI code)
│   │   ├── workspace/   # Saved tokens and history (in memory stub for now)
│   │   └── ui/          # JavaFX screens and navigation
│   ├── main/resources/  # app.css
│   └── test/java/com/jwtoffline/    # JUnit 5 tests for core and workspace
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
- Saeed Rafieepour

**Course:** DIGT2107 - Practice of Software Development  
**Instructor:** Dr. May Haidar  
**Term:** Fall 2026 / Winter 2027
