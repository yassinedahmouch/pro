package com.maiia.pro.service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProAvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    private static final String APPOINTMENT_END_DATE = "appointment end date";

	private static final String IS_SLOT_AVAILABLE    = "is slot available";

	private static final Duration TIME_SLOT_DURATION = Duration.ofMinutes(15);

    public List<Availability> findByPractitionerId(Integer practitionerId) {
        return availabilityRepository.findByPractitionerId(practitionerId);
    }

    public List<Availability> generateAvailabilities(Integer practitionerId) {
    	List<TimeSlot> timeSlots = timeSlotRepository.findByPractitionerId(practitionerId);
		List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitionerId);
		List<Availability> availabilities = new ArrayList<>();

		for (TimeSlot timeSlot : timeSlots) {
			LocalDateTime start = timeSlot.getStartDate();
			LocalDateTime end = timeSlot.getEndDate();
			LocalDateTime currentSlotStart = start;

			while (currentSlotStart.isBefore(end)) {
				LocalDateTime currentSlotEnd = currentSlotStart.plus(TIME_SLOT_DURATION);

				Map<String, Object> result = isSlotAvailable(currentSlotStart, currentSlotEnd, appointments);
				boolean isSlotAvailable = (boolean) result.get(IS_SLOT_AVAILABLE);

				if (isSlotAvailable) {
					availabilities.add(Availability.builder().practitionerId(practitionerId).startDate(currentSlotStart)
																							.endDate(currentSlotEnd).build());
				}

				currentSlotStart = (LocalDateTime) result.get(APPOINTMENT_END_DATE);
			}
		}

		return availabilities;
    }
    
    private static Map<String, Object> isSlotAvailable(LocalDateTime start, LocalDateTime end, List<Appointment> appointments) {
		Map<String, Object> result = new HashMap<>();

		for (Appointment appointment : appointments) {
			LocalDateTime appointmentEndDate = appointment.getEndDate();

			if (start.isBefore(appointmentEndDate) && end.isAfter(appointment.getStartDate())) {
				result.put(IS_SLOT_AVAILABLE, false);
				result.put(APPOINTMENT_END_DATE, appointmentEndDate);
				return result;
			}
		}

		result.put(IS_SLOT_AVAILABLE, true);
		result.put(APPOINTMENT_END_DATE, end);
		return result;
	}
}
