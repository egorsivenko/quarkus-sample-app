package pragmasoft.k1teauth.common.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ErrorResponse(String error) {}
