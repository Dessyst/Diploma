package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.RoomDto;
import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.dto.MeetingDto;
import com.example.registrationlogindemo.entity.Meeting;
import com.example.registrationlogindemo.entity.Room;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.RoomService;
import com.example.registrationlogindemo.service.UserService;
import com.example.registrationlogindemo.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.LocalDate;
import java.util.*;


@Controller
public class AuthController {

    private UserService userService;
    private MeetingService meetingService;
    private RoomService roomService;

    public AuthController(UserService userService, MeetingService meetingService, RoomService roomService) {
        this.userService = userService;
        this.meetingService = meetingService;
        this.roomService = roomService;
    }

    @GetMapping("index")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user, BindingResult result, Model model){

        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/meetingsall")
    public String listMeetings(Model model){
        List<Meeting> meetings = meetingService.findAllMeetings();
        model.addAttribute("meetings", meetings);
        return "meetingsall";
    }


    @GetMapping("/addusertomeeting/{meetingId}")
    public String showAddUserToMeetingForm(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("meeting", meeting);
        return "addUserToMeet"; // Return the name of the Thymeleaf template for the form
    }

    @PostMapping("/addusertomeeting/{meetingId}")
    public String addUserToMeeting(@RequestParam("userId") String userId, @PathVariable int meetingId) {
        User user1 = userService.findById(Integer.parseInt(String.valueOf(userId)));
        Meeting meeting1 = meetingService.findById(meetingId);

        meetingService.addUser(user1, meeting1);

        return "redirect:/addusertomeeting/{meetingId}";
    }

    @GetMapping("/userDetails/{userId}")
    public String showUserDetails(@PathVariable int userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "userDetails";
    }

    @GetMapping("/editUser/{userId}")
    public String showEditUserForm(@PathVariable int userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/editUser/{userId}")
    public String editUser(@ModelAttribute UserDto userDto, @PathVariable int userId) {
        userService.editUser(userDto, userId);
        return "redirect:/userDetails/{userId}";
    }

    @GetMapping("/editmeeting/{meetingId}")
    public String showEditMeetingForm(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        model.addAttribute("meeting", meeting);
        return "editMeeting";
    }

    @PostMapping("/editmeeting/{meetingId}")
    public String editMeeting(@ModelAttribute MeetingDto meetingDto, @PathVariable int meetingId) {
        meetingService.editMeeting(meetingDto, meetingId);
        return "redirect:/meetingDetails/{meetingId}";
    }

    @GetMapping("/editroom/{roomId}")
    public String showEditRoomForm(@PathVariable int roomId, Model model) {
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        return "editRoom";
    }

    @PostMapping("/editroom/{roomId}")
    public String editRoom(@ModelAttribute RoomDto roomDto, @PathVariable int roomId) {
        roomService.editRoom(roomDto, roomId);
        return "redirect:/roomDetails/{roomId}";
    }

    @GetMapping("/rooms")
    public String showRoomForm(Model model) {
        RoomDto room = new RoomDto();
        model.addAttribute("room", room);
        return "roomForm";
    }

    @PostMapping("/rooms/save")
    public String createRoom(@Valid @ModelAttribute("room") RoomDto room,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("room", room);
            return "roomForm";
        }

        roomService.saveRoom(room);
        return "redirect:/rooms?success";
    }
    @GetMapping("/roomsall")
    public String listRooms(Model model){
        List<RoomDto> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        return "roomsall";
    }

    @GetMapping("/meetingDetails/{meetingId}")
    public String showMeetingDetails(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        model.addAttribute("meeting", meeting);
        return "meetingDetails";
    }

    @GetMapping("/roomDetails/{roomId}")
    public String showRoomDetails(@PathVariable int roomId, Model model) {
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        return "roomDetails";
    }

    @GetMapping("/filterrooms")
    public String showFilterRooms(Model model) {
        return "filterRooms";
    }

    @PostMapping("/filterrooms")
    public String filterRooms(@RequestParam("capacity") int capacity,
                              @RequestParam(name = "hasWhiteboard", defaultValue = "false") boolean hasWhiteboard,
                              @RequestParam(name = "hasProjector", defaultValue = "false") boolean hasProjector,
                              @RequestParam(name = "hasConferenceCall", defaultValue = "false") boolean hasConferenceCall,
                              Model model){

        List<Room> filteredRooms = roomService.filterRooms(hasProjector, hasWhiteboard, hasConferenceCall, capacity);
        model.addAttribute("filteredRooms", filteredRooms);

        return "filterRooms";
    }

    @GetMapping("/bookroom/{roomId}")
    public String showBookRoom(Model model, @PathVariable int roomId) {
        MeetingDto meeting = new MeetingDto();
        model.addAttribute("meeting", meeting);
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        List<Meeting> meetings = meetingService.findAllMeetings();
        model.addAttribute("meetings", meetings);
        return "bookRoom";
    }

    @PostMapping("/bookroom/{roomId}")
    public String bookRoom(@Valid @ModelAttribute("meeting") MeetingDto meeting,
                                BindingResult result, Model model, @PathVariable int roomId) {

        Room room1 = roomService.findById(roomId);
        List<Meeting> meetings = room1.getMeetings();

        for(Meeting m: meetings){
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

    @GetMapping("/passing")
    public String Passing() {
        int meetingCount = meetingService.getLastMeeting();

        return "redirect:/addusersorlater/" + meetingCount;
    }

    @GetMapping("/addusersorlater/{meetingId}")
    public String addUsersOrLater(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        model.addAttribute("meeting", meeting);

        return "addUsersOrLater";
    }


    @GetMapping("/mymeetings")
    public String showMyMeetings(Model model) {
        List<Meeting> meetings = meetingService.findAllMeetings();
        List<Meeting> myMeetings = new ArrayList<>();
        for(Meeting meeting: meetings){
            if(meeting.getCreatorId() == currentUserId()){
                myMeetings.add(meeting);
            }
        }
        model.addAttribute("meetings", myMeetings);
        return "myMeetings";
    }

    @GetMapping("/myaccount")
    public String showMyAccount(Model model) {
        model.addAttribute("currentId", currentUserId());

        User user = userService.findById(currentUserId());
        model.addAttribute("user", user);

        return "myAccount";
    }


    @GetMapping("/calendar")
    public String getCalendar(Model model) {
        // Fetch events from your data source
        return "calendar";
    }

    @GetMapping("/mycalendar")
    public String getMyCalendar(Model model) {
        return "myCalendar";
    }


    @GetMapping("/agendaDay")
    public String showAgendaForDate(Model model, @RequestParam("date") String date) {
        List<Meeting> meetings = meetingService.findMeetingsForDate(LocalDate.parse(date).atStartOfDay());
        model.addAttribute("events", meetings);
        return "agenda";
    }


    @GetMapping("/events")
    @ResponseBody
    public List<Map<String, Object>> getEvents() {
        List<Meeting> meetings = meetingService.findAllMeetings();

        List<Map<String, Object>> events = new ArrayList<>();
        for (Meeting meeting : meetings) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", meeting.getName());
            event.put("start", meeting.getStartTime().toString());
            event.put("end", meeting.getEndTime().toString());
            events.add(event);
        }

        return events;
    }

    @GetMapping("/myevents")
    @ResponseBody
    public List<Map<String, Object>> getMyEvents() {
        int currentId = currentUserId();
        List<Meeting> meetings = userService.findAllMeetingsForUser(currentId);

        List<Map<String, Object>> events = new ArrayList<>();
        for (Meeting meeting : meetings) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", meeting.getName());
            event.put("start", meeting.getStartTime().toString());
            event.put("end", meeting.getEndTime().toString());
            events.add(event);
        }

        return events;
    }



    @GetMapping("currentuser")
    public String getCurrentUsername(Model model) {
        model.addAttribute("currentId", currentUserId());

        return "currentuser";
    }


    int currentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userService.findByEmail(authentication.getName()).getUser_id();
        }
        return 0;
    }

    @GetMapping("/deleteuser/{userId}")
    public String showDeleteUserForm(@PathVariable int userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "deleteUser";
    }

    @PostMapping("/deleteuser/{userId}")
    public String deleteUser(@PathVariable int userId){
        userService.deleteUser(userId);
        return "redirect:/users";
    }

    @GetMapping("/deleteroom/{roomId}")
    public String showDeleteRoomForm(@PathVariable int roomId, Model model) {
        Room room = roomService.findById(roomId);
        model.addAttribute("room", room);
        return "deleteRoom";
    }

    @PostMapping("/deleteroom/{roomId}")
    public String deleteRoom(@PathVariable int roomId){
        roomService.deleteRoom(roomId);
        return "redirect:/users";
    }

    @GetMapping("/deletemeeting/{meetingId}")
    public String showDeleteMeetingForm(@PathVariable int meetingId, Model model) {
        Meeting meeting = meetingService.findById(meetingId);
        model.addAttribute("meeting", meeting);
        return "deleteMeeting";
    }

    @PostMapping("/deletemeeting/{meetingId}")
    public String deleteMeeting(@PathVariable int meetingId){
        meetingService.deleteMeeting(meetingId);
        return "redirect:/meetingsall";
    }
}
