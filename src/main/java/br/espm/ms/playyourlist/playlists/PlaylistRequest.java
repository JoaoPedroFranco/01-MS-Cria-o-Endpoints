package br.espm.ms.playyourlist.playlists;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo do POST/PUT /playlists com as regras de validacao do enunciado. */
public record PlaylistRequest(
        @Schema(example = "Foco Total")
        @NotBlank(message = "nome e obrigatorio e nao pode ser vazio")
        @Size(max = 100, message = "nome deve ter no maximo 100 caracteres")
        String nome,

        @Schema(example = "Musicas para manter a produtividade no trabalho")
        @Size(max = 255, message = "descricao deve ter no maximo 255 caracteres")
        String descricao) {
}
