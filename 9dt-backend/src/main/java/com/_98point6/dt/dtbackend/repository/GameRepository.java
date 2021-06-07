package com._98point6.dt.dtbackend.repository;

import com._98point6.dt.dtbackend.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    Game findByGameId(String gameId);
}
