
package com.vacuna.vacuna.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.vacuna.vacuna.dao.PacienteDAO;
import com.vacuna.vacuna.dao.UsuarioDAO;
import com.vacuna.vacuna.exception.CentrosNoEncontradosException;
import com.vacuna.vacuna.exception.CitasNoEncontradasException;
import com.vacuna.vacuna.exception.ControlHorasVacunacionException;
import com.vacuna.vacuna.exception.CupoNoEncontradoException;
import com.vacuna.vacuna.exception.DiasEntreDosisIncorrectosException;
import com.vacuna.vacuna.exception.ErrorDosisAdministradasException;
import com.vacuna.vacuna.exception.NoHayDosisException;
import com.vacuna.vacuna.exception.SlotVacunacionSuperadoException;
import com.vacuna.vacuna.exception.UsuarioNoExisteException;
import com.vacuna.vacuna.exception.VacunaException;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cita;
import com.vacuna.vacuna.model.Paciente;
import com.vacuna.vacuna.model.Usuario;

import edu.esi.uclm.exceptions.SigevaException;

import com.vacuna.vacuna.model.Cupo;

import com.vacuna.vacuna.model.FormatoVacunacion;

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
	
	@Autowired
	private PacienteDAO repositoryPaciente;

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

	@Transactional
	@DeleteMapping("/eliminarCitaCompleta/{id}")
	/***
	 * Eliminamos las dos citas, la de la primera dosis y la de la segunda
	 * 
	 * @param id
	 * @return null
	 */
	public Cita eliminar1CitaCompleta(@PathVariable String id) {
		Optional<Cita> c = repositoryCita.findById(id);

		if (c.isPresent()) {
			Cita citaAux = new Cita();
			citaAux = c.get();
			Paciente p = (Paciente) repositoryUsuario.findByDni(c.get().getDniPaciente());
			p.setDosisAdministradas("0");
			repositoryUsuario.save(p);
			String nombreCentro = citaAux.getNombreCentro();
			CentroSanitario cs = repositoryCentro.findByNombre(nombreCentro);
			cs.setDosisTotales(cs.getDosisTotales() + 1);
			repositoryCentro.save(cs);
			repositoryCita.deleteById(id);
		}

		return null;
	}

	@Transactional
	@PutMapping("/eliminarCita/{id}")
	/***
	 * Eliminamos solo una cita
	 * 
	 * @param id
	 * @param info
	 * @return cita eliminada
	 * @throws ParseException
	 */
	public Cita eliminarCita(@PathVariable String id, @RequestBody Map<String, Object> info) throws ParseException {

		Optional<Cita> c = repositoryCita.findById(id);
		Cita cita = new Cita();

		if (c.isPresent()) {
			cita = c.get();
		}

		JSONObject jso = new JSONObject(info);
		String nombreCentro = jso.optString("centrosSanitarios");
		String fechaPrimeraMod = jso.getString("fechaPrimeraDosis");

		CentroSanitario cs = repositoryCentro.findByNombre(nombreCentro);
		cs.setDosisTotales(cs.getDosisTotales() + 1);
		repositoryCentro.save(cs);

		DateFormat fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date citaMod = fechaHora.parse(fechaPrimeraMod);

		/*
		 * cita.setFechaPrimeraDosis(citaMod.getTime());
		 * 
		 * cita.setFechaSegundaDosis(0);
		 */

		return repositoryCita.save(cita);
	}

	@DeleteMapping("/anularCita")
	public void anularCita(HttpSession session, @RequestBody Map<String, Object> info) {
		try {
			JSONObject json = new JSONObject(info);
			String idCita = json.getString("idCita");
			Cita cita = repositoryCita.findByIdCita(idCita);

			if (cita.isUsada())
				throw new ResponseStatusException(HttpStatus.CONFLICT, "La cita que intenta anular ya ha sido utilizada.");

			Cupo cupo = repositoryCupo.findAllByCentroSanitarioAndFechaAndHora(cita.getNombreCentro(), cita.getFecha(),
					cita.getHora());
			cupo.setPersonasRestantes(cupo.getPersonasRestantes() + 1);

			repositoryCita.deleteById(idCita);
			repositoryCupo.save(cupo);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus(), e.getMessage());
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
		String email = (String)session.getAttribute("email");		
		
		List<Cita> citas = repositoryCita.findAllByNombreCentroAndFecha(repositoryUsuario.findByDni(email).getCentroAsignado(), fecha);
		
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

	@SuppressWarnings("deprecation")
	@Transactional
	@PutMapping("/modificarCita/{id}")
	/***
	 * Modificamos una cita
	 * 
	 * @param id
	 * @param info
	 * @return cita modificada
	 * @throws ParseException
	 * @throws DiasEntreDosisIncorrectosException
	 * @throws NoHayDosisException
	 * @throws SlotVacunacionSuperadoException
	 */
	public Cita modificarCita(@PathVariable String id, @RequestBody Map<String, Object> info) throws ParseException,
			DiasEntreDosisIncorrectosException, NoHayDosisException, SlotVacunacionSuperadoException {
		Optional<Cita> c = repositoryCita.findById(id);

		if (!c.isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No exite dicha cita");
		}
		Cita cita = c.get();
		JSONObject jso = new JSONObject(info);
		String dniPaciente = jso.optString("dniPaciente");

		String nombreCentro = jso.optString("centrosSanitarios");
		String fechaPrimeraMod = jso.getString("fechaPrimeraDosis");
		String fechaSegundaMod = jso.getString("fechaSegundaDosis");
		Paciente usuario = (Paciente) repositoryUsuario.findByDni(c.get().getDniPaciente());

		Date today = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Date dataFormateada = formato.parse(fechaPrimeraMod);
		Date dataFormateada2 = formato.parse(fechaSegundaMod);

		String fechaHOY = LocalDate.now().toString();
		long fechaPrimeraDosisMS = new Date(dataFormateada.getYear(), dataFormateada.getMonth(),
				dataFormateada.getDate(), 0, 0).getTime();
		long fechaSegundaDosisMS = new Date(dataFormateada2.getYear(), dataFormateada2.getMonth(),
				dataFormateada2.getDate(), 0, 0).getTime();

		long fechaLimitePD = 1641772800000L;// 10 de enero de 2022
		long fechaLimiteSD = 1643587200000L;// 31 de enero de 2022
		if (fechaPrimeraDosisMS <= fechaHOY || fechaSegundaDosisMS <= fechaHOY) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedes viajar en el tiempo");
		}
		if (fechaLimitePD <= fechaPrimeraDosisMS || fechaLimiteSD <= fechaSegundaDosisMS) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedes superar las fechas limites");
		}

		String nombre = usuario.getNombre();
		Cita citaModificada = modificar(dniPaciente, nombreCentro, fechaPrimeraMod, fechaSegundaMod, nombre);
		cita.setFechaPrimeraDosis(citaModificada.getFechaPrimeraDosis());
		cita.setFechaSegundaDosis(citaModificada.getFechaSegundaDosis());
		return repositoryCita.save(cita);
	}

	@PostMapping("/modificarCita")
	public void modificarCita(HttpSession session, @RequestBody Map<String, Object> datosCita) {

		try {

			JSONObject json = new JSONObject(datosCita);
			String idCita = json.getString("idCita");
			String idCupo = json.getString("idCupo");
			String emailUsuario = (String) session.getAttribute("emailUsuario");

			Usuario usuario = repositoryUsuario.findByEmail(emailUsuario);
			Cita citaModificar = repositoryCita.findByIdCita(idCita);
			Optional<Cupo> optCupoElegido = repositoryCupo.findById(idCupo);
			Cupo cupoElegido = new Cupo();

			if (optCupoElegido.isPresent())
				cupoElegido = optCupoElegido.get();

			List<Cita> listaCitas = repositoryCita.findAllByUsuarioEmail(usuario.getEmail());
			//listaCitas.sort(Comparator.comparing(Cita::getFecha));
			int citasAsignadas = listaCitas.size();
			
			if (citasAsignadas < 1)
				throw new ResponseStatusException (HttpStatus.NOT_FOUND,
						"No se puede modificar citas puesto que no dispone de ninguna cita asignada");

			if (cupoElegido.getPersonasRestantes() < 1)
				throw new ResponseStatusException (HttpStatus.FORBIDDEN,
						"No hay hueco para cita el dia " + cupoElegido.getFecha() + " a las " + cupoElegido.getHora());

			if (citaModificar.isUsada())
				throw new ResponseStatusException (HttpStatus.NOT_FOUND,
						"No se puede modificar su cita puesto que ya está vacunado");
			
			if(listaCitas.size()==2) {
				int indiceCita = -1;
				if(citaModificar.getIdCita().equalsIgnoreCase(listaCitas.get(0).getIdCita()))
					indiceCita = 0;
				else if(citaModificar.getIdCita().equalsIgnoreCase(listaCitas.get(1).getIdCita()))
					indiceCita = 1;

				switch (indiceCita) {
				case 0:
					if (LocalDate.parse(cupoElegido.getFecha()).isAfter(LocalDate.parse(listaCitas.get(1).getFecha()))
							|| LocalDate.parse(cupoElegido.getFecha())
							.isEqual(LocalDate.parse(listaCitas.get(1).getFecha())))
						throw new SigevaException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS,
								"No se puede poner la primera cita el mismo dia o un dia posterior a la primera");
					break;
				case 1:
					if (LocalDate.parse(cupoElegido.getFecha())
							.isBefore(LocalDate.parse(listaCitas.get(0).getFecha()).plusDays(21)))
						throw new SigevaException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS,
								"No se puede poner la primera cita el mismo dia o un dia posterior a la primera");
					break;
				default:
					break;
				}
			
			citaModificar.setFecha(cupoElegido.getFecha());
			citaModificar.setHora(cupoElegido.getHora());
			repositoryCita.save(citaModificar);

			cupoElegido.setPersonasRestantes(cupoElegido.getPersonasRestantes() - 1);
			repositoryCupo.save(cupoElegido);

		} catch (ResponseStatusException  e) {
			if(e.getStatus() == HttpStatus.FORBIDDEN) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
			}else if(e.getStatus() == HttpStatus.NOT_FOUND) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
			}		}
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

	private CentroSanitario obtenerCentro(String nombreCentro) {
		List<CentroSanitario> listaCentros = repositoryCentro.findAll();
		for (int i = 0; i < listaCentros.size(); i++) {
			if (listaCentros.get(i).getNombre().equals(nombreCentro)) {
				centroSanitario = listaCentros.get(i);
				return centroSanitario;
			}
		}
		return centroSanitario;
	}

	private void comprobarFechas(LocalDate fecha2, LocalDate fecha1) throws DiasEntreDosisIncorrectosException {
		if (fecha2.isBefore(fecha1.plusDays(21))) {
			throw new DiasEntreDosisIncorrectosException();
		}
	}

	private void fechasSlot(LocalDate fechaActual, LocalDate nocheVieja) throws SlotVacunacionSuperadoException {
		LocalDate fechaLimite = LocalDate.parse("" + LocalDate.now().getYear() + "-12-11");
		if (fechaActual.isAfter(nocheVieja) || fechaActual.isAfter(fechaLimite)) {
			throw new SlotVacunacionSuperadoException();
		}
	}

	private void fechasSlot1(Long aux1, Long nocheVieja, Long aux2) throws SlotVacunacionSuperadoException {
		if ((aux1 >= nocheVieja) || (aux2 >= nocheVieja)) {
			throw new SlotVacunacionSuperadoException();
		}
	}

	@SuppressWarnings("deprecation")
	/***
	 * Modificamos una cita controlando mas excepciones
	 * 
	 * @param dniPaciente
	 * @param nombreCentro
	 * @param fechaPrimeraMod
	 * @param fechaSegundaMod
	 * @param nombre
	 * @return cita modificada
	 * @throws ParseException
	 * @throws DiasEntreDosisIncorrectosException
	 * @throws NoHayDosisException
	 * @throws SlotVacunacionSuperadoException
	 */
	private Cita modificar(String dniPaciente, String nombreCentro, String fechaPrimeraMod, String fechaSegundaMod,
			String nombre) throws ParseException, DiasEntreDosisIncorrectosException, NoHayDosisException,
			SlotVacunacionSuperadoException {
		List<Cita> listaCitas = repositoryCita.findAll();
		Cita c = new Cita();
		CentroSanitario centroSanitario = obtenerCentro(nombreCentro);

		LocalDate fecha1 = LocalDate.parse(fechaPrimeraMod);
		LocalDate fecha2 = LocalDate.parse(fechaSegundaMod);

		comprobarFechas(fecha2, fecha1);

		LocalDate fechaActual = LocalDate.now();
		String nocheVieja = "" + LocalDate.now().getYear() + "-12-31"; // Dia 31 de Enero
		int contadorAforo = 0; // Aforo para el centro que cogemos
		boolean asignada = true;
		if (listaCitas.isEmpty()) {
			if (centroSanitario.getDosisTotales() >= 2) {
				++contadorAforo;
				c = new Cita();
				c.setNombreCentro(centroSanitario.getNombre());
				c.setFechaPrimeraDosis(aux1);

				c.setFechaSegundaDosis(aux2);
				c.setDniPaciente(dniPaciente);
				c.setNombrePaciente(nombre);
				centroSanitario.setDosisTotales(centroSanitario.getDosisTotales() - 2);
				repositoryCentro.save(centroSanitario);
				return c;
			} else {
				throw new NoHayDosisException();
			}
		} else {
			asignada = false;
			while (!asignada) {
				for (int i = 0; i < listaCitas.size(); i++) { // Esto ahora mismo no hace nada
					if ((fecha1.getTime() == fechaActual) || (fecha2.getTime() == fechaActual)) {
						++contadorAforo;
					}
				}

				if (contadorAforo >= centroSanitario.getAforo()) {
					if (new Date(fechaActual).getHours() == centroSanitario.getHoraFin()) {
						fechaActual += (3600000 * 12); // Proximo dia a las 08.00am
					} else {
						fechaActual += 3600000; // Siguiente rango de horas
					}

					fechasSlot1(aux1, nocheVieja, aux2);

					contadorAforo = 0;
				} else {
					++contadorAforo;
					c = new Cita();
					c.setNombreCentro(centroSanitario.getNombre());
					c.setFechaPrimeraDosis(aux1);
					c.setFechaSegundaDosis(aux2);
					c.setDniPaciente(dniPaciente);
					c.setNombrePaciente(nombre);
					centroSanitario.setDosisTotales(centroSanitario.getDosisTotales() - 2);
					repositoryCentro.save(centroSanitario);
					asignada = true;
					return c;

				}
			}
		}

		return c;
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
	public void solicitarCita(@RequestBody Map<String, Object> info)
			throws UsuarioNoExisteException, CupoNoEncontradoException {
		JSONObject json = new JSONObject(info);
		String email = json.getString("email");
		try {
			Optional<Usuario> optPaciente = repositoryUsuario.findById(email);
			Paciente paciente = null;
			if(optPaciente.isPresent() && optPaciente.get().getTipoUsuario().equals("Paciente")) {
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

	@GetMapping("/getCitasOtroDia/{email}/{fecha}")
	public List<Cita> getCitasOtroDia(HttpServletRequest session, @PathVariable("email") String email, @PathVariable("fecha") String fecha) {
		return getCitasPorDia(fecha, email);
	}
	
	public List<Cita> getCitasPorDia(String fecha, String email) {
		return repositoryCita.findAllByNombreCentroAndFecha( repositoryUsuario.findByEmail(email).getCentroAsignado(),fecha);
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
}