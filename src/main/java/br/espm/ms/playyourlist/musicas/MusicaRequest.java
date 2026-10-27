package br.espm.ms.playyourlist.musicas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Corpo do POST/PUT /musicas com as regras de validacao do enunciado. */
public record MusicaRequest(
        @Schema(example = "Hotel California")
        @NotBlank(message = "titulo e obrigatorio e nao pode ser vazio")
        @Size(max = 150, message = "titulo deve ter no maximo 150 caracteres")
        String titulo,

        @Schema(example = "Eagles")
        @NotBlank(message = "artista e obrigatorio e nao pode ser vazio")
        @Size(max = 150, message = "artista deve ter no maximo 150 caracteres")
        String artista,

        @Schema(example = "Hotel California")
        @Size(max = 150, message = "album deve ter no maximo 150 caracteres")
        String album,

        @Schema(example = "391")
        @NotNull(message = "duracao e obrigatoria")
        @Positive(message = "duracao deve ser maior que zero")
        Integer duracao,

        @Schema(example = "Rock")
        @Size(max = 50, message = "genero deve ter no maximo 50 caracteres")
        String genero) {

    void copiarPara(Musica m) {
        m.setTitulo(titulo);
        m.setArtista(artista);
        m.setAlbum(album);
        m.setDuracao(duracao);
        m.setGenero(genero);
    }
}
