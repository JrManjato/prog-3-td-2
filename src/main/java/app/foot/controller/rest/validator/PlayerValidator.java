package app.foot.controller.rest.validator;

import app.foot.controller.rest.model.Player;
import app.foot.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PlayerValidator implements Consumer<Player> {
    @Override
    public void accept(Player player) {
        StringBuilder exceptionBuilder = new StringBuilder();
        if (player.getName() == null || player.getName().isEmpty() || player.getName().isBlank()) {
            exceptionBuilder.append("Player name is mandatory.");
        }
        if(player.getIsGuardian() == null){
            exceptionBuilder.append("Player isGuardian is mandatory.");
        }
        if (!exceptionBuilder.isEmpty()) {
            throw new BadRequestException(exceptionBuilder.toString());
        }
    }
}
