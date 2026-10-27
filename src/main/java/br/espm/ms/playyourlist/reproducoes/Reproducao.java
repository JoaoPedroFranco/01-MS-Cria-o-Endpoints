package br.espm.ms.playyourlist.reproducoes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "reproducoes")
public class Reproducao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "playlistid")
    private Integer playlistId;

    @Column(name = "datahora")
    private LocalDateTime dataHora;

    public Reproducao() {
    }

    public Reproducao(Integer playlistId, LocalDateTime dataHora) {
        this.playlistId = playlistId;
        this.dataHora = dataHora;
    }

    public Integer getId() { return id; }
    public Integer getPlaylistId() { return playlistId; }
    public LocalDateTime getDataHora() { return dataHora; }
}
