package app.foot.controller.rest.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@Setter
public class Player {
    private Integer id;
    private String name;
    private Boolean isGuardian;
}
