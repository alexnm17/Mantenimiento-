package com.vacuna.vacuna.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vacuna.vacuna.dao.CupoDAO;
import com.vacuna.vacuna.dao.PacienteDAO;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cupo;
import com.vacuna.vacuna.model.Paciente;


@RestController
public class CupoController {

	@Autowired
	private CupoDAO cupoDao;
	@Autowired
	private PacienteDAO pacienteDao;

	@GetMapping("/getAllCuposConHuecoPorFecha")
	public List<Cupo> getAllCuposConHuecoPorFecha(@RequestParam String email, @RequestParam String fecha) {
		Optional<Paciente> optPaciente = pacienteDao.findById(email);
		CentroSanitario centroSanitario = new CentroSanitario();
		if (optPaciente.isPresent())
			centroSanitario.setNombre(((Paciente) optPaciente.get()).getCentroAsignado());

		List<Cupo> listaCupos = cupoDao.findAllByCentroVacunacionAndFecha(centroSanitario, fecha);
		List<Cupo> listaCuposLibres = new ArrayList<>();

		listaCupos.sort(Comparator.comparing(Cupo::getFecha));

		Cupo cupo;
		for (int i = 0; i < listaCupos.size(); i++) {
			cupo = listaCupos.get(i);
			if (cupo.getPersonasRestantes() > 0) {
				listaCuposLibres.add(cupo);
			}
		}

		return listaCuposLibres;
	}
}
