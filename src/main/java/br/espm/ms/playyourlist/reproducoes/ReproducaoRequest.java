package br.espm.ms.playyourlist.reproducoes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReproducaoRequest(
        @Schema(example = "1")
        @NotNull(message = "playlistId e obrigatorio")
        @Positive(message = "playlistId deve ser maior que zero")
        Integer playlistId) {
}
