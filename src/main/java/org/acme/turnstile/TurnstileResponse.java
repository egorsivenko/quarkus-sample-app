package org.acme.turnstile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public record TurnstileResponse(
        boolean success,
        String hostname,
        @JsonProperty("challenge_ts")
        String challengeTimestamp,
        @JsonProperty("error-codes")
        String[] errorCodes
) {
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TurnstileResponse rsp)) return false;
        return success == rsp.success
                && Objects.equals(hostname, rsp.hostname)
                && Objects.equals(challengeTimestamp, rsp.challengeTimestamp)
                && Objects.deepEquals(errorCodes, rsp.errorCodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, hostname, challengeTimestamp, Arrays.hashCode(errorCodes));
    }

    @Override
    public String toString() {
        return "TurnstileResponse{" +
                "success=" + success +
                ", hostname='" + hostname + '\'' +
                ", challengeTimestamp='" + challengeTimestamp + '\'' +
                ", errorCodes=" + Arrays.toString(errorCodes) +
                '}';
    }
}
