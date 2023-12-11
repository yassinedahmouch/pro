package com.maiia.pro.service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.repository.AppointmentRepository;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProAppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment find(String appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow();
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> findByPractitionerId(Integer practitionerId) {
        return appointmentRepository.findByPractitionerId(practitionerId);
    }
    
	public Appointment createAppointment(Integer practitionerId, Integer patientId, LocalDateTime startDate,
			LocalDateTime endDate) {
		return appointmentRepository.save(Appointment.builder().practitionerId(practitionerId)
															   .patientId(patientId)
															   .startDate(startDate)
														       .endDate(endDate)
															   .build());
	}
}
