package br.espm.ms.playyourlist.playlists;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Associacao playlist x musica (tabela playlist_musicas). */
@Entity
@Table(name = "playlist_musicas")
public class PlaylistMusica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "playlistid")
    private Integer playlistId;

    @Column(name = "musicaid")
    private Integer musicaId;

    public PlaylistMusica() {
    }

    public PlaylistMusica(Integer playlistId, Integer musicaId) {
        this.playlistId = playlistId;
        this.musicaId = musicaId;
    }

    public Integer getId() { return id; }
    public Integer getPlaylistId() { return playlistId; }
    public Integer getMusicaId() { return musicaId; }
}
