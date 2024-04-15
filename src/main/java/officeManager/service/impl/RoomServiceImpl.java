package officeManager.service.impl;

import officeManager.dto.RoomDto;
import officeManager.entity.Room;
import officeManager.repository.RoomRepository;
import officeManager.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void saveRoom(RoomDto roomDto) {
        Room room = new Room();
        room.setName(roomDto.getName());
        room.setCapacity(roomDto.getCapacity());
        room.setDescription(roomDto.getDescription());
        room.setHasProjector(roomDto.isHasProjector());
        room.setHasWhiteboard(roomDto.isHasWhiteboard());
        room.setHasConferenceCall(roomDto.isHasConferenceCall());

        roomRepository.save(room);
    }

    @Override
    @Transactional
    public void editRoom(RoomDto roomDto, int id) {
        Room room = roomRepository.getReferenceById(id);
        room.setName(roomDto.getName());
        room.setCapacity(roomDto.getCapacity());
        room.setDescription(roomDto.getDescription());
        room.setHasProjector(roomDto.isHasProjector());
        room.setHasWhiteboard(roomDto.isHasWhiteboard());
        room.setHasConferenceCall(roomDto.isHasConferenceCall());
        roomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(int id){
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomDto> findAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        List<RoomDto> roomDtos = new ArrayList<>();
        for (Room room : rooms) {
            RoomDto roomDto = new RoomDto();
            roomDto.setId((long) room.getId());
            roomDto.setName(room.getName());
            roomDto.setCapacity(room.getCapacity());
            roomDto.setDescription(room.getDescription());
            roomDto.setHasProjector(room.isHasProjector());
            roomDto.setHasWhiteboard(room.isHasWhiteboard());
            roomDto.setHasConferenceCall(room.isHasConferenceCall());
            roomDto.setMeetings(room.getMeetings());
            roomDtos.add(roomDto);
        }
        return roomDtos;
    }

    @Override
    public Room findById(int id) {
        return roomRepository.getReferenceById(id);
    }

    @Override
     public List<Room> filterRooms(boolean hasProjector, boolean hasWhiteBoard, boolean hasConferenceCall, int capacity) {
        List<Room> rooms = roomRepository.findAll();
        List<Room> filteredRooms = new ArrayList<>();

        for (Room room : rooms) {

            if ((hasProjector && !room.isHasProjector()) ||
                    (hasWhiteBoard && !room.isHasWhiteboard()) ||
                    (hasConferenceCall && !room.isHasConferenceCall() || room.getCapacity() < capacity)) {
            } else {
                filteredRooms.add(room);
            }
        }

        return filteredRooms;
    }
}
