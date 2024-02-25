package com.example.registrationlogindemo.service;

import com.example.registrationlogindemo.dto.RoomDto;
import com.example.registrationlogindemo.entity.Room;

import java.util.List;

public interface RoomService {
    void saveRoom(RoomDto roomDto);
    void deleteRoom(int id);
    void editRoom(RoomDto roomDto, int id);


    List<RoomDto> findAllRooms();

    Room findById(int id);
    List<Room> filterRooms(boolean hasProjector, boolean hasWhiteBoard, boolean hasConferenceCall, int capacity);
}
