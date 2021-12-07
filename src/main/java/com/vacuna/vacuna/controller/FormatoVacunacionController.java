package com.vacuna.vacuna.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

import com.vacuna.vacuna.dao.CentroSanitarioDAO;
import com.vacuna.vacuna.dao.CupoDAO;
import com.vacuna.vacuna.dao.FormatoVacunacionDAO;
import com.vacuna.vacuna.exception.ControlHorasVacunacionException;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cupo;
import com.vacuna.vacuna.model.FormatoVacunacion;


@RestController
@RequestMapping("formato")
public class FormatoVacunacionController {

	@Autowired
	private FormatoVacunacionDAO formatoVacunacionDao;
	@Autowired
	private CentroSanitarioDAO centroVacunacionDao;
	@Autowired
	private CupoDAO cupoDao;

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
			crearPlantillasCitaVacunacion();
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
	
	public void crearPlantillasCitaVacunacion() {
		FormatoVacunacion formato = getFormatoVacunacion();
		List<CentroSanitario> centrosVacunacion = centroVacunacionDao.findAll();

		int horaFin = LocalTime.parse(formato.getHoraFinVacunacion()).getHour();
		int horaInicio = LocalTime.parse(formato.getHoraInicioVacunacion()).getHour();
		double duracion = (double) formato.getDuracionFranjaVacunacion() / 60;
		int numFranjas = (int) ((horaFin - horaInicio) / duracion);

		for (int i = 0; i < centrosVacunacion.size(); i++) {
			LocalDate fechaCita = LocalDate.now();

			while (fechaCita.isBefore(LocalDate.parse(LocalDate.now().plusYears(1).getYear() + "-02-01"))) {

				crearCupo(numFranjas, fechaCita, LocalTime.parse(formato.getHoraInicioVacunacion()),
						centrosVacunacion.get(i), formato.getDuracionFranjaVacunacion(),
						formato.getPersonasPorFranja());

				fechaCita = fechaCita.plusDays(1);
			}
		}
	}
	
	public void crearCupo(int numFranjas, LocalDate fecha, LocalTime horaInicio, CentroSanitario centroSanitario,
			int duracion, int personasMax) {
		
		Cupo cupo;

		for (int i = 0; i < numFranjas; i++) {

			cupo = new Cupo(fecha.toString(), horaInicio.toString(), centroSanitario, personasMax);
			cupoDao.save(cupo);

			horaInicio = horaInicio.plusMinutes(duracion);

		}

	}
}
