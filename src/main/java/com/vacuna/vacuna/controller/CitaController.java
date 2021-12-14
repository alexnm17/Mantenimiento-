
package com.vacuna.vacuna.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.vacuna.vacuna.dao.CentroSanitarioDAO;
import com.vacuna.vacuna.dao.CitaDAO;
import com.vacuna.vacuna.dao.CupoDAO;
import com.vacuna.vacuna.dao.FormatoVacunacionDAO;
import com.vacuna.vacuna.dao.UsuarioDAO;
import com.vacuna.vacuna.exception.CentrosNoEncontradosException;
import com.vacuna.vacuna.exception.CitasNoEncontradasException;
import com.vacuna.vacuna.exception.ControlHorasVacunacionException;
import com.vacuna.vacuna.exception.CupoNoEncontradoException;
import com.vacuna.vacuna.exception.NoHayDosisException;
import com.vacuna.vacuna.exception.SlotVacunacionSuperadoException;
import com.vacuna.vacuna.exception.UsuarioNoExisteException;
import com.vacuna.vacuna.exception.VacunaException;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cita;
import com.vacuna.vacuna.model.Cupo;
import com.vacuna.vacuna.model.FormatoVacunacion;
import com.vacuna.vacuna.model.Paciente;
import com.vacuna.vacuna.model.Usuario;

@RestController
/***
 * 
 * @author crist
 *
 */
@RequestMapping("cita")
public class CitaController {

	CentroSanitario centroSanitario;

	@Autowired
	private CitaDAO repositoryCita;

	@Autowired
	private FormatoVacunacionDAO repositoryFormatoVacunacion;

	@Autowired
	private CentroSanitarioDAO repositoryCentro;

	@Autowired
	private UsuarioDAO repositoryUsuario;

	@Autowired
	private CupoDAO repositoryCupo;

	@GetMapping("/getTodos")
	/***
	 * Obtenemos la lista de las citas
	 * 
	 * @return repositorio de citas
	 * @throws CitasNoEncontradasException
	 */
	public List<Cita> get() throws CitasNoEncontradasException {
		try {
			return repositoryCita.findAll();
		} catch (Exception e) {
			throw new CitasNoEncontradasException();
		}
	}

	@GetMapping("/getCentroSanitario/{email}")
	/***
	 * Obtenemos la lista de los centros
	 * 
	 * @param email
	 * @return repositorio de centros
	 * @throws CentrosNoEncontradosException
	 */
	public List<Cita> getCentroSanitario(@PathVariable String email) throws CentrosNoEncontradosException {
		Usuario sanitario = repositoryUsuario.findByEmail(email);

		CentroSanitario cs = repositoryCentro.findByNombre(sanitario.getCentroAsignado());
		try {
			return repositoryCita.findAllByNombreCentro(cs.getNombre());
		} catch (Exception e) {
			throw new CentrosNoEncontradosException();
		}
	}

	@DeleteMapping("/anularCita/{id}")
	public void anularCita(HttpSession session, @PathVariable String id) throws VacunaException {
		try {
			Optional<Cita> optCita = repositoryCita.findById(id);
			Cita cita = null;
			if (optCita.isPresent()) {
				cita = optCita.get();
			} else {
				throw new CitasNoEncontradasException ();
			}

			if (cita.isUsada())
				throw new VacunaException(HttpStatus.CONFLICT,
						"La cita que intenta anular ya ha sido utilizada.");

			CentroSanitario centroSanitario = repositoryCentro.findByNombre(cita.getNombreCentro());

			Cupo cupo = repositoryCupo.findAllByCentroSanitarioAndFechaAndHora(centroSanitario, cita.getFecha(),
					cita.getHora());
			cupo.setPersonasRestantes(cupo.getPersonasRestantes() + 1);

			repositoryCita.deleteById(id);
			repositoryCupo.save(cupo);
		} catch (ResponseStatusException e) {
			throw new VacunaException(e.getStatus(), e.getMessage());
		}
	}

	@GetMapping("/getCitaPaciente/{dni}")
	/***
	 * Obtenemos la cita de un paciente
	 * 
	 * @param dni
	 * @return null
	 */
	public List<Cita> getCitaPaciente(@PathVariable String dni) {
		return repositoryCita.findAllByDniPaciente(dni);
	}

	@GetMapping("/getCitaPorDia/{fecha}")
	public List<Cita> getCitasPorDia(HttpSession session, @PathVariable String fecha) {
		String email = (String) session.getAttribute("email");

		List<Cita> citas = repositoryCita
				.findAllByNombreCentroAndFecha(repositoryUsuario.findByDni(email).getCentroAsignado(), fecha);

		return citas;
	}

