package officeManager.service;

import officeManager.dto.MeetingDto;
import officeManager.entity.Meeting;
import officeManager.entity.Room;
import officeManager.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    void saveMeeting(MeetingDto meetingDto, int creatorId);

    void deleteMeeting(int id);

    Meeting findById(int id);

    void addUser(User user, Meeting meeting);

    List<Meeting> findAllMeetings();

    void addRoom(Room room, Meeting meeting);

    int getLastMeeting();

    void editMeeting(MeetingDto meetingDto, int id);

    List<Meeting> findMeetingsForDate(LocalDateTime selectedDate);
}
