package app.foot.repository.mapper;

import app.foot.exception.BadRequestException;
import app.foot.model.Player;
import app.foot.model.PlayerScorer;
import app.foot.repository.MatchRepository;
import app.foot.repository.PlayerRepository;
import app.foot.repository.TeamRepository;
import app.foot.repository.entity.PlayerEntity;
import app.foot.repository.entity.PlayerScoreEntity;
import app.foot.repository.entity.TeamEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PlayerMapper {
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;


    public Player toDomain(PlayerEntity entity) {
        return Player.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isGuardian(entity.isGuardian())
                .teamName(entity.getTeam().getName())
                .build();
    }

    public PlayerEntity toDomain(app.foot.controller.rest.model.Player player, String teamName) {
        TeamEntity team = teamRepository.findByName(teamName);
        if (team == null) {
            throw new BadRequestException("Team#" + teamName + " does not exist");
        }
        return PlayerEntity.builder()
                .id(player.getId())
                .guardian(player.getIsGuardian())
                .team(team)
                .name(player.getName())
                .build();
    }

    public PlayerScorer toDomain(PlayerScoreEntity entity) {
        return PlayerScorer.builder()
                .player(toDomain(entity.getPlayer()))
                .minute(entity.getMinute())
                .isOwnGoal(entity.isOwnGoal())
                .build();
    }

    public PlayerScoreEntity toEntity(int matchId, PlayerScorer scorer) {
        return PlayerScoreEntity.builder()
                .player(playerRepository.findById(scorer.getPlayer().getId()).get())
                .match(matchRepository.findById(matchId).get())
                .ownGoal(scorer.getIsOwnGoal())
                .minute(scorer.getMinute())
                .build();
    }
}