	@GetMapping("/getCitasPaciente/{dni}")
	/***
	 * Obtenemos la cita de un paciente
	 * 
	 * @param dni
	 * @return lista de citas
	 */
	public List<Cita> getCitasPaciente(@PathVariable String dni) {
		return repositoryCita.findAllByDniPaciente(dni);
	}

	@GetMapping("/")
	/***
	 * Obtenemos todas las citas
	 * 
	 * @return repositorio de citas
	 */
	public List<Cita> readAll() {
		return repositoryCita.findAll();
	}

	@Transactional
	@PutMapping("/definirFormatoVacunacion")
	/***
	 * Modificamos una cita
	 * 
	 * @param session
	 * @param info
	 * @return Formato de vacunacion creado
	 * @throws ParseException
	 * @throws DiasEntreDosisIncorrectosException
	 * @throws NoHayDosisException
	 * @throws SlotVacunacionSuperadoException
	 */
	public void definirFormatoVacunacion(HttpSession session, @RequestBody Map<String, Object> datosFormatoVacunacion)
			throws ControlHorasVacunacionException {

		JSONObject jso = new JSONObject(datosFormatoVacunacion);
		String horaInicio = jso.getString("horaInicio");
		String horaFin = jso.getString("horaFin");
		int duracionFranja = jso.getInt("duracionFranja");
		int personasAVacunar = jso.getInt("personasAVacunar");

		FormatoVacunacion formatoVacunacion = new FormatoVacunacion(horaInicio, horaFin, duracionFranja,
				personasAVacunar);
		if (formatoVacunacion.horasCorrectas()) {
			repositoryFormatoVacunacion.insert(formatoVacunacion);
		} else {
			throw new ControlHorasVacunacionException();
		}

	}

	/***
	 * Creamos una cita
	 * 
	 * @param nombreCentro
	 * @param dniUsuario
	 * @param nombre
	 * @return cita creada
	 * @throws SlotVacunacionSuperadoException
	 * @throws NoHayDosisException
	 */
	@PostMapping("/solicitarCita")
	public void solicitarCita(@RequestBody Map<String, Object> info, HttpSession session)
			throws UsuarioNoExisteException, CupoNoEncontradoException {
	
		JSONObject json = new JSONObject(info);
		String email = json.getString("email");
		try {
			Optional<Usuario> optPaciente = repositoryUsuario.findById(email);
			Paciente paciente = null;
			if (optPaciente.isPresent() && optPaciente.get().getTipoUsuario().equals("Paciente")) {
				paciente = (Paciente) optPaciente.get();
			}

			if (paciente == null)
				throw new UsuarioNoExisteException();

			List<Cita> listaCitas = repositoryCita.findAllByDniPaciente(paciente.getDni());
			int citasAsignadas = listaCitas.size();
			switch (citasAsignadas) {
			case 0:
				// Primera
				asignarDosis(paciente, LocalDate.now());
				// Segunda
				asignarDosis(paciente, LocalDate.now().plusDays(21));
				break;

			case 1:
				Cita primeraDosis = repositoryCita.findByDniPaciente(paciente.getDni());
				LocalDate fechaPrimeraCita = LocalDate.parse(primeraDosis.getFecha());

				// Asignar SegundaDosis
				asignarDosis(paciente, fechaPrimeraCita.plusDays(21));

				break;

			case 2:
				if (paciente.getDosisAdministradas().equals("2"))
					throw new VacunaException(HttpStatus.FORBIDDEN, "El paciente ya tiene dos dosis administradas");
				throw new VacunaException(HttpStatus.FORBIDDEN, "El paciente: " + paciente.getDni()
						+ " ya dispone de dos citas asignadas. Si desea modificar su cita, utilice Modificar Cita");

			default:
				break;
			}
		} catch (VacunaException e) {
			throw new ResponseStatusException(e.getStatus(), e.getMessage());
		}

	}

	@GetMapping("/getCitasHoy/{email}")
	public List<Cita> getCitasHoy(@PathVariable("email") String email) {
		String fecha = LocalDate.now().toString();
		return getCitasPorDia(fecha, email);
	}
	
	@GetMapping("/getCitasOtroDia/{email}/{fecha}")
	public List<Cita> getCitasOtroDia(HttpServletRequest session, @PathVariable("email") String email,
			@PathVariable("fecha") String fecha) {
		return getCitasPorDia(fecha, email);
	}

	public List<Cita> getCitasPorDia(String fecha, String email) {
		return repositoryCita.findAllByNombreCentroAndFecha(repositoryUsuario.findByEmail(email).getCentroAsignado(),
				fecha);
	}

