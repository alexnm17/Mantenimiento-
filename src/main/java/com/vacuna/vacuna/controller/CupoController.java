package com.vacuna.vacuna.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vacuna.vacuna.dao.UsuarioDAO;

	

public class CupoController {
	
	@Autowired
	private UsuarioDAO repositoryUsuario;
	/*
	@GetMapping("/getAllCuposDisponiblesPorFecha")
	public List<Cupo> getAllCuposConHuecoPorFecha(HttpSession session, @RequestParam String fecha) {
		Optional<Usuario> optUsuario = repositoryUsuario.findByEmail((String)session.getAttribute("email"));
		CentroSanitario centroSanitario = new CentroSanitario();
		if (optUsuario.isPresent())
			centroSanitario = optUsuario.get().getCentroSanitario();

		List<Cupo> listaCupos = cupoDao.findAllByCentroSanitarioAndFecha(centroVacunacion, fecha);
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
	}*/
}
