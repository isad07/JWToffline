# JWTOffline Test Cases (ITR2.1)

Updated after local verification on November 8, 2026 (Windows, JDK 17). mvn -o verify: BUILD SUCCESS; 94 tests, zero failures, errors, or skips. These are local results, not a new CI run. Manual checks remain Not run. The PDF remains the original deliverable snapshot.

| ID | Title | Req | Type | Automated by | Status | Priority |
|---|---|---|---|---|---|---|
| TC-001 | Decode a valid HS256 token | FR-2, FR-3 | Unit | `JwtDecoderTest#decodesHeaderAndPayload` | Pass | High |
| TC-002 | Accept a "Bearer " prefix and extra whitespace | FR-1 | Unit | `JwtDecoderTest#acceptsBearerPrefixAndWhitespace` | Pass | High |
| TC-003 | Show header and payload as indented JSON in original key order | FR-3 | Unit | `JwtDecoderTest#prettyPrintsJson`<br>`JsonTest#keepsKeyOrderWhenPrinting`<br>`JsonTest#parsesNestedValues`<br>`JsonTest#escapesStrings` | Pass | High |
| TC-004 | Empty input gives a plain message | FR-4 | Unit | `JwtDecoderTest#rejectsEmptyInput` | Pass | High |
| TC-005 | Wrong number of parts gives a plain message | FR-4 | Unit | `JwtDecoderTest#rejectsWrongNumberOfParts` | Pass | High |
| TC-006 | Bad Base64URL in the header is reported | FR-4 | Unit | `JwtDecoderTest#rejectsBadBase64` | Pass | High |
| TC-007 | Payload that is not JSON is reported | FR-4 | Unit | `JwtDecoderTest#rejectsPayloadThatIsNotJson`<br>`JsonTest#rejectsBrokenJson`<br>`JsonTest#rejectsLenientJsonSyntax` | Pass | High |
| TC-008 | Claim values are shown exactly as sent | FR-3 | Unit | `JsonTest#preservesLargeIntegerClaims`<br>`JsonTest#preservesPreciseDecimalClaims`<br>`JsonTest#preservesIntegersBeyondLongRange`<br>`JsonTest#preservesNullClaimsAndDoesNotHtmlEscapeValues`<br>`JwtDecoderTest#protectsNestedHeaderAndPayloadValues`<br>`JwtDecoderTest#snapshotsNestedValuesFromConstructorInputs` | Pass | Medium |
| TC-009 | Signing algorithm and encoded signature length are reported | FR-7 | Unit | `JwtDecoderTest#allowsUnsignedTokenWithEmptySignature`<br>`JwtDecoderTest#describesAlgorithmAndSignatureLength`<br>`JwtDecoderTest#distinguishesMissingAlgorithmFromNone` | Pass | Medium |
| TC-010 | Copy header, payload, and generated token | FR-8 | Manual UI | `Manual` | Not run | Medium |
| TC-011 | Token with a future exp shows as valid | FR-6 | Unit | `ClaimInspectorTest#validWhenExpInFuture` | Pass | High |
| TC-012 | Token past exp shows as expired | FR-6 | Unit | `ClaimInspectorTest#expiredWhenExpInPast`<br>`ClaimInspectorTest#expiredExactlyAtExp` | Pass | High |
| TC-013 | Token before nbf shows as not valid yet | FR-6 | Unit | `ClaimInspectorTest#notYetValidBeforeNbf` | Pass | High |
| TC-014 | Token without exp shows no expiry | FR-6 | Unit | `ClaimInspectorTest#noExpiryWhenExpMissing`<br>`ClaimInspectorTest#distinguishesNullExpiryFromMissingExpiry` | Pass | High |
| TC-015 | exp, iat, and nbf shown as readable local times | FR-5 | Unit | `ClaimInspectorTest#formatsTimeClaimsInOrder`<br>`ClaimInspectorTest#formatsFractionalSecondsOnEitherSideOfTheEpoch` | Pass | High |
| TC-016 | Bad time claims are flagged, not crashed on | FR-5, FR-4 | Unit | `ClaimInspectorTest#reportsNonNumericExpAsInvalid`<br>`ClaimInspectorTest#rejectsOutOfRangeAndNonFiniteTimestampsWithoutThrowing`<br>`ClaimInspectorTest#handlesDatesOutsideTheDisplayableRange`<br>`ClaimInspectorTest#checksAllPresentTimeClaims` | Pass | Medium |
| TC-017 | Durations read naturally | FR-6 | Unit | `ClaimInspectorTest#humanDurations` | Pass | Low |
| TC-018 | Encoder reproduces a known HS256 token | FR-11 | Unit | `JwtEncoderTest#reproducesKnownHs256Token` | Pass | High |
| TC-019 | alg and typ are set from the chosen algorithm | FR-10, NFR-4 | Unit | `JwtEncoderTest#setsAlgAndTypAutomatically`<br>`JwtEncoderTest#headerAlgIsOverriddenBySelectedAlgorithm` | Pass | High |
| TC-020 | All six algorithms sign and verify | FR-10, FR-11, FR-14 | Unit | `SignatureVerifierTest#roundTripsAllHmacAlgorithms`<br>`SignatureVerifierTest#roundTripsRsaWithPublicKey`<br>`JwtEncoderTest#signsWithRsaPrivateKey` | Pass | High |
| TC-021 | Invalid JSON is caught before signing | FR-9 | Unit | `JwtEncoderTest#rejectsInvalidHeaderJsonWithPosition`<br>`JwtEncoderTest#rejectsInvalidPayloadJson` | Pass | High |
| TC-022 | Missing or unusable keys are explained | FR-11 | Unit | `JwtEncoderTest#rejectsMissingKey`<br>`JwtEncoderTest#rejectsGarbageRsaKey`<br>`KeyParserTest#reportsMissingAndMalformedKeys`<br>`KeyParserTest#rejectsNonRsaKeys`<br>`KeyParserTest#rejectsPublicKeyWherePrivateKeyIsRequired` | Pass | High |
| TC-023 | PKCS#1 RSA private keys are accepted | FR-11 | Unit | `KeyParserTest#readsPkcs1KeysAndUsesThemForRsaSigning` | Pass | Medium |
| TC-024 | Numbers in claims are not rounded when signing | FR-11 | Unit | `JwtEncoderTest#signsWithoutRoundingHeaderOrPayloadNumbers` | Pass | Medium |
| TC-025 | Open a generated token in the decoder | FR-12 | Manual UI | `Manual` | Not run | Medium |
| TC-026 | Correct HMAC secret passes | FR-13 | Unit | `SignatureVerifierTest#acceptsCorrectHmacSecret` | Pass | High |
| TC-027 | Wrong HMAC secret fails with a reason | FR-13, FR-15 | Unit | `SignatureVerifierTest#rejectsWrongHmacSecret` | Pass | High |
| TC-028 | Edited payload is detected | FR-13, NFR-4 | Unit | `SignatureVerifierTest#detectsTamperedPayload`<br>`SignatureVerifierTest#verifiesOriginalBytesWithoutReformattingJson` | Pass | High |
| TC-029 | RSA token checked against a public key | FR-14 | Unit | `SignatureVerifierTest#roundTripsRsaWithPublicKey`<br>`SignatureVerifierTest#rejectsRsaTokenWithOtherPublicKey` | Pass | Medium |
| TC-030 | alg "none" and unsupported algorithms are refused | FR-15, NFR-4 | Unit | `SignatureVerifierTest#refusesAlgNone`<br>`SignatureVerifierTest#reportsUnsupportedAlgorithm`<br>`SignatureVerifierTest#reportsMissingAlgorithmInsteadOfUnsignedToken` | Pass | High |
| TC-031 | Missing key and broken token are explained | FR-15, FR-4 | Unit | `SignatureVerifierTest#explainsMissingKey`<br>`SignatureVerifierTest#reportsMalformedTokenInsteadOfThrowing` | Pass | High |
| TC-032 | Signature check is separate from expiry | FR-15 | Unit | `SignatureVerifierTest#checksSignatureIndependentlyOfTimeClaims`<br>`SignatureVerifierTest#allowsInspectingMalformedDateClaimsButReportsVerificationError` | Pass | Medium |
| TC-033 | Send the decoded token to the Verify screen | FR-16 | Manual UI | `Manual` | Not run | Medium |
| TC-034 | Save a token with a label | FR-17 | Unit | `InMemoryTokenRepositoryTest#savesAndFindsToken`<br>`InMemoryTokenRepositoryTest#blankLabelBecomesUntitled`<br>`InMemoryTokenRepositoryTest#rejectsEmptyToken` | Pass | Medium |
| TC-035 | Find saved tokens by label, newest first | FR-18 | Unit | `InMemoryTokenRepositoryTest#searchMatchesLabelIgnoringCase`<br>`FileTokenRepositoryTest#keepsSaveTimeAndOrderAfterReload` | Pass | Medium |
| TC-036 | Rename and delete a saved token | FR-19 | Unit | `InMemoryTokenRepositoryTest#renamesAndDeletes` | Pass | Low |
| TC-037 | Sample tokens decode and verify | FR-17 | Integration | `InMemoryTokenRepositoryTest#sampleDataDecodesAndVerifiesWithDemoSecret` | Pass | Low |
| TC-038 | Workspace table and opening a saved token | FR-18, FR-20 | Manual UI | `Manual` | Not run | Medium |
| TC-039 | Recent history on the Home screen | FR-21 | Unit | `TokenHistoryTest#newestFirstWithoutDuplicates`<br>`TokenHistoryTest#deduplicatesBearerAndRawTokens`<br>`TokenHistoryTest#dropsOldestPastLimit`<br>`TokenHistoryTest#ignoresBlankTokens`<br>`TokenHistoryTest#limitMustBePositive` | Pass | Low |
| TC-040 | Workspace survives closing and reopening | FR-22 | Unit | `FileTokenRepositoryTest#savedTokensSurviveReopening`<br>`FileTokenRepositoryTest#renameAndDeleteArePersisted`<br>`FileTokenRepositoryTest#searchWorksAfterReload` | Pass | High |
| TC-041 | First launch creates the workspace file | FR-22 | Unit | `FileTokenRepositoryTest#firstLaunchCreatesFileWithSampleTokens`<br>`FileTokenRepositoryTest#missingFileWithoutSeedingStartsEmpty` | Pass | Medium |
| TC-042 | Damaged workspace file does not stop the app | FR-22, NFR-5 | Unit | `FileTokenRepositoryTest#corruptFileStartsEmptyAndKeepsBackup`<br>`FileTokenRepositoryTest#entryWithMissingFieldsIsTreatedAsCorrupt` | Pass | High |
| TC-043 | Saved tokens are there after restarting the app | FR-22 | Manual UI | `Manual` | Not run | High |
| TC-044 | Side navigation keeps each screen's input | FR-23 | Manual UI | `Manual` | Not run | High |
| TC-045 | No networking code in the app | NFR-1 | Static | `ArchitectureTest#noSourceFileUsesNetworking`<br>`ArchitectureTest#sourcesAreFound` | Pass | High |
| TC-046 | Secrets are never written to disk | NFR-1 | Unit | `FileTokenRepositoryTest#fileStoresOnlyTokensAndLabels` | Pass | High |
| TC-047 | App works with the network turned off | NFR-1 | Manual | `Manual` | Not run | Medium |
| TC-048 | Decode an 8 KB token in under 100 ms | NFR-2 | Performance | `PerformanceTest#decodes8kbTokenUnder100ms` | Pass | High |
| TC-049 | Verify an 8 KB token in under 100 ms | NFR-2 | Performance | `PerformanceTest#verifiesHmac8kbTokenUnder100ms`<br>`PerformanceTest#verifiesRsa8kbTokenUnder100ms` | Pass | High |
| TC-050 | App starts in under 3 seconds | NFR-2 | Manual | `Manual` | Not run | Medium |
| TC-051 | New user decodes a token within 30 seconds | NFR-3 | Usability | `Manual` | Not run | Medium |
| TC-052 | Random input never crashes the logic | NFR-5, FR-4 | Robustness | `RobustnessTest#decoderOnlyThrowsJwtExceptionForRandomInput`<br>`RobustnessTest#encoderOnlyThrowsJwtExceptionForRandomJson`<br>`RobustnessTest#verifierNeverThrowsAndNeverPassesGarbage` | Pass | High |
| TC-053 | Number with a huge exponent (regression for D-01) | NFR-5, FR-4 | Robustness | `RobustnessTest#hugeExponentIsReportedAsInvalidJson` | Pass | High |
| TC-054 | Build and tests pass on Linux with JDK 17 | NFR-6 | CI | `.github/workflows/build.yml` | Pass | Medium |
| TC-055 | App runs on Windows and macOS | NFR-6 | Manual | `Manual` | Not run | Medium |
| TC-056 | Logic packages have no UI code | NFR-7 | Static | `ArchitectureTest#corePackageHasNoUiCode`<br>`ArchitectureTest#workspacePackageHasNoUiCode` | Pass | Medium |
| TC-057 | Line coverage of core and workspace is at least 80% | NFR-7 | Coverage | `JaCoCo (mvn verify)` | Pass | Medium |
| TC-058 | Keyboard only use and readable status | NFR-8 | Manual | `Manual` | Not run | Low |
| TC-059 | Failed writes preserve state and permit retry | FR-22, NFR-5 | Unit | `FileTokenRepositoryTest#failedWritesPreserveTokensAndAllowRetry` | Pass | High |
| TC-060 | Initial write failure allows startup | FR-22, NFR-5 | Unit | `FileTokenRepositoryTest#initialWriteFailureProducesWarningInsteadOfStoppingStartup` | Pass | High |
## Details

