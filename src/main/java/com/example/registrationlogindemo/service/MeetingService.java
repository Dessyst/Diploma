package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.dto.MeetingDto;
import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.Meeting;
import com.example.registrationlogindemo.entity.Room;
import com.example.registrationlogindemo.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    void saveMeeting(MeetingDto meetingDto, int creatorId);
    void deleteMeeting(int id);
    Meeting findById(int id);
    public List<User> findAllUsersInMeeting(int id);
    void addUser(User user, Meeting meeting);
    List<Meeting> findAllMeetings();
    void addRoom(Room room, Meeting meeting);
    int getLastMeeting();
    void editMeeting(MeetingDto meetingDto, int id);

    Meeting findByName(String name);

    List<Meeting> findMeetingsForDate(LocalDateTime selectedDate);


}
