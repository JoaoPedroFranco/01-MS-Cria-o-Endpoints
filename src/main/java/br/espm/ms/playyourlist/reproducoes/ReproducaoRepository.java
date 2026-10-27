package br.espm.ms.playyourlist.reproducoes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReproducaoRepository extends JpaRepository<Reproducao, Integer> {

    List<Reproducao> findByPlaylistIdOrderByDataHora(Integer playlistId);

    long countByPlaylistId(Integer playlistId);
}
