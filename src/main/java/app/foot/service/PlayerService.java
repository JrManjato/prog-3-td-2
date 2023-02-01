package app.foot.service;

import app.foot.exception.BadRequestException;
import app.foot.model.Player;
import app.foot.repository.PlayerRepository;
import app.foot.repository.entity.PlayerEntity;
import app.foot.repository.mapper.PlayerMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;
    private final PlayerMapper mapper;

    public List<Player> getPlayers() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Player> addPlayer(List<app.foot.controller.rest.model.Player> players, String teamName){
        List<PlayerEntity> mappedPlayer = players.stream()
                        .map(player -> mapper.toDomain(player, teamName))
                        .toList();
        return repository.saveAll(mappedPlayer).stream()
                .map(mapper::toDomain)
                .toList();
    }

    public List<Player> editPlayer (List<app.foot.controller.rest.model.Player> players){
        List<PlayerEntity> mappedPlayer = players.stream().map(player -> {
            if(player.getId() == null){
                throw new BadRequestException("All player need id");
            }
            Optional<PlayerEntity> tempPlayerEntity = repository.findById(player.getId());
            if(tempPlayerEntity.isEmpty()){
                throw new BadRequestException("The Player#" + player.getId() + " does not exits.");
            }
            PlayerEntity playerEntity = tempPlayerEntity.get();
            playerEntity.setName(player.getName() != null ? player.getName() : playerEntity.getName());
            playerEntity.setGuardian(player.getIsGuardian() != null ? player.getIsGuardian() : playerEntity.isGuardian());
            return playerEntity;
        }).toList();

        return repository.saveAll(mappedPlayer).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
