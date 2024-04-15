package officeManager.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="meetings")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private LocalDateTime startTime;

    @Column(nullable=false)
    private LocalDateTime endTime;

    @Column(nullable=false)
    private int creatorId;

    @ManyToMany(mappedBy="meetings", fetch = FetchType.LAZY)
    private List<User> users;
    @ManyToMany(mappedBy = "meetings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms;

    public List<Room> getRooms() {
        if (rooms == null) {
            rooms = new ArrayList<>();
        }
        return rooms;
    }
}
