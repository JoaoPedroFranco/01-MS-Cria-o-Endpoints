package br.espm.ms.playyourlist.playlists;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Microservico "playlists": manutencao das playlists e de suas musicas. */
@RestController
@RequestMapping("/playlists")
@Tag(name = "playlists", description = "Manutencao das playlists")
public class PlaylistController {

    private final PlaylistService service;

    public PlaylistController(PlaylistService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova playlist")
    public ResponseEntity<Playlist> criar(@Valid @RequestBody PlaylistRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(req));
    }

    @GetMapping
    @Operation(summary = "Listar todas as playlists")
    public List<Playlist> listar() {
        return service.listar();
    }

    @GetMapping("/{playlistid}")
    @Operation(summary = "Buscar uma playlist pelo playlistid")
    public Playlist buscar(@PathVariable Integer playlistid) {
        return service.buscar(playlistid);
    }

    @PutMapping("/{playlistid}")
    @Operation(summary = "Atualizar o nome e a descricao de uma playlist")
    public Playlist atualizar(@PathVariable Integer playlistid, @Valid @RequestBody PlaylistRequest req) {
        return service.atualizar(playlistid, req);
    }

    @DeleteMapping("/{playlistid}")
    @Operation(summary = "Excluir uma playlist e suas musicas associadas")
    public ResponseEntity<Void> excluir(@PathVariable Integer playlistid) {
        service.excluir(playlistid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlistid}/musicas/{musicaId}")
    @Operation(summary = "Adicionar uma musica a playlist")
    public ResponseEntity<PlaylistMusica> adicionarMusica(@PathVariable Integer playlistid,
                                                          @PathVariable Integer musicaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarMusica(playlistid, musicaId));
    }

    @DeleteMapping("/{playlistid}/musicas/{musicaId}")
    @Operation(summary = "Remover uma musica da playlist")
    public ResponseEntity<Void> removerMusica(@PathVariable Integer playlistid, @PathVariable Integer musicaId) {
        service.removerMusica(playlistid, musicaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{playlistid}/musicas")
    @Operation(summary = "Listar os ids das musicas de uma playlist")
    public List<Integer> listarMusicas(@PathVariable Integer playlistid) {
        return service.listarMusicas(playlistid);
    }
}
