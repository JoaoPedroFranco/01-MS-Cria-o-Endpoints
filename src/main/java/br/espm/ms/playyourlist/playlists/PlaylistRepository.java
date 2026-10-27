package br.espm.ms.playyourlist.playlists;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
}
