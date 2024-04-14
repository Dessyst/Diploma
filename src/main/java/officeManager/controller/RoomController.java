package officeManager.controller;

import jakarta.validation.Valid;
import officeManager.dto.MeetingDto;
import officeManager.dto.RoomDto;
import officeManager.entity.Meeting;
import officeManager.entity.Room;
import officeManager.service.MeetingService;
import officeManager.service.RoomService;
import officeManager.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("room")
public class RoomController {
    private RoomService roomService;
    private MeetingService meetingService;
    private UserService userService;

    public RoomController(RoomService roomService, MeetingService meetingService, UserService userService) {
        this.roomService = roomService;
        this.meetingService = meetingService;
        this.userService = userService;
    }

    @GetMapping("edit/{roomId}")
    public String showEditRoomForm(@PathVariable int roomId, Model model) {
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        return "editRoom";
    }

    @PostMapping("edit/{roomId}")
    public String editRoom(@ModelAttribute RoomDto roomDto, @PathVariable int roomId) {
        roomService.editRoom(roomDto, roomId);
        return "redirect:/room/{roomId}";
    }

    @GetMapping("new")
    public String showRoomForm(Model model) {
        RoomDto room = new RoomDto();
        model.addAttribute("room", room);
        return "roomForm";
    }

    @GetMapping("{roomId}")
    public String showRoomDetails(@PathVariable int roomId, Model model) {
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        return "roomDetails";
    }

    @PostMapping("new")
    public String createRoom(@Valid @ModelAttribute("room") RoomDto room,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("room", room);
            return "roomForm";
        }

        roomService.saveRoom(room);
        return "redirect:/room";
    }
    @GetMapping("")
    public String listRooms(Model model) {
        List<RoomDto> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        return "roomsall";
    }

    @GetMapping("filter")
    public String showFilterRooms(Model model) {
        return "filterRooms";
    }

    @PostMapping("filter")
    public String filterRooms(@RequestParam("capacity") int capacity,
                              @RequestParam(name = "hasWhiteboard", defaultValue = "false") boolean hasWhiteboard,
                              @RequestParam(name = "hasProjector", defaultValue = "false") boolean hasProjector,
                              @RequestParam(name = "hasConferenceCall", defaultValue = "false") boolean hasConferenceCall,
                              Model model) {

        List<Room> filteredRooms = roomService.filterRooms(hasProjector, hasWhiteboard, hasConferenceCall, capacity);
        model.addAttribute("filteredRooms", filteredRooms);

        return "filterRooms";
    }

    @GetMapping("book/{roomId}")
    public String showBookRoom(Model model, @PathVariable int roomId) {
        MeetingDto meeting = new MeetingDto();
        model.addAttribute("meeting", meeting);
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        List<Meeting> meetings = meetingService.findAllMeetings();
        model.addAttribute("meetings", meetings);
        return "bookRoom";
    }

    @PostMapping("book/{roomId}")
    public String bookRoom(@Valid @ModelAttribute("meeting") MeetingDto meeting,
                           BindingResult result, Model model, @PathVariable int roomId) {

        Room room1 = roomService.findById(roomId);
        List<Meeting> meetings = room1.getMeetings();

        for(Meeting m: meetings) {
            if(meeting.getStartTime().isAfter(m.getStartTime()) && meeting.getEndTime().isBefore(m.getEndTime())){
                result.rejectValue("startTime", null, "Meeting time is already used");
            }
            else if (meeting.getStartTime().isBefore(m.getStartTime()) && meeting.getEndTime().isAfter(m.getEndTime())){
                result.rejectValue("endTime", null, "Meeting time is already used");
            }
            else if (meeting.getStartTime().isAfter(m.getStartTime()) && meeting.getStartTime().isBefore(m.getEndTime())){
                result.rejectValue("startTime", null, "Meeting time is already used");
            }
            else if (meeting.getEndTime().isAfter(m.getStartTime()) && meeting.getEndTime().isBefore(m.getEndTime())){
                result.rejectValue("endTime", null, "Meeting time is already used");
            }
            if(meeting.getStartTime().isAfter(meeting.getEndTime())){
                result.rejectValue("startTime", null, "Meeting start time cannot be after end time");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("meeting", meeting);
            model.addAttribute("room", room1);

            return "bookRoom";
        }

        meetingService.saveMeeting(meeting, currentUserId());

        int lastMeetingId = meetingService.getLastMeeting();

        Meeting meeting1 = meetingService.findById(lastMeetingId);

        meetingService.addRoom(room1, meeting1);

        return "redirect:/passing";
    }

    @GetMapping("delete/{roomId}")
    public String showDeleteRoomForm(@PathVariable int roomId, Model model) {
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        return "deleteRoom";
    }

    @PostMapping("delete/{roomId}")
    public String deleteRoom(@PathVariable int roomId){
        roomService.deleteRoom(roomId);
        return "redirect:/room";
    }

    int currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userService.findByEmail(authentication.getName()).getId();
        }
        return 0;
    }
}
