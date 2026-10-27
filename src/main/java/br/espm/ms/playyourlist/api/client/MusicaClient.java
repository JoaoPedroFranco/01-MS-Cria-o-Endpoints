package br.espm.ms.playyourlist.api.client;

import br.espm.ms.playyourlist.api.dto.MusicaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Cliente Open Feign do microservico "musicas". */
@FeignClient(name = "musicas", url = "${app.servicos.url}")
public interface MusicaClient {

    @GetMapping("/musicas/{id}")
    MusicaDTO buscar(@PathVariable("id") Integer id);
}
