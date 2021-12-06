package com.vacuna.vacuna.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.vacuna.vacuna.dao.UsuarioDAO;




import org.springframework.web.bind.annotation.RestController;

import com.vacuna.vacuna.dao.CentroSanitarioDAO;
import com.vacuna.vacuna.dao.CupoDAO;
import com.vacuna.vacuna.dao.PacienteDAO;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cupo;
import com.vacuna.vacuna.model.FormatoVacunacion;
import com.vacuna.vacuna.model.Paciente;
import com.vacuna.vacuna.model.Usuario;




@RestController
public class CupoController {

	@Autowired
	private CupoDAO cupoDao;
	@Autowired
	private PacienteDAO pacienteDao;
	@Autowired
	private CentroSanitarioDAO repositoryCentro;



	@GetMapping("/getAllCuposDisponiblesPorFecha")
	public List<Cupo> getAllCuposConHuecoPorFecha(HttpSession session, @RequestParam String fecha) {
		
		Optional<Paciente> optUsuario = pacienteDao.findById((String)session.getAttribute("email"));
		CentroSanitario centroSanitario = new CentroSanitario();
		if (optUsuario.isPresent())
			centroSanitario = repositoryCentro.findByNombre(optUsuario.get().getCentroAsignado());

		List<Cupo> listaCupos = cupoDao.findAllByCentroSanitarioAndFecha(centroSanitario, fecha);
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
