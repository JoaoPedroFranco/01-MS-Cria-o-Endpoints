package br.espm.ms.playyourlist.playlists;

import br.espm.ms.playyourlist.comum.ConflitoException;
import br.espm.ms.playyourlist.comum.NaoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlaylistService {

    private final PlaylistRepository playlists;
    private final PlaylistMusicaRepository playlistMusicas;

    public PlaylistService(PlaylistRepository playlists, PlaylistMusicaRepository playlistMusicas) {
        this.playlists = playlists;
        this.playlistMusicas = playlistMusicas;
    }

    public List<Playlist> listar() {
        return playlists.findAll();
    }

    public Playlist buscar(Integer id) {
        return playlists.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Playlist " + id + " nao encontrada"));
    }

    @Transactional
    public Playlist criar(PlaylistRequest req) {
        Playlist p = new Playlist();
        p.setNome(req.nome());
        p.setDescricao(req.descricao());
        return playlists.save(p);
    }

    @Transactional
    public Playlist atualizar(Integer id, PlaylistRequest req) {
        Playlist p = buscar(id);
        p.setNome(req.nome());
        p.setDescricao(req.descricao());
        return playlists.save(p);
    }

    /** Exclui a playlist e suas musicas associadas. */
    @Transactional
    public void excluir(Integer id) {
        Playlist p = buscar(id);
        playlistMusicas.deleteByPlaylistId(id);
        playlists.delete(p);
    }

    @Transactional
    public PlaylistMusica adicionarMusica(Integer playlistId, Integer musicaId) {
        buscar(playlistId);
        if (playlistMusicas.existsByPlaylistIdAndMusicaId(playlistId, musicaId)) {
            throw new ConflitoException("Musica " + musicaId + " ja esta na playlist " + playlistId);
        }
        try {
            return playlistMusicas.saveAndFlush(new PlaylistMusica(playlistId, musicaId));
        } catch (DataIntegrityViolationException e) {
            // chave estrangeira musicaid -> musicas(id)
            throw new NaoEncontradoException("Musica " + musicaId + " nao encontrada");
        }
    }

    @Transactional
    public void removerMusica(Integer playlistId, Integer musicaId) {
        buscar(playlistId);
        if (playlistMusicas.deleteByPlaylistIdAndMusicaId(playlistId, musicaId) == 0) {
            throw new NaoEncontradoException("Musica " + musicaId + " nao esta na playlist " + playlistId);
        }
    }

    /** Somente os ids das musicas da playlist. */
    public List<Integer> listarMusicas(Integer playlistId) {
        buscar(playlistId);
        return playlistMusicas.findByPlaylistIdOrderById(playlistId).stream()
                .map(PlaylistMusica::getMusicaId)
                .toList();
    }
}