### TC-001: Decode a valid HS256 token

- **Requirement(s):** FR-2, FR-3
- **Type:** Unit
- **Automated by:** JwtDecoderTest#decodesHeaderAndPayload
- **Preconditions:** The well known jwt.io example token is available (HS256, sub 1234567890, name John Doe).
- **Steps:**
  1. Call JwtDecoder.decode() with the example token.
  2. Read the header map, payload map, and signature segment.
- **Expected:** Header has alg HS256 and typ JWT. Payload has sub "1234567890", name "John Doe", iat 1516239022. Signature segment is returned unchanged.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-002: Accept a "Bearer " prefix and extra whitespace

- **Requirement(s):** FR-1
- **Type:** Unit
- **Automated by:** JwtDecoderTest#acceptsBearerPrefixAndWhitespace
- **Preconditions:** Example token available.
- **Steps:**
  1. Build the input "  Bearer <token>\n".
  2. Call JwtDecoder.decode() on it.
- **Expected:** Token decodes. The raw token stored is the bare token with no prefix or whitespace.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-003: Show header and payload as indented JSON in original key order

- **Requirement(s):** FR-3
- **Type:** Unit
- **Automated by:** JwtDecoderTest#prettyPrintsJson, JsonTest#keepsKeyOrderWhenPrinting, JsonTest#parsesNestedValues, JsonTest#escapesStrings
- **Preconditions:** Example token available.
- **Steps:**
  1. Decode the token and call prettyHeader() and prettyPayload().
  2. Parse and print a JSON object whose keys are not alphabetical.
  3. Parse nested objects, arrays, and strings with quotes.
