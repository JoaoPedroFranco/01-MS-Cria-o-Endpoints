package br.espm.ms.playyourlist.api.client;

import br.espm.ms.playyourlist.api.dto.PlaylistDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/** Cliente Open Feign do microservico "playlists". */
@FeignClient(name = "playlists", url = "${app.servicos.url}")
public interface PlaylistClient {

    @GetMapping("/playlists/{playlistid}")
    PlaylistDTO buscar(@PathVariable("playlistid") Integer playlistId);

    @PostMapping("/playlists/{playlistid}/musicas/{musicaId}")
    void adicionarMusica(@PathVariable("playlistid") Integer playlistId,
                         @PathVariable("musicaId") Integer musicaId);
}
