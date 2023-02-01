package app.foot.controller;

import app.foot.controller.rest.model.Player;
import app.foot.controller.rest.mapper.PlayerRestMapper;
import app.foot.controller.rest.validator.PlayerValidator;
import app.foot.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ListIterator;

@RestController
@AllArgsConstructor
public class PlayerController {
    private final PlayerRestMapper mapper;
    private final PlayerService service;
    private final PlayerValidator playerValidator;

    @GetMapping("/players")
    public List<Player> getPlayers() {
        return service.getPlayers().stream()
                .map(mapper::toRest)
                .toList();
    }
    @PostMapping("/players")
    public List<Player> addPlayers(@RequestBody List<Player> players, @RequestParam String teamName){
        players.forEach(playerValidator);
        return service.addPlayer(players, teamName).stream()
                .map(mapper::toRest)
                .toList();
    }

    @PutMapping("/players")
    public List<Player> editPlayers(@RequestBody List<Player> players){
        return service.editPlayer(players).stream()
                .map(mapper::toRest)
                .toList();
    }
}