	private Cupo buscarCupoLibre(LocalDate fechaActualDate, CentroSanitario CentroSanitario) {
		Cupo cupo = null;

		// Para poder coger siempre la primera con un hueco libre por fecha
		List<Cupo> listaCupos = repositoryCupo.findAllByCentroSanitario(CentroSanitario);
		listaCupos.sort(Comparator.comparing(Cupo::getFecha));

		for (int i = 0; i < listaCupos.size(); i++) {
			cupo = listaCupos.get(i);
			if (LocalDate.parse(cupo.getFecha()).isAfter(fechaActualDate) && cupo.getPersonasRestantes() > 0) {
				break;
			}
		}
		return cupo;
	}

	public void asignarDosis(Usuario usuario, LocalDate fechaActual) throws CupoNoEncontradoException {
		CentroSanitario centro = repositoryCentro.findByNombre(usuario.getCentroAsignado());
		Cupo cupoAsignado = buscarCupoLibre(fechaActual, centro);
		if (cupoAsignado == null) {
			throw new CupoNoEncontradoException();
		}
		repositoryCupo.deleteByFechaAndHoraAndCentroSanitario(cupoAsignado.getFecha(), cupoAsignado.getHora(),
				cupoAsignado.getCentroVacunacion());
		cupoAsignado.setPersonasRestantes(cupoAsignado.getPersonasRestantes() - 1);
		repositoryCupo.save(cupoAsignado);

		Cita cita = new Cita(cupoAsignado.getFecha(), cupoAsignado.getHora(), usuario.getCentroAsignado(),
				usuario.getDni());
		repositoryCita.save(cita);

	}

	@PostMapping("/modificarCita")
	public void modificarCita(HttpSession session, @RequestBody Map<String, Object> datosCita) throws VacunaException {
		try {
			JSONObject json = new JSONObject(datosCita);
			String idCita = json.getString("idCita");
			String idCupo = json.getString("idCupo");
			String dniPaciente = json.getString("dniPaciente");

			Optional<Cita> citaaModificar = repositoryCita.findById(idCita);
			Cita citaModificar = citaaModificar.get();
			Optional<Cupo> optCupoElegido = repositoryCupo.findById(idCupo);
			Cupo cupoElegido = new Cupo();

			if (optCupoElegido.isPresent())
				cupoElegido = optCupoElegido.get();

			List<Cita> listaCitas = repositoryCita.findAllByDniPaciente(dniPaciente);
			listaCitas.sort(Comparator.comparing(Cita::getFecha));
			int citasAsignadas = listaCitas.size();

			if (citasAsignadas < 1)
				throw new VacunaException(HttpStatus.NOT_FOUND,
						"No se puede modificar citas puesto que no dispone de ninguna cita asignada");

			if (cupoElegido.getPersonasRestantes() < 1)
				throw new VacunaException(HttpStatus.FORBIDDEN,
						"No hay hueco para cita el dia " + cupoElegido.getFecha() + " a las " + cupoElegido.getHora());

			if (citaModificar.isUsada())
				throw new VacunaException(HttpStatus.NOT_FOUND,
						"No se puede modificar su cita puesto que ya está vacunado");

			if (listaCitas.size() == 2) {
				int indiceCita = -1;
				if (citaModificar.getId().equalsIgnoreCase(listaCitas.get(0).getId()))
					indiceCita = 0;
				else if (citaModificar.getId().equalsIgnoreCase(listaCitas.get(1).getId()))
					indiceCita = 1;

				switch (indiceCita) {
				case 0:
					if (LocalDate.parse(cupoElegido.getFecha()).isAfter(LocalDate.parse(listaCitas.get(1).getFecha()))
							|| LocalDate.parse(cupoElegido.getFecha())
									.isEqual(LocalDate.parse(listaCitas.get(1).getFecha())))
						throw new VacunaException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS,
								"No se puede poner la primera cita el mismo dia o un dia posterior a la primera");
					break;
				case 1:
					if (LocalDate.parse(cupoElegido.getFecha())
							.isBefore(LocalDate.parse(listaCitas.get(0).getFecha()).plusDays(21)))
						throw new VacunaException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS,
								"No se puede poner la primera cita el mismo dia o un dia posterior a la primera");
					break;
				default:
					break;
				}
			}
			citaModificar.setFecha(cupoElegido.getFecha());
			citaModificar.setHora(cupoElegido.getHora());
			repositoryCita.save(citaModificar);

			cupoElegido.setPersonasRestantes(cupoElegido.getPersonasRestantes() - 1);
			repositoryCupo.save(cupoElegido);

		} catch (ResponseStatusException e) {
			if (e.getStatus() == HttpStatus.FORBIDDEN) {
				throw new VacunaException(HttpStatus.FORBIDDEN, e.getMessage());
			} else if (e.getStatus() == HttpStatus.NOT_FOUND) {
				throw new VacunaException(HttpStatus.NOT_FOUND, e.getMessage());
			}
		}
	}

}