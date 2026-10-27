package br.espm.ms.playyourlist.api.dto;

import java.time.LocalDateTime;

public record ReproducaoDTO(Integer id, Integer playlistId, LocalDateTime dataHora) {
}
