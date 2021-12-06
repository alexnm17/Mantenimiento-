package com.vacuna.vacuna.controller;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.vacuna.vacuna.dao.FormatoVacunacionDAO;
import com.vacuna.vacuna.exception.ControlHorasVacunacionException;
import com.vacuna.vacuna.model.FormatoVacunacion;


@RestController
@RequestMapping("formato")
public class FormatoVacunacionController {

	@Autowired
	private FormatoVacunacionDAO formatoVacunacionDao;

	@PostMapping("/definirFormatoVacunacion")
	public void definirFormatoVacunacion(HttpSession session, @RequestBody Map<String, Object> datosFormatoVacunacion) {

		try {
			JSONObject jso = new JSONObject(datosFormatoVacunacion);
			String horaInicio = jso.getString("horaInicio");
			String horaFin = jso.getString("horaFin");
			int duracionFranja = jso.getInt("duracionFranja");
			int personasAVacunar = jso.getInt("personasAVacunar");

			FormatoVacunacion formatoVacunacion = new FormatoVacunacion(horaInicio, horaFin, duracionFranja,
					personasAVacunar);
			if (formatoVacunacion.horasCorrectas()) {
				formatoVacunacionDao.insert(formatoVacunacion);
			} else {
				if (!formatoVacunacion.horasCorrectas())
					throw new ControlHorasVacunacionException();
			}

		} catch (ControlHorasVacunacionException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}

	}

	@GetMapping("/getFormatoVacunacion")
	public FormatoVacunacion getFormatoVacunacion() {
		Optional<FormatoVacunacion> optFormato = formatoVacunacionDao.findById("Formato_Unico");
		FormatoVacunacion formatoVacunacion = null;
		if (optFormato.isPresent())
			formatoVacunacion = optFormato.get();
		else {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No existe un formato de Vacunacion definido");
		}
		return formatoVacunacion;
	}
}
