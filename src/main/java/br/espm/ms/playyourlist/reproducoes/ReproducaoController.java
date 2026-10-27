package br.espm.ms.playyourlist.reproducoes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/** Microservico "reproducoes": controla quantas vezes cada playlist foi executada. */
@RestController
@RequestMapping("/reproducao")
@Tag(name = "reproducoes", description = "Estatisticas de reproducao das playlists")
public class ReproducaoController {

    private final ReproducaoRepository repository;

    public ReproducaoController(ReproducaoRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Operation(summary = "Registrar uma reproducao da playlist (datahora = agora)")
    public ResponseEntity<Reproducao> registrar(@Valid @RequestBody ReproducaoRequest req) {
        Reproducao r = repository.save(new Reproducao(req.playlistId(), LocalDateTime.now()));
        return ResponseEntity.status(HttpStatus.CREATED).body(r);
    }

    @GetMapping("/{playlistid}")
    @Operation(summary = "Listar todas as reproducoes de uma playlist")
    public List<Reproducao> listar(@PathVariable Integer playlistid) {
        return repository.findByPlaylistIdOrderByDataHora(playlistid);
    }

    @GetMapping("/total/{playlistid}")
    @Operation(summary = "Total de vezes que uma playlist foi executada")
    public TotalReproducoesResponse total(@PathVariable Integer playlistid) {
        return new TotalReproducoesResponse(playlistid, repository.countByPlaylistId(playlistid));
    }
}
