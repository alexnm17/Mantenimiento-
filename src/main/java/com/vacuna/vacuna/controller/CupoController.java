package com.vacuna.vacuna.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vacuna.vacuna.dao.CentroSanitarioDAO;
import com.vacuna.vacuna.dao.CupoDAO;
import com.vacuna.vacuna.dao.UsuarioDAO;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cupo;
import com.vacuna.vacuna.model.Usuario;

@RestController
@RequestMapping("cupo")
public class CupoController {

	@Autowired
	private CupoDAO cupoDao;
	@Autowired
	private UsuarioDAO usuarioDao;
	@Autowired
	private CentroSanitarioDAO repositoryCentro;

	@GetMapping("/getAllCuposDisponiblesPorFecha/{fecha}/{emailPaciente}")
	public List<Cupo> getAllCuposConHuecoPorFecha(HttpSession session, @PathVariable("fecha") String fecha,
			@PathVariable("emailPaciente") String emailPaciente) {
		String emailUsuario = emailPaciente;
		Optional<Usuario> optUsuario = usuarioDao.findById(emailUsuario);
		CentroSanitario centroSanitario = new CentroSanitario();
		if (optUsuario.isPresent())
			centroSanitario = repositoryCentro.findByNombre(optUsuario.get().getCentroAsignado());
		// Aqui abajo me da problemas, no se por que pero devuelve una array vacia.
		// Revisar.
		List<Cupo> listaCupos = cupoDao.findAllByCentroSanitarioAndFecha(centroSanitario, fecha);
		List<Cupo> listaCuposLibres = new ArrayList<>();

		listaCupos.sort((d1, d2) -> d1.getFecha().compareTo(d2.getFecha()));

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
