package br.espm.ms.playyourlist.musicas;

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

/** Microservico "musicas": CRUD das musicas disponiveis. */
@RestController
@RequestMapping("/musicas")
@Tag(name = "musicas", description = "Mantem as musicas disponiveis (CRUD)")
public class MusicaController {

    private final MusicaService service;

    public MusicaController(MusicaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova musica")
    public ResponseEntity<Musica> criar(@Valid @RequestBody MusicaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(req));
    }

    @GetMapping
    @Operation(summary = "Listar todas as musicas")
    public List<Musica> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma musica pelo ID")
    public Musica buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma musica")
    public Musica atualizar(@PathVariable Integer id, @Valid @RequestBody MusicaRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma musica")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
