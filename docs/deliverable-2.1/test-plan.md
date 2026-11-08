# JWTOffline Test Plan (ITR2.1)

Full version with every test case: [Deliverable_2_1_JWTOffline.pdf](Deliverable_2_1_JWTOffline.pdf).
Traceability: [traceability-matrix.csv](traceability-matrix.csv). Test cases: [test-cases.md](test-cases.md).

## Scope

All 23 functional and 8 non-functional requirements from Deliverable 1.2. Automated GUI tests (TestFX) are
planned for Deliverable 3.1; until then the screens are covered by manual test cases.

## Test levels

| Level | What it checks | Tool |
|---|---|---|
| Unit | Decoder, encoder, verifier, claims, JSON, keys, repositories, history | JUnit 5 |
| Integration | Sample tokens through repository, decoder and verifier | JUnit 5 |
| Performance | NFR-2: 8 KB token decoded or verified in under 100 ms (median of 21 runs) | JUnit 5 |
| Robustness | NFR-5: seeded random input never escapes as anything but JwtException | JUnit 5 |
| Static | NFR-1 no network code, NFR-7 no JavaFX in core or workspace | JUnit 5 source scan |
| Coverage | NFR-7: 80% line coverage of core and workspace | JaCoCo |
| Manual | Screens, clipboard, restart, platforms, offline, accessibility, usability | Written steps |

## Running

```
mvn test        # all 90 tests
mvn verify      # tests plus coverage report in target/site/jacoco/index.html
mvn javafx:run  # start the app for manual test cases
```

CI runs `mvn -B verify` on every push to main, development and feature branches and attaches the JaCoCo report.

## Exit criteria

All automated tests pass in CI, every requirement maps to at least one test case, and every failure is
logged as a defect (D-01, D-02, ...) with a regression test.

## Defects

| ID | Description | Status |
|---|---|---|
| D-01 | `{"exp":1e9999999999}` made BigDecimal throw NumberFormatException out of Json.parse, which the screens do not catch. Now reported as invalid JSON. Regression test `RobustnessTest#hugeExponentIsReportedAsInvalidJson`. | Fixed |