- **Expected:** Header prints as {"alg": "HS256", "typ": "JWT"} over four indented lines. Keys keep the order they had in the token. Nested values and escaped strings survive the round trip.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-004: Empty input gives a plain message

- **Requirement(s):** FR-4
- **Type:** Unit
- **Automated by:** JwtDecoderTest#rejectsEmptyInput
- **Preconditions:** None.
- **Steps:**
  1. Call decode() with "   ".
- **Expected:** A JwtException with the message "Paste a token to decode". No other exception.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-005: Wrong number of parts gives a plain message

- **Requirement(s):** FR-4
- **Type:** Unit
- **Automated by:** JwtDecoderTest#rejectsWrongNumberOfParts
- **Preconditions:** None.
- **Steps:**
  1. Call decode() with "abc.def" (two parts).
- **Expected:** A JwtException whose message says a JWT has 3 parts.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-006: Bad Base64URL in the header is reported

- **Requirement(s):** FR-4
- **Type:** Unit
- **Automated by:** JwtDecoderTest#rejectsBadBase64
- **Preconditions:** None.
- **Steps:**
  1. Call decode() with "@@@.e30.sig".
- **Expected:** A JwtException saying the header is not valid Base64URL.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-007: Payload that is not JSON is reported

- **Requirement(s):** FR-4
- **Type:** Unit
- **Automated by:** JwtDecoderTest#rejectsPayloadThatIsNotJson, JsonTest#rejectsBrokenJson, JsonTest#rejectsLenientJsonSyntax
- **Preconditions:** None.
- **Steps:**
  1. Decode a token whose payload is Base64URL of "hello".
  2. Parse {"a":}, {"a":1 (unclosed), {"a":1} extra, and [1,2] where an object is needed.
  3. Parse lenient JSON: single quotes, unquoted keys, 01, trailing comma, comments.
- **Expected:** Each case ends in a JwtException starting with "Invalid JSON" or saying the payload is not valid JSON. Lenient syntax is rejected, not silently accepted.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-008: Claim values are shown exactly as sent

- **Requirement(s):** FR-3
- **Type:** Unit
- **Automated by:** JsonTest#preservesLargeIntegerClaims, JsonTest#preservesPreciseDecimalClaims, JsonTest#preservesIntegersBeyondLongRange, JsonTest#preservesNullClaimsAndDoesNotHtmlEscapeValues, JwtDecoderTest#protectsNestedHeaderAndPayloadValues, JwtDecoderTest#snapshotsNestedValuesFromConstructorInputs
- **Preconditions:** None.
- **Steps:**
  1. Parse claims 9007199254740993, 9223372036854775808, 12345.67890123456789, null values, and the text <tag>&=é.
  2. Try to change the maps returned by the decoder.
  3. Change the input map after building a DecodedJwt.
- **Expected:** Numbers are not rounded, null stays null, and nothing is HTML escaped. The decoded maps cannot be modified and do not change when the input changes.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-009: Signing algorithm and encoded signature length are reported

- **Requirement(s):** FR-7
- **Type:** Unit
- **Automated by:** JwtDecoderTest#allowsUnsignedTokenWithEmptySignature, JwtDecoderTest#describesAlgorithmAndSignatureLength, JwtDecoderTest#distinguishesMissingAlgorithmFromNone
- **Preconditions:** None.
- **Steps:**
  1. Decode a token with alg "none" and an empty signature.
  2. Decode tokens whose header is {} and {"alg":null}.
- **Expected:** alg "none" is reported as "none" with an empty signature. A missing or null alg is reported as "(missing)", not as "none". The description used by Decode reports 43 chars for the HS256 sample and none for an empty signature; UI rendering remains manual.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-010: Copy header, payload, and generated token

- **Requirement(s):** FR-8
- **Type:** Manual UI
- **Automated by:** Manual
- **Preconditions:** App running. A token decoded on the Decode screen and one generated on the Encode screen.
- **Steps:**
  1. On Decode, click Copy next to the header, then paste into a text editor.
  2. Repeat for the payload.
  3. On Encode, generate a token, click Copy, paste into a text editor.
- **Expected:** Each paste matches what is shown on screen exactly. One click per copy.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Medium

### TC-011: Token with a future exp shows as valid

- **Requirement(s):** FR-6
- **Type:** Unit
- **Automated by:** ClaimInspectorTest#validWhenExpInFuture
- **Preconditions:** ClaimInspector built with a fixed clock.
- **Steps:**
  1. Inspect a payload whose exp is 2 hours after the clock time.
- **Expected:** Status VALID with the message "Expires in 2 hours".
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-012: Token past exp shows as expired

- **Requirement(s):** FR-6
- **Type:** Unit
- **Automated by:** ClaimInspectorTest#expiredWhenExpInPast, ClaimInspectorTest#expiredExactlyAtExp
- **Preconditions:** Fixed clock.
- **Steps:**
  1. Inspect a payload whose exp is 90 seconds before the clock time.
  2. Inspect a payload whose exp equals the clock time exactly.
- **Expected:** Status EXPIRED. exp 90 seconds ago reads "Expired 1 minute ago". The exact boundary second also counts as expired.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-013: Token before nbf shows as not valid yet

- **Requirement(s):** FR-6
- **Type:** Unit
- **Automated by:** ClaimInspectorTest#notYetValidBeforeNbf
- **Preconditions:** Fixed clock.
- **Steps:**
  1. Inspect a payload whose nbf is 30 seconds ahead and exp one hour ahead.
- **Expected:** Status NOT_YET_VALID (nbf wins over a future exp), with the message "Starts in 30 seconds".
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-014: Token without exp shows no expiry

- **Requirement(s):** FR-6
- **Type:** Unit
- **Automated by:** ClaimInspectorTest#noExpiryWhenExpMissing, ClaimInspectorTest#distinguishesNullExpiryFromMissingExpiry
- **Preconditions:** Fixed clock.
- **Steps:**
  1. Inspect a payload with no exp.
  2. Inspect a payload with "exp": null.
- **Expected:** Missing exp gives NO_EXPIRY and "No expiry set (exp claim missing)". An exp of null is not treated as missing; it is flagged as an invalid time claim.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-015: exp, iat, and nbf shown as readable local times

- **Requirement(s):** FR-5
- **Type:** Unit
- **Automated by:** ClaimInspectorTest#formatsTimeClaimsInOrder, ClaimInspectorTest#formatsFractionalSecondsOnEitherSideOfTheEpoch
- **Preconditions:** Fixed clock in a known time zone.
- **Steps:**
  1. Inspect a payload with iat 1516239022 and exp one hour later, clock zone UTC.
  2. Inspect iat 0.5 and iat -0.5.
- **Expected:** Claims are listed in the order exp, iat. iat reads "2018-01-18 01:30:22 Z". 0.5 reads 1970-01-01 00:00:00 Z and -0.5 reads 1969-12-31 23:59:59 Z (rounded down).
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-016: Bad time claims are flagged, not crashed on

- **Requirement(s):** FR-5, FR-4
- **Type:** Unit
- **Automated by:** ClaimInspectorTest#reportsNonNumericExpAsInvalid, ClaimInspectorTest#rejectsOutOfRangeAndNonFiniteTimestampsWithoutThrowing, ClaimInspectorTest#handlesDatesOutsideTheDisplayableRange, ClaimInspectorTest#checksAllPresentTimeClaims
- **Preconditions:** Fixed clock.
- **Steps:**
  1. Inspect exp "tomorrow".
  2. Inspect exp set to Long.MAX_VALUE, Long.MIN_VALUE, Double.MAX_VALUE, NaN, and both infinities.
  3. Set each of exp, iat, nbf to "bad date" in turn.
  4. Inspect iat equal to Instant.MAX.
- **Expected:** Every case gives INVALID_TIME_CLAIM with a message starting "Invalid <claim>:" and the claim shown as "Invalid timestamp". No exception is thrown.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-017: Durations read naturally

- **Requirement(s):** FR-6
- **Type:** Unit
- **Automated by:** ClaimInspectorTest#humanDurations
- **Preconditions:** None.
- **Steps:**
  1. Format 1 s, 45 min, 3 days, and -120 s.
- **Expected:** Text is "1 second", "45 minutes", "3 days", and "2 minutes" (negative values use the absolute time).
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Low

### TC-018: Encoder reproduces a known HS256 token

- **Requirement(s):** FR-11
- **Type:** Unit
- **Automated by:** JwtEncoderTest#reproducesKnownHs256Token
- **Preconditions:** Known header, payload, and secret "your-256-bit-secret".
- **Steps:**
  1. Encode the jwt.io header and payload with HS256.
- **Expected:** Output is byte for byte the same as the published example token.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-019: alg and typ are set from the chosen algorithm

- **Requirement(s):** FR-10, NFR-4
- **Type:** Unit
- **Automated by:** JwtEncoderTest#setsAlgAndTypAutomatically, JwtEncoderTest#headerAlgIsOverriddenBySelectedAlgorithm
- **Preconditions:** None.
- **Steps:**
  1. Encode with an empty header and HS384.
  2. Encode with header {"alg":"none","kid":"k1"} and HS512.
- **Expected:** Header gets alg HS384 and typ JWT. In the second case alg becomes HS512 (the header cannot pick its own algorithm) and kid is kept.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-020: All six algorithms sign and verify

- **Requirement(s):** FR-10, FR-11, FR-14
- **Type:** Unit
- **Automated by:** SignatureVerifierTest#roundTripsAllHmacAlgorithms, SignatureVerifierTest#roundTripsRsaWithPublicKey, JwtEncoderTest#signsWithRsaPrivateKey
- **Preconditions:** A fresh 2048 bit RSA key pair as PKCS#8 and SPKI PEM.
- **Steps:**
  1. Sign a token with HS256, HS384, HS512 and verify each with the same secret.
  2. Sign with RS256, RS384, RS512 and verify with the public key.
- **Expected:** All six tokens are compact (three parts) and verify successfully.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-021: Invalid JSON is caught before signing

- **Requirement(s):** FR-9
- **Type:** Unit
- **Automated by:** JwtEncoderTest#rejectsInvalidHeaderJsonWithPosition, JwtEncoderTest#rejectsInvalidPayloadJson
- **Preconditions:** None.
- **Steps:**
  1. Encode with payload {"sub": } and a separate malformed multiline header. Assert the errors identify line 1 and line 2 respectively, and include a numeric column.
- **Expected:** JwtException starting with "Invalid JSON". The message includes the line and column of the error. No token is produced.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-022: Missing or unusable keys are explained

- **Requirement(s):** FR-11
- **Type:** Unit
- **Automated by:** JwtEncoderTest#rejectsMissingKey, JwtEncoderTest#rejectsGarbageRsaKey, KeyParserTest#reportsMissingAndMalformedKeys, KeyParserTest#rejectsNonRsaKeys, KeyParserTest#rejectsPublicKeyWherePrivateKeyIsRequired
- **Preconditions:** None.
- **Steps:**
  1. Encode HS256 with an empty secret.
  2. Encode RS256 with "not a key".
  3. Parse null, empty, garbage, and a PEM with an invalid body as both key types.
  4. Parse an EC key pair, and an RSA public key where a private key is required.
- **Expected:** Each case gives a JwtException with a message saying what kind of key was expected, for example "Enter a secret to sign with".
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-023: PKCS#1 RSA private keys are accepted

- **Requirement(s):** FR-11
- **Type:** Unit
- **Automated by:** KeyParserTest#readsPkcs1KeysAndUsesThemForRsaSigning
- **Preconditions:** A fresh 2048 bit RSA key pair.
- **Steps:**
  1. Convert a fresh key pair to PKCS#1 "RSA PRIVATE KEY" and "RSA PUBLIC KEY" PEM.
  2. Parse both, sign an RS256 token with the private key, verify with the public key.
- **Expected:** Both keys load with the same bytes as the originals and the token verifies.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-024: Numbers in claims are not rounded when signing

- **Requirement(s):** FR-11
- **Type:** Unit
- **Automated by:** JwtEncoderTest#signsWithoutRoundingHeaderOrPayloadNumbers
- **Preconditions:** None.
- **Steps:**
  1. Encode a header with 0.1234567890123456789 and a payload with 12345.67890123456789 and 9223372036854775808.
  2. Decode the parts and verify.
- **Expected:** Header and payload text match the input exactly and the signature verifies.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-025: Open a generated token in the decoder

- **Requirement(s):** FR-12
- **Type:** Manual UI
- **Automated by:** Manual
- **Preconditions:** App running on the Encode screen.
- **Steps:**
  1. Generate any HS256 token.
  2. Click "Open in decoder".
- **Expected:** The Decode screen opens with the new token filled in and already decoded.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Medium

### TC-026: Correct HMAC secret passes

- **Requirement(s):** FR-13
- **Type:** Unit
- **Automated by:** SignatureVerifierTest#acceptsCorrectHmacSecret
- **Preconditions:** Example token and its secret.
- **Steps:**
  1. Verify the example token with "your-256-bit-secret".
- **Expected:** Result is valid with the message "Signature verified (HS256)".
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-027: Wrong HMAC secret fails with a reason

- **Requirement(s):** FR-13, FR-15
- **Type:** Unit
- **Automated by:** SignatureVerifierTest#rejectsWrongHmacSecret
- **Preconditions:** Example token.
- **Steps:**
  1. Verify with "wrong-secret".
- **Expected:** Result is invalid and the message starts with "Signature does not match".
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-028: Edited payload is detected

- **Requirement(s):** FR-13, NFR-4
- **Type:** Unit
- **Automated by:** SignatureVerifierTest#detectsTamperedPayload, SignatureVerifierTest#verifiesOriginalBytesWithoutReformattingJson
- **Preconditions:** Example token.
- **Steps:**
  1. Replace the payload with one where name is "Admin", keep the old signature, verify.
  2. Sign a header and payload that contain extra spaces, newlines, and <tag>&é directly with HMAC256, then verify.
- **Expected:** Tampered token fails. The untouched token with odd spacing still passes.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-029: RSA token checked against a public key

- **Requirement(s):** FR-14
- **Type:** Unit
- **Automated by:** SignatureVerifierTest#roundTripsRsaWithPublicKey, SignatureVerifierTest#rejectsRsaTokenWithOtherPublicKey
- **Preconditions:** Two different RSA key pairs.
- **Steps:**
  1. Sign with key pair A and verify with public key A.
  2. Verify the same token with public key B.
- **Expected:** Public key A passes. Public key B fails.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-030: alg "none" and unsupported algorithms are refused

- **Requirement(s):** FR-15, NFR-4
- **Type:** Unit
- **Automated by:** SignatureVerifierTest#refusesAlgNone, SignatureVerifierTest#reportsUnsupportedAlgorithm, SignatureVerifierTest#reportsMissingAlgorithmInsteadOfUnsignedToken
- **Preconditions:** None.
- **Steps:**
  1. Verify a token with alg "none" and the key "anything".
  2. Verify a token with alg ES256.
  3. Verify tokens with header {} and {"alg":null}.
- **Expected:** All are invalid. Messages mention "none", read "Unsupported algorithm: ES256", and say the header has no alg value.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-031: Missing key and broken token are explained

- **Requirement(s):** FR-15, FR-4
- **Type:** Unit
- **Automated by:** SignatureVerifierTest#explainsMissingKey, SignatureVerifierTest#reportsMalformedTokenInsteadOfThrowing
- **Preconditions:** None.
- **Steps:**
  1. Verify an HS256 token with an empty key.
  2. Verify the text "not-a-token".
- **Expected:** "Enter the secret that was used to sign this token", and an invalid result for the broken token. No exception reaches the caller.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-032: Signature check is separate from expiry

- **Requirement(s):** FR-15
- **Type:** Unit
- **Automated by:** SignatureVerifierTest#checksSignatureIndependentlyOfTimeClaims, SignatureVerifierTest#allowsInspectingMalformedDateClaimsButReportsVerificationError
- **Preconditions:** None.
- **Steps:**
  1. Verify a correctly signed token with exp in 1970 and nbf, iat in 2100.
  2. Sign a token whose exp is "not a date", decode it, then verify it.
- **Expected:** The first token verifies (expiry is shown on the Decode screen, not by the verifier). The string exp still decodes for inspection, and verification returns an invalid result starting "Could not verify:" instead of crashing.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-033: Send the decoded token to the Verify screen

- **Requirement(s):** FR-16
- **Type:** Manual UI
- **Automated by:** Manual
- **Preconditions:** App running. A token decoded on the Decode screen.
- **Steps:**
  1. Click "Verify signature" on the Decode screen.
- **Expected:** The Verify screen opens with the same token in its input box.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Medium

### TC-034: Save a token with a label

- **Requirement(s):** FR-17
- **Type:** Unit
- **Automated by:** InMemoryTokenRepositoryTest#savesAndFindsToken, InMemoryTokenRepositoryTest#blankLabelBecomesUntitled, InMemoryTokenRepositoryTest#rejectsEmptyToken
- **Preconditions:** Empty repository.
- **Steps:**
  1. Save "  Login token " with "abc.def.ghi ".
  2. Save with a blank label.
  3. Save a blank token.
- **Expected:** Label and token are trimmed and listed. Blank label becomes "Untitled token". Blank token is rejected.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-035: Find saved tokens by label, newest first

- **Requirement(s):** FR-18
- **Type:** Unit
- **Automated by:** InMemoryTokenRepositoryTest#searchMatchesLabelIgnoringCase, FileTokenRepositoryTest#keepsSaveTimeAndOrderAfterReload
- **Preconditions:** Repository with "Staging admin" and "Prod reader".
- **Steps:**
  1. Search "ADMIN".
  2. Search "".
  3. Save two tokens a day apart, reopen the file, list them.
- **Expected:** Search is case insensitive and returns one hit; empty search returns all. List is newest first and save times are kept.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-036: Rename and delete a saved token

- **Requirement(s):** FR-19
- **Type:** Unit
- **Automated by:** InMemoryTokenRepositoryTest#renamesAndDeletes
- **Preconditions:** One saved token.
- **Steps:**
  1. Rename to "new".
  2. Rename to " ".
  3. Delete it twice.
- **Expected:** Rename works, blank rename returns false, first delete returns true, second returns false, list is empty.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Low

### TC-037: Sample tokens decode and verify

- **Requirement(s):** FR-17
- **Type:** Integration
- **Automated by:** InMemoryTokenRepositoryTest#sampleDataDecodesAndVerifiesWithDemoSecret
- **Preconditions:** Repository built with sample data.
- **Steps:**
  1. Decode each of the 3 sample tokens.
  2. Verify each with "jwtoffline-demo-secret".
- **Expected:** Three tokens, all decode, all verify. Checks repository, decoder, and verifier together.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Low

### TC-038: Workspace table and opening a saved token

- **Requirement(s):** FR-18, FR-20
- **Type:** Manual UI
- **Automated by:** Manual
- **Preconditions:** App running with the sample tokens in the workspace.
- **Steps:**
  1. Open the Workspace screen and check the columns.
  2. Type "pay" in the filter box.
  3. Double click the remaining row.
  4. Go back, select a row, click "Open in decoder".
- **Expected:** Columns show label, algorithm, status, and save time. Filter leaves only "Payments service (HS512)". Both double click and the button open the token on the Decode screen.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Medium

### TC-039: Recent history on the Home screen

- **Requirement(s):** FR-21
- **Type:** Unit
- **Automated by:** TokenHistoryTest#newestFirstWithoutDuplicates, TokenHistoryTest#deduplicatesBearerAndRawTokens, TokenHistoryTest#dropsOldestPastLimit, TokenHistoryTest#ignoresBlankTokens, TokenHistoryTest#limitMustBePositive
- **Preconditions:** Empty TokenHistory.
- **Steps:**
  1. Add a, b, a.
  2. Add "  Bearer a.b.c", "a.b.c", and "  bEaReR   a.b.c  ".
  3. With limit 2, add 1, 2, 3.
  4. Add a blank token and null.
  5. Create a history with limit 0.
- **Expected:** Order is a, b with no duplicate. All Bearer forms count as one token. With limit 2 the list is 3, 2. Blank and null are ignored. Limit 0 is rejected. The app uses the default limit of 20.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Low

### TC-040: Workspace survives closing and reopening

- **Requirement(s):** FR-22
- **Type:** Unit
- **Automated by:** FileTokenRepositoryTest#savedTokensSurviveReopening, FileTokenRepositoryTest#renameAndDeleteArePersisted, FileTokenRepositoryTest#searchWorksAfterReload
- **Preconditions:** Temporary folder from JUnit @TempDir.
- **Steps:**
  1. Save a token, then open a new FileTokenRepository on the same file.
  2. Save two, rename one, delete the other, reopen.
  3. Save two, reopen, search.
- **Expected:** Reopened repository has the same tokens with the same ids, labels, and times. Rename and delete are kept. Search works on reloaded data.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-041: First launch creates the workspace file

- **Requirement(s):** FR-22
- **Type:** Unit
- **Automated by:** FileTokenRepositoryTest#firstLaunchCreatesFileWithSampleTokens, FileTokenRepositoryTest#missingFileWithoutSeedingStartsEmpty
- **Preconditions:** File and its folder do not exist.
- **Steps:**
  1. Open with sample seeding on.
  2. Open with sample seeding off.
- **Expected:** With seeding, the folder and file are created and hold the 3 sample tokens. Without seeding, the workspace is empty and no file is written until the first save.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-042: Damaged workspace file does not stop the app

- **Requirement(s):** FR-22, NFR-5
- **Type:** Unit
- **Automated by:** FileTokenRepositoryTest#corruptFileStartsEmptyAndKeepsBackup, FileTokenRepositoryTest#entryWithMissingFieldsIsTreatedAsCorrupt
- **Preconditions:** workspace.json contains "{ this is not json" or an entry with no token.
- **Steps:**
  1. Open the repository on the damaged file.
- **Expected:** Workspace starts empty, a warning is available, and the old file is copied to workspace.json.corrupt so nothing is lost.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-043: Saved tokens are there after restarting the app

- **Requirement(s):** FR-22
- **Type:** Manual UI
- **Automated by:** Manual
- **Preconditions:** App installed. Delete ~/.jwtoffline/workspace.json before starting.
- **Steps:**
  1. Start the app and open Workspace (3 sample tokens).
  2. Decode any token, save it as "Restart test".
  3. Close the app fully and start it again.
  4. Open Workspace.
- **Expected:** "Restart test" is listed with the 3 samples. Renames and deletes made before closing are also kept.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** High

### TC-044: Side navigation keeps each screen's input

- **Requirement(s):** FR-23
- **Type:** Manual UI
- **Automated by:** Manual
- **Preconditions:** App running.
- **Steps:**
  1. Paste a token on Decode.
  2. Switch to Encode, type a payload.
  3. Switch to Verify, then back to Decode and Encode.
- **Expected:** One window. Every screen is reachable from the side menu, and the token and payload typed earlier are still there.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** High

### TC-045: No networking code in the app

- **Requirement(s):** NFR-1
- **Type:** Static
- **Automated by:** ArchitectureTest#noSourceFileUsesNetworking, ArchitectureTest#sourcesAreFound
- **Preconditions:** Tests run from the project folder.
- **Steps:**
  1. Scan every .java file under src/main/java for java.net, HttpURLConnection, and Socket usage.
- **Expected:** No file matches. The guard test confirms the scan actually found the source files.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-046: Secrets are never written to disk

- **Requirement(s):** NFR-1
- **Type:** Unit
- **Automated by:** FileTokenRepositoryTest#fileStoresOnlyTokensAndLabels
- **Preconditions:** Temporary folder.
- **Steps:**
  1. Save a token signed with the demo secret.
  2. Read workspace.json as text.
- **Expected:** The file holds the label and token but not the secret.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-047: App works with the network turned off

- **Requirement(s):** NFR-1
- **Type:** Manual
- **Automated by:** Manual
- **Preconditions:** Wi-Fi off and cable unplugged.
- **Steps:**
  1. Start the app.
  2. Decode, encode, verify, and save a token.
- **Expected:** Every feature works with no error or delay.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Medium

### TC-048: Decode an 8 KB token in under 100 ms

- **Requirement(s):** NFR-2
- **Type:** Performance
- **Automated by:** PerformanceTest#decodes8kbTokenUnder100ms
- **Preconditions:** An HS256 token of about 8 KB built in @BeforeAll.
- **Steps:**
  1. Warm up with 5 decodes.
  2. Time 21 decodes and take the median.
- **Expected:** Median is under 100 ms.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-049: Verify an 8 KB token in under 100 ms

- **Requirement(s):** NFR-2
- **Type:** Performance
- **Automated by:** PerformanceTest#verifiesHmac8kbTokenUnder100ms, PerformanceTest#verifiesRsa8kbTokenUnder100ms
- **Preconditions:** 8 KB HS256 and RS256 tokens and their keys.
- **Steps:**
  1. Warm up 5 times.
  2. Time 21 verifications of each and take the median.
- **Expected:** Both verify as valid and both medians are under 100 ms.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-050: App starts in under 3 seconds

- **Requirement(s):** NFR-2
- **Type:** Manual
- **Automated by:** Manual
- **Preconditions:** Typical student laptop, app built with mvn package.
- **Steps:**
  1. Start a stopwatch and launch the app.
  2. Stop when the Home screen is usable. Repeat 3 times.
- **Expected:** Every start is under 3 seconds.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Medium

### TC-051: New user decodes a token within 30 seconds

- **Requirement(s):** NFR-3
- **Type:** Usability
- **Automated by:** Manual
- **Preconditions:** Three classmates who have not used the app. A sample token on the clipboard.
- **Steps:**
  1. Ask each person to "find out who this token belongs to" with no instructions.
  2. Time until they read the sub claim. Note anything confusing.
- **Expected:** All three succeed in 30 seconds or less.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Medium

### TC-052: Random input never crashes the logic

- **Requirement(s):** NFR-5, FR-4
- **Type:** Robustness
- **Automated by:** RobustnessTest#decoderOnlyThrowsJwtExceptionForRandomInput, RobustnessTest#encoderOnlyThrowsJwtExceptionForRandomJson, RobustnessTest#verifierNeverThrowsAndNeverPassesGarbage
- **Preconditions:** Seeded random generators so every run uses the same inputs.
- **Steps:**
  1. Send 2,000 random strings and random three part tokens to the decoder.
  2. Send 1,000 random header and payload pairs to the encoder.
  3. Send 1,000 random tokens and keys to the verifier.
- **Expected:** Decoder and encoder only ever throw JwtException, which every screen catches and shows. Verifier never throws and never reports a random token as valid.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-053: Number with a huge exponent (regression for D-01)

- **Requirement(s):** NFR-5, FR-4
- **Type:** Robustness
- **Automated by:** RobustnessTest#hugeExponentIsReportedAsInvalidJson
- **Preconditions:** None.
- **Steps:**
  1. Parse {"exp":1e9999999999}.
  2. Decode and verify a token whose payload is that JSON.
- **Expected:** A JwtException with a readable message. Before the fix this threw NumberFormatException, which the screens do not catch.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** High

### TC-054: Build and tests pass on Linux with JDK 17

- **Requirement(s):** NFR-6
- **Type:** CI
- **Automated by:** .github/workflows/build.yml
- **Preconditions:** Push to a tracked branch.
- **Steps:**
  1. GitHub Actions runs mvn -B verify on ubuntu-latest with Temurin 17.
- **Expected:** Build succeeds and the JaCoCo report is uploaded.
- **Actual:** Runs #1 to #15 all succeeded (Oct 10 to 11, 2026).
- **Status:** Pass
- **Priority:** Medium

### TC-055: App runs on Windows and macOS

- **Requirement(s):** NFR-6
- **Type:** Manual
- **Automated by:** Manual
- **Preconditions:** One Windows and one macOS machine with JDK 17 or newer.
- **Steps:**
  1. Clone the repo and run mvn javafx:run.
  2. Decode, encode, verify, and save a token.
- **Expected:** App starts and every screen works on both systems.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Medium

### TC-056: Logic packages have no UI code

- **Requirement(s):** NFR-7
- **Type:** Static
- **Automated by:** ArchitectureTest#corePackageHasNoUiCode, ArchitectureTest#workspacePackageHasNoUiCode
- **Preconditions:** Tests run from the project folder.
- **Steps:**
  1. Scan core and workspace sources for javafx imports.
- **Expected:** No file in core or workspace imports JavaFX.
- **Actual:** Passed locally on Windows/JDK 17, November 8, 2026 (mvn -o verify; 94 tests, zero failures, errors, or skips).
- **Status:** Pass
- **Priority:** Medium

### TC-057: Line coverage of core and workspace is at least 80%

- **Requirement(s):** NFR-7
- **Type:** Coverage
- **Automated by:** JaCoCo (mvn verify)
- **Preconditions:** Full test suite runs.
- **Steps:**
  1. Run mvn verify.
  2. Open target/site/jacoco/index.html and read line coverage for the core and workspace packages.
- **Expected:** Both packages are at 80% line coverage or higher.
- **Actual:** Local JaCoCo report, November 8, 2026: core 215/220 lines (97.7%); workspace 135/144 lines (93.8%). Both exceed 80%. UI coverage is 0%; manual checks remain outstanding.
- **Status:** Pass
- **Priority:** Medium

### TC-058: Keyboard only use and readable status

- **Requirement(s):** NFR-8
- **Type:** Manual
- **Automated by:** Manual
- **Preconditions:** App running. Mouse unplugged.
- **Steps:**
  1. Use Tab, Shift+Tab, Enter, and Space to decode, encode, verify, and save.
  2. Check text contrast of banners with a contrast checker.
- **Expected:** Every action is reachable by keyboard. Text meets WCAG AA. Status banners use words, not only color.
- **Actual:** Not run yet. Scheduled for the Week 10 manual test session (before Deliverable 2.2).
- **Status:** Not run
- **Priority:** Low

### TC-059: Failed workspace writes preserve state and permit retry

- **Requirement(s):** FR-22, NFR-5
- **Type:** Unit
- **Automated by:** FileTokenRepositoryTest#failedWritesPreserveTokensAndAllowRetry
- **Steps:** Save a token, block the storage directory, attempt save/rename/delete, restore access, and retry.
- **Expected:** Failed writes raise JwtException, preserve the original tokens and file, and allow a later successful retry.
- **Actual:** Passed locally on November 8, 2026 (mvn -o verify). UI error banners were not manually tested.
- **Status:** Pass
- **Priority:** High

### TC-060: Initial workspace write failure allows startup

- **Requirement(s):** FR-22, NFR-5
- **Type:** Unit
- **Automated by:** FileTokenRepositoryTest#initialWriteFailureProducesWarningInsteadOfStoppingStartup
- **Steps:** Open a first-launch workspace whose storage directory is blocked by a file.
- **Expected:** Construction succeeds with an empty repository and warning instead of an uncaught exception.
- **Actual:** Passed locally on November 8, 2026 (mvn -o verify). Warning rendering remains a manual UI check.
- **Status:** Pass
- **Priority:** High