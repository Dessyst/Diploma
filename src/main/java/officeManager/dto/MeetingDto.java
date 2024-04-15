package officeManager.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import officeManager.entity.User;
import officeManager.entity.Room;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDto {
    private Long id;

    @NotEmpty
    private String name;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    private List<User> users;

    private List<Room> rooms;
}
