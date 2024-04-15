package officeManager.service.impl;

import officeManager.dto.MeetingDto;
import officeManager.dto.UserDto;
import officeManager.entity.Meeting;
import officeManager.entity.Room;
import officeManager.repository.MeetingRepository;
import officeManager.repository.RoomRepository;
import officeManager.service.EmailService;
import officeManager.service.MeetingService;
import org.springframework.stereotype.Service;
import officeManager.entity.User;
import officeManager.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class MeetingServiceImpl implements MeetingService{

    private MeetingRepository meetingRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    private RoomRepository roomRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository, UserRepository userRepository, RoomRepository roomRepository, EmailService emailService) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.roomRepository = roomRepository;
    }

    @Override
    public void saveMeeting(MeetingDto meetingDto, int creatorId) {
        Meeting meeting = new Meeting();
        meeting.setName(meetingDto.getName());
        meeting.setStartTime(meetingDto.getStartTime());
        meeting.setEndTime(meetingDto.getEndTime());
        meeting.setCreatorId(creatorId);

        meeting.setUsers(meetingDto.getUsers());
        meeting.setRooms(meetingDto.getRooms());

        meetingRepository.save(meeting);
    }

    @Override
    @Transactional
    public void deleteMeeting(int id) {
        Meeting meeting = meetingRepository.getReferenceById(id);
        // Remove the meeting from associated users and rooms
        if(meeting.getUsers() != null){
            meeting.getUsers().forEach(user -> {
                if (user.getMeetings() != null){
                    user.getMeetings().remove(meeting);
                }
            });
            meeting.getUsers().clear();
        }

        if(meeting.getRooms() != null) {
            for(var room: meeting.getRooms())
                if(room.getMeetings() != null)
                    room.getMeetings().remove(meeting);

            meeting.getRooms().clear();
        }

        meetingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void editMeeting(MeetingDto meetingDto, int meetingId) {
        Meeting meeting = meetingRepository.getReferenceById(meetingId);
        meeting.setName(meetingDto.getName());
        meeting.setStartTime(meetingDto.getStartTime());
        meeting.setEndTime(meetingDto.getEndTime());
        meeting.setUsers(meetingDto.getUsers());
        meeting.setRooms(meetingDto.getRooms());
        meetingRepository.save(meeting);
    }

    @Override
    @Transactional
    public void addUser(User user, Meeting meeting) {
        if(meeting.getUsers().contains(user)){
            return;
        }
        meeting.getUsers().add(user);
        user.getMeetings().add(meeting);

        meetingRepository.save(meeting);
        userRepository.save(user);

        String userEmail = user.getEmail();
        String emailSubject = "You've been added to a meeting";
        String emailText = "You have been added to a meeting.\n\nMeeting Details:\n";
        emailText += "Meeting Title: " + meeting.getName() + "\n";
        emailText += "Meeting Start Time: " + meeting.getStartTime() + "\n";
        emailText += "Meeting End Time: " + meeting.getEndTime() + "\n";

        emailService.sendEmail(userEmail, emailSubject, emailText);
    }

    @Override
    @Transactional
    public void addRoom(Room room, Meeting meeting) {
        meeting.getRooms().add(room);
        room.getMeetings().add(meeting);

        meetingRepository.save(meeting);
        roomRepository.save(room);
    }

    @Override
    public Meeting findById(int id){
        return meetingRepository.getReferenceById(id);
    }

    @Override
    public List<Meeting> findMeetingsForDate(LocalDateTime selectedDate) {
        List<Meeting> meetings = meetingRepository.findAll();
        List<Meeting> selectedMeetings = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (meeting.getStartTime().toLocalDate().equals(selectedDate.toLocalDate())) {
                selectedMeetings.add(meeting);
            }
        }

        return selectedMeetings;
    }

    @Override
    public int getLastMeeting() {
        List<Meeting> meetings = meetingRepository.findAll();
        int size = meetings.size();
        Meeting meeting = meetings.get(size - 1);
        int id = meeting.getId();

        return id;
    }

    public List<Meeting> findAllMeetings() {
        List<Meeting> meetings = meetingRepository.findAll();
        return meetings;
    }
}
