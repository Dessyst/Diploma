package officeManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import officeManager.entity.Meeting;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

    private Long id;

    private String name;

    private String description;

    private int capacity;

    private boolean hasProjector;

    private boolean hasWhiteboard;

    private boolean hasConferenceCall;

    private List<Meeting> meetings;
}
