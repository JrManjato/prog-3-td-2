package integration;

import app.foot.FootApi;
import app.foot.controller.rest.model.Exception;
import app.foot.controller.rest.model.Match;
import app.foot.controller.rest.model.PlayerScorer;
import app.foot.controller.rest.model.Team;
import app.foot.controller.rest.model.TeamMatch;
import app.foot.controller.rest.model.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.Invalid_Score_Time;
import static utils.TestUtils.Not_Found_Match_Id;
import static utils.TestUtils.Valid_Score_Time;
import static utils.TestUtils.createPlayer;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
@Slf4j
public class MatchIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();  //Allow 'java.time.Instant' mapping

    @Test
    void read_match_by_id_ok() throws java.lang.Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Match actual = objectMapper.readValue(
                response.getContentAsString(), Match.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedMatch2(), actual);
    }

    @Test
    void read_match_by_id_ko() throws java.lang.Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches/0"))
                .andReturn()
                .getResponse();

        Exception actual = objectMapper.readValue(
                response.getContentAsString(), Exception.class);

        Exception expected = Exception.builder()
                .error(HttpStatus.NOT_FOUND)
                .message("Match#" + Not_Found_Match_Id + " not found.")
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(expected, actual);
    }

    @Test
    void add_goal_to_match_ok() throws java.lang.Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/matches/3/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(playerScorer1())))
                )
                .andReturn()
                .getResponse();

        Match actual = objectMapper.readValue(response.getContentAsString(), Match.class);


        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(3, actual.getId());
        assertTrue(actual.getTeamA().getScorers().contains(playerScorer1()));
    }

    @Test
    void add_goal_to_match_ko() throws java.lang.Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/matches/3/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(playerScorer2())))
                )
                .andReturn()
                .getResponse();

        Exception actual = objectMapper.readValue(response.getContentAsString(), Exception.class);
        Exception expected = Exception.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST)
                .message("Player#" + playerScorer2().getPlayer().getName() + " cannot score after minute 90. ")
                .build();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(expected, actual);
    }

    private static Match expectedMatch2() {
        return Match.builder()
                .id(2)
                .teamA(teamMatchA())
                .teamB(teamMatchB())
                .stadium("S2")
                .datetime(Instant.parse("2023-01-01T14:00:00Z"))
                .build();
    }

    private static TeamMatch teamMatchB() {
        return TeamMatch.builder()
                .team(team3())
                .score(0)
                .scorers(List.of())
                .build();
    }

    private static TeamMatch teamMatchA() {
        return TeamMatch.builder()
                .team(team2())
                .score(2)
                .scorers(List.of(PlayerScorer.builder()
                                .player(player3())
                                .scoreTime(70)
                                .isOG(false)
                                .build(),
                        PlayerScorer.builder()
                                .player(player6())
                                .scoreTime(80)
                                .isOG(true)
                                .build()))
                .build();
    }

    private static Team team3() {
        return Team.builder()
                .id(3)
                .name("E3")
                .build();
    }

    private static Player player6() {
        return Player.builder()
                .id(6)
                .name("J6")
                .isGuardian(false)
                .build();
    }

    private static Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .isGuardian(false)
                .build();
    }

    private static Team team2() {
        return Team.builder()
                .id(2)
                .name("E2")
                .build();
    }

    private PlayerScorer playerScorer1() {
        return PlayerScorer.builder()
                .scoreTime(Valid_Score_Time)
                .player(player1())
                .isOG(false)
                .build();
    }
    Player player1(){ return createPlayer(1, false); }

    private PlayerScorer playerScorer2() {
        return PlayerScorer.builder()
                .scoreTime(Invalid_Score_Time)
                .player(player1())
                .isOG(false)
                .build();
    }
}
