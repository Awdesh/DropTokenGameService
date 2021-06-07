package com._98point6.dt.dtbackend.repository;

import com._98point6.dt.dtbackend.domain.Game;
import com._98point6.dt.dtbackend.domain.Move;
import com._98point6.dt.dtbackend.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovesRepository extends JpaRepository<Move, Integer> {

    List<Move> findByGame(Game game);

}
