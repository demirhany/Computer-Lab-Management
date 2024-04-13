package com.swedist.computer_lab_backend.controller;

import com.swedist.computer_lab_backend.dto.ComputerStudentDTO;
import com.swedist.computer_lab_backend.dto.ReservationPostRequest;
import com.swedist.computer_lab_backend.repository.ComputerRepository;
import com.swedist.computer_lab_backend.repository.StudentRepository;
import com.swedist.computer_lab_backend.service.ComputerService;
import com.swedist.computer_lab_backend.service.ReservationService;
import com.swedist.computer_lab_backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.swedist.computer_lab_backend.constants.AppConstants.*;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    private final ComputerService computerService;
    private final StudentService studentService;

    @Autowired
    public ReservationController(ReservationService reservationService,
                                 ComputerService computerService,
                                 StudentService studentService) {
        this.reservationService = reservationService;
        this.computerService = computerService;
        this.studentService = studentService;
    }

    @GetMapping({"/", ""})
    public String showReservations(@RequestParam(required = false) String successMessage,
                                   @RequestParam(required = false) String errorMessage,
                                   Model model) {
        model.addAttribute(STUDENT_LIST_KEY, studentService.getStudents());
        model.addAttribute(COMPUTER_LIST_KEY, computerService.getComputers());
        model.addAttribute(RESERVATION_LIST_KEY, reservationService.getReservations());
        model.addAttribute(SUCCESS_MESSAGE_KEY,
                Objects.requireNonNullElse(successMessage, "Reservation list fetched successfully"));
        if (errorMessage != null) model.addAttribute(ERROR_MESSAGE_KEY, errorMessage);
        return "reservation/index";
    }

    @PostMapping(value = {"/", ""})
    public String addReservation(ReservationPostRequest reservationPostRequest) {
        String successMessage = null;
        String errorMessage = null;
        try {
            ComputerStudentDTO createdReservation = reservationService.createReservation(reservationPostRequest);
            successMessage = "Reservation added successfully. id: %d".formatted(createdReservation.getId());
        } catch (Exception e) {
            errorMessage = "Failed to add reservation";
        }
        return getRedirect(successMessage, errorMessage);
    }

    @PostMapping(value = "/{id}")
    public String updateReservation(ReservationPostRequest reservationPostRequest, @PathVariable Long id) {
        String successMessage = null;
        String errorMessage = null;
        try {
            ComputerStudentDTO createdReservation = reservationService.updateReservation(id, reservationPostRequest);
            successMessage = "The reservation updated successfully. id: %d".formatted(createdReservation.getId());
        } catch (Exception e) {
            errorMessage = "Failed to update the reservation";
        }
        return getRedirect(successMessage, errorMessage);
    }

    private String getRedirect(String successMessage, String errorMessage) {
        StringBuilder redirectUrl = new StringBuilder("redirect:/reservation/");
        if (errorMessage != null || successMessage != null) redirectUrl.append("?");
        if (errorMessage != null) redirectUrl.append("errorMessage=").append(errorMessage);
        else if (successMessage != null) redirectUrl.append("successMessage=").append(successMessage);
        return redirectUrl.toString();
    }
}
