package br.espm.ms.playyourlist.api.client;

import br.espm.ms.playyourlist.api.dto.NovaReproducaoDTO;
import br.espm.ms.playyourlist.api.dto.ReproducaoDTO;
import br.espm.ms.playyourlist.api.dto.TotalReproducoesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Cliente Open Feign do microservico "reproducoes". */
@FeignClient(name = "reproducoes", url = "${app.servicos.url}")
public interface ReproducaoClient {

    @PostMapping("/reproducao")
    ReproducaoDTO registrar(@RequestBody NovaReproducaoDTO req);

    @GetMapping("/reproducao/total/{playlistid}")
    TotalReproducoesDTO total(@PathVariable("playlistid") Integer playlistId);
}
