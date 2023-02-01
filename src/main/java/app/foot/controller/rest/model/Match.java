package app.foot.controller.rest.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Match {
  private Integer id;
  private TeamMatch teamA;
  private TeamMatch teamB;
  private String stadium;
  private Instant datetime;
}
