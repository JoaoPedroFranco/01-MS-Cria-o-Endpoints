package br.espm.ms.playyourlist.api;

import br.espm.ms.playyourlist.api.client.MusicaClient;
import br.espm.ms.playyourlist.api.client.PlaylistClient;
import br.espm.ms.playyourlist.api.client.ReproducaoClient;
import br.espm.ms.playyourlist.api.dto.ExecucaoResponse;
import br.espm.ms.playyourlist.api.dto.MusicaDTO;
import br.espm.ms.playyourlist.api.dto.NovaReproducaoDTO;
import br.espm.ms.playyourlist.api.dto.PlaylistDTO;
import br.espm.ms.playyourlist.api.dto.ReproducaoDTO;
import br.espm.ms.playyourlist.api.dto.TotalReproducoesDTO;
import br.espm.ms.playyourlist.comum.ConflitoException;
import br.espm.ms.playyourlist.comum.NaoEncontradoException;
import feign.FeignException;
import org.springframework.stereotype.Service;

/** Orquestra os microservicos musicas, playlists e reproducoes via Open Feign. */
@Service
public class ApiService {

    private final MusicaClient musicas;
    private final PlaylistClient playlists;
    private final ReproducaoClient reproducoes;

    public ApiService(MusicaClient musicas, PlaylistClient playlists, ReproducaoClient reproducoes) {
        this.musicas = musicas;
        this.playlists = playlists;
        this.reproducoes = reproducoes;
    }

    public String adicionarMusica(Integer playlistId, Integer musicaId) {
        MusicaDTO musica = buscarMusica(musicaId);          // valida a musica
        PlaylistDTO playlist = buscarPlaylist(playlistId);  // valida a playlist
        try {
            playlists.adicionarMusica(playlistId, musicaId);
        } catch (FeignException.Conflict e) {
            throw new ConflitoException("Musica " + musica.titulo()
                    + " ja esta na playlist " + playlist.nome());
        }
        return "Música " + musica.titulo() + " adicionada com sucesso à playlist " + playlist.nome();
    }

    public ExecucaoResponse executar(Integer playlistId) {
        PlaylistDTO playlist = buscarPlaylist(playlistId);
        ReproducaoDTO reproducao = reproducoes.registrar(new NovaReproducaoDTO(playlistId));
        TotalReproducoesDTO total = reproducoes.total(playlistId);
        return new ExecucaoResponse(
                "Playlist " + playlist.nome() + " executada com sucesso",
                playlistId, reproducao.dataHora(), total.total());
    }

    private MusicaDTO buscarMusica(Integer id) {
        try {
            return musicas.buscar(id);
        } catch (FeignException.NotFound e) {
            throw new NaoEncontradoException("Musica " + id + " nao encontrada");
        }
    }

    private PlaylistDTO buscarPlaylist(Integer id) {
        try {
            return playlists.buscar(id);
        } catch (FeignException.NotFound e) {
            throw new NaoEncontradoException("Playlist " + id + " nao encontrada");
        }
    }
}
