package br.espm.ms.playyourlist.playlists;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistMusicaRepository extends JpaRepository<PlaylistMusica, Integer> {

    List<PlaylistMusica> findByPlaylistIdOrderById(Integer playlistId);

    boolean existsByPlaylistIdAndMusicaId(Integer playlistId, Integer musicaId);

    long deleteByPlaylistIdAndMusicaId(Integer playlistId, Integer musicaId);

    long deleteByPlaylistId(Integer playlistId);
}
