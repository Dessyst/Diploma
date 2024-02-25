package com.example.registrationlogindemo.service.impl;

import com.example.registrationlogindemo.dto.MeetingDto;
import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.Meeting;
import com.example.registrationlogindemo.entity.Room;
import com.example.registrationlogindemo.repository.MeetingRepository;
import com.example.registrationlogindemo.repository.RoomRepository;
import com.example.registrationlogindemo.service.EmailService;
import com.example.registrationlogindemo.service.MeetingService;
import org.springframework.stereotype.Service;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        Meeting meeting = meetingRepository.findById(id);
        if (meeting != null) {
            // Remove the meeting from associated users and rooms
            meeting.getUsers().forEach(user -> user.getMeetings().remove(meeting));
            meeting.getRooms().forEach(room -> room.getMeetings().remove(meeting));

            // Clear the associations to ensure they are properly updated
            meeting.getUsers().clear();
            meeting.getRooms().clear();

            meetingRepository.deleteById(id);
        } else {
            // Handle the case where the meeting with the given ID is not found
            System.out.println("Meeting not found with ID: " + id);
        }
    }


    @Override
    @Transactional
    public void editMeeting(MeetingDto meetingDto, int meetingId){
        Meeting meeting = meetingRepository.findById(meetingId);
        meeting.setName(meetingDto.getName());
        meeting.setStartTime(meetingDto.getStartTime());
        meeting.setEndTime(meetingDto.getEndTime());
        meeting.setUsers(meetingDto.getUsers());
        meeting.setRooms(meetingDto.getRooms());
        meetingRepository.save(meeting);
    }


    @Override
    @Transactional
    public void addUser(User user, Meeting meeting){
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
    public void addRoom(Room room, Meeting meeting){
        meeting.getRooms().add(room);
        room.getMeetings().add(meeting);

        meetingRepository.save(meeting);
        roomRepository.save(room);
    }



    @Override
    public Meeting findById(int id){
        return meetingRepository.findById(id);
    }

    @Override
    public List<User> findAllUsersInMeeting(int id){
        List<User> users = meetingRepository.findById(id).getUsers();
        return users;
    }

    @Override
    public List<Meeting> findMeetingsForDate(LocalDateTime selectedDate){
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
    public int getLastMeeting(){
        List<Meeting> meetings = meetingRepository.findAll();
        int size = meetings.size();
        Meeting meeting = meetings.get(size - 1);
        int id = meeting.getId();

        return id;
    }


    @Override
    public Meeting findByName(String name){
        return meetingRepository.findByName(name);
    }





    public List<Meeting> findAllMeetings() {
        List<Meeting> meetings = meetingRepository.findAll();
        //return meetings.stream().map((meeting) -> convertEntityToDto(meeting))
        //      .collect(Collectors.toList());
        return meetings;
    }

    private MeetingDto convertEntityToDto(Meeting meeting){
        MeetingDto meetingDto = new MeetingDto();
        meeting.setMeeting_id(meeting.getMeeting_id());
        meetingDto.setName(meeting.getName());
        meetingDto.setStartTime(meeting.getStartTime());
        meetingDto.setEndTime(meeting.getEndTime());
        return meetingDto;
    }
}
