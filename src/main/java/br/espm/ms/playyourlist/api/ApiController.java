package br.espm.ms.playyourlist.api;

import br.espm.ms.playyourlist.api.dto.ExecucaoResponse;
import br.espm.ms.playyourlist.comum.MensagemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Microservico "api": orquestrador. */
@RestController
@RequestMapping("/api")
@Tag(name = "api", description = "Orquestra os servicos via Open Feign")
public class ApiController {

    private final ApiService service;

    public ApiController(ApiService service) {
        this.service = service;
    }

    @PostMapping("/adicionar/{playlistId}/musicas/{musicaId}")
    @Operation(summary = "Adicionar uma musica a uma playlist, validando musica e playlist via Open Feign")
    public MensagemResponse adicionar(@PathVariable Integer playlistId, @PathVariable Integer musicaId) {
        return new MensagemResponse(service.adicionarMusica(playlistId, musicaId));
    }

    @PutMapping("/executar/{playlistId}")
    @Operation(summary = "Executar uma playlist, registrando a execucao em POST /reproducao via Open Feign")
    public ExecucaoResponse executar(@PathVariable Integer playlistId) {
        return service.executar(playlistId);
    }
}
