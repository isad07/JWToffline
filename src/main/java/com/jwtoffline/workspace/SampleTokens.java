package com.jwtoffline.workspace;

import java.util.List;

/**
 * Sample tokens signed with DEMO_SECRET for testing the app.
 */
public final class SampleTokens {

    public static final String DEMO_SECRET = "jwtoffline-demo-secret";

    public static final String ADMIN_SESSION =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyLTEwNDIiLCJuYW1lIjoiQXZhIFBhdGVsIiwiZW1haWwiOiJhdmFAZXhhbXBsZS5jb20iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3NjcyMjU2MDAsImV4cCI6MTg5MzQ1NjAwMH0.-OtiKPZChJqnsMR5TxwVT1KELjpNbk0eUOSpPM0ZmPU";

    public static final String EXPIRED_QA =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJxYS1ib3QiLCJzY29wZSI6InJlYWQ6b3JkZXJzIiwiaWF0IjoxNTc3ODM2ODAwLCJleHAiOjE1Nzc4NDA0MDB9.XkUXggokhK7yn9Pj9MM7TRXRzgRm4OMELK1CXedb7Jw";

    public static final String SERVICE_HS512 =
            "eyJraWQiOiJkZXYta2V5LTEiLCJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2F1dGgubG9jYWwudGVzdCIsInN1YiI6InN2Yy1wYXltZW50cyIsImF1ZCI6Im9yZGVycy1hcGkiLCJpYXQiOjE3NjcyMjU2MDAsIm5iZiI6MTc2NzIyNTYwMCwiZXhwIjoxOTI0OTkyMDAwfQ.aXTZVaZDIUJKTGJF3_3_Ejut9CO4ojqC1dbGas_vNjzAVmaUYNonI0Bg17q3x0IVM9jhD3YFIhbx9ijSU8HcPg";

    public record SampleToken(String label, String token) {
    }

    public static final List<SampleToken> ALL = List.of(
            new SampleToken("Expired QA bot token", EXPIRED_QA),
            new SampleToken("Payments service (HS512)", SERVICE_HS512),
            new SampleToken("Admin session, staging", ADMIN_SESSION));

    private SampleTokens() {
    }
}
