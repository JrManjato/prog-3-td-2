package integration;

import app.foot.FootApi;
import app.foot.controller.rest.model.Exception;
import app.foot.controller.rest.model.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static utils.TestUtils.Player_new_name;
import static utils.TestUtils.UnknownTeamEntityName;
import static utils.TestUtils.TeamEntityName1;
import static utils.TestUtils.createPlayer;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
@Slf4j
public class PlayerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Player player1() { return createPlayer(1, false); }
    Player player2() { return createPlayer(2, false); }
    Player player3() { return createPlayer(3, false); }
    Player player4() { return createPlayer(4, false); }
    Player player5() { return createPlayer(5, false); }

    Player player6(){
        return Player.builder()
                .id(4)
                .isGuardian(true)
                .build();
    }

    Player player7(){
        return Player.builder()
                .id(5)
                .name(Player_new_name)
                .build();
    }

    Player player8() {
        return Player.builder()
            .name("New Player4")
            .isGuardian(false)
            .build();
    }


    @Test
    void read_players_ok() throws java.lang.Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/players"))
                .andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(actual.containsAll(List.of(
                player1(),
                player2(),
                player3())));
    }

    @Test
    void add_players_ok() throws java.lang.Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post("/players")
                        .param("teamName", TeamEntityName1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(player8())))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        Player actual = convertFromHttpResponse(response).get(0);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(player8().getName(), actual.getName());
        assertEquals(player8().getIsGuardian(), actual.getIsGuardian());
    }
    @Test
    void add_players_ko() throws java.lang.Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post("/players")
                        .param("teamName", UnknownTeamEntityName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(player8())))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        Exception actual = objectMapper.readValue(response.getContentAsString(), Exception.class);
        Exception expected = Exception.builder()
                .message("Team#" + UnknownTeamEntityName + " does not exist")
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void edit_players_ok() throws java.lang.Exception {
        Player expected1 = player4();
        Player expected2 = player5();
        expected1.setIsGuardian(true);
        expected2.setName(Player_new_name);

        List<Player> expected = List.of(expected1, expected2);

        MockHttpServletResponse response = mockMvc
                .perform(put("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(player7(), player6())))
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()
                .getResponse();
        List<Player> actual = convertFromHttpResponse(response);

        assertTrue(expected.containsAll(actual));
    }
    @Test
    void edit_players_ko() throws java.lang.Exception {
        MockHttpServletResponse response = mockMvc
                .perform(put("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(Player.builder().name("Player with not id").build())))
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()
                .getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        Exception actual = objectMapper.readValue(response.getContentAsString(), Exception.class);
        Exception expected = Exception.builder()
                .message("All player need id")
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST)
                .build();

        assertEquals(expected, actual);
    }

    private List<Player> convertFromHttpResponse(MockHttpServletResponse response)
            throws JsonProcessingException, UnsupportedEncodingException {
        CollectionType playerListType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Player.class);
        return objectMapper.readValue(
                response.getContentAsString(),
                playerListType);
    }
}
