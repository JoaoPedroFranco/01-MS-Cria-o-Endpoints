package br.espm.ms.playyourlist.api.dto;

import java.time.LocalDateTime;

public record ExecucaoResponse(String mensagem, Integer playlistId, LocalDateTime dataHora, long totalExecucoes) {
}
