package com.vacuna.vacuna.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
import com.vacuna.vacuna.dao.UsuarioDAO;
import com.vacuna.vacuna.exception.CentrosNoEncontradosException;
import com.vacuna.vacuna.exception.DatosIncompletosException;
import com.vacuna.vacuna.exception.DniNoValidoException;
import com.vacuna.vacuna.exception.EmailExistenteException;
import com.vacuna.vacuna.exception.EmailIncorrectoException;
import com.vacuna.vacuna.exception.PasswordIncorrectaException;
import com.vacuna.vacuna.exception.PasswordNoCoincideException;
import com.vacuna.vacuna.exception.UsuarioExistenteException;
import com.vacuna.vacuna.exception.UsuarioNoEliminadoException;
import com.vacuna.vacuna.exception.UsuarioNoExisteException;
import com.vacuna.vacuna.exception.UsuariosNoEncontradosException;
import com.vacuna.vacuna.model.Administrador;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cita;
import com.vacuna.vacuna.model.Paciente;
import com.vacuna.vacuna.model.PersonalDeCitas;
import com.vacuna.vacuna.model.Sanitario;
import com.vacuna.vacuna.model.Usuario;

/***
 * 
 * @author crist
 *
 */
@RestController
@RequestMapping("Usuario")

public class UsuarioController {
 
	@Autowired
	private UsuarioDAO repository;
	  
	@Autowired
	private CitaDAO repositoryCita;
	
	@Autowired
	private CentroSanitarioDAO repositoryCentro;

	@Autowired
	private CentroSanitarioDAO csrepository;
	
	private static final String PACIENTE = "Paciente";
	private String regexEmail = "^(.+)@(.+)(.+).(.+)$";
	private String regex = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$";
	
	@PutMapping("/add")
	/***
	 * Metodo para a??adir un usuario
	 * @param info
	 * @throws NoSuchAlgorithmException
	 * @throws PasswordNoCoincideException
	 * @throws DatosIncompletosException
	 * @throws DniNoValidoException
	 * @throws EmailIncorrectoException
	 * @throws EmailExistenteException
	 * @throws PasswordIncorrectaException
	 * @throws UsuarioExistenteException
	 * @throws UsuarioNoExisteException
	 */
	public void add(@RequestBody Map<String, Object> info) throws NoSuchAlgorithmException, PasswordNoCoincideException, DatosIncompletosException, 
	DniNoValidoException, EmailIncorrectoException, EmailExistenteException, PasswordIncorrectaException, UsuarioExistenteException, UsuarioNoExisteException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		JSONObject jso = new JSONObject(info);
		String nombre = jso.optString("nombre");
		String email = jso.optString("email");
		String dni = jso.optString("dni"); 
		String tipoUsuario = jso.optString("tipoUsuario");
		String password = jso.optString("password"); 
		String password2 = jso.optString("password2");
		String centroAsignado = jso.optString("centroAsignado");
		String localidad = jso.optString("localidad");
		String provincia = jso.optString("provincia");
		
		if (!password.equals(password2))
			throw new PasswordNoCoincideException();

		byte[] pwd = md.digest(password.getBytes());
		
		Usuario u;
		
		if(!formValido(nombre, email, dni, centroAsignado, tipoUsuario, password)) {
			throw new DatosIncompletosException();
		}
		validarDNI(dni); 
		validarEmail(email);
		validarPassword(password);
		
		if (repository.findByEmail(email) != null) {
			throw new UsuarioExistenteException();
		}
		
		
		switch(tipoUsuario) {
		case "Administrador":
			u = new Administrador(nombre, email, pwd, dni, tipoUsuario, centroAsignado);
			break;
		case PACIENTE:
			if(!formValido(provincia, localidad)) {
				throw new DatosIncompletosException();
			}
			u = new Paciente(nombre, email, pwd, dni, tipoUsuario, centroAsignado, "0", localidad, provincia);
			break;
		case "Sanitario":
			u = new Sanitario(nombre, email, pwd, dni, tipoUsuario, centroAsignado);
			break;
		case "Personal de Citas":
			u = new PersonalDeCitas(nombre, email, pwd, dni, tipoUsuario, centroAsignado);
			break;
		default:
			throw new UsuarioNoExisteException();
		}
			
		repository.insert(u);

	} 
	
	@GetMapping("/getNombrePaciente/{dni}")
	/***
	 * Metodo para obtener el nombre del paciente buscandolo por su dni
	 * @param dni
	 * @return nombre usuario
	 */
	public String getNombrePaciente(@PathVariable String dni){
		Paciente u = (Paciente) repository.findByDni(dni);
		return u.getNombre();	
	}

	@GetMapping("/getTodos")
	/***
	 * Metodo para buscar los usuarios
	 * @return usuario
	 * @throws UsuariosNoEncontradosException
	 */
	public List<Usuario> get() throws UsuariosNoEncontradosException {
		try {
			return repository.findAll();
		} catch (Exception e) {
			throw new UsuariosNoEncontradosException();
		}
	}

	@GetMapping("/getCentros")
	/***
	 * Metodo para obetner los centros sanitarios
	 * @return centro sanitario
	 * @throws CentrosNoEncontradosException
	 */
	public List<CentroSanitario> devolverCentro() throws CentrosNoEncontradosException {
		try {
			return csrepository.findAll();

		} catch (Exception e) {
			throw new CentrosNoEncontradosException();
		}

	}

	@Transactional
	@DeleteMapping("/eliminarUsuario/{email}")
	/***
	 * Metodo para eliminar el usuario 
	 * @param email
	 * @return repositorio actualizado
	 * @throws UsuarioNoExisteException
	 * @throws UsuarioNoEliminadoException
	 */
	public Usuario eliminarUsuario(@PathVariable String email) throws UsuarioNoExisteException, UsuarioNoEliminadoException {
		if (repository.findByEmail(email) == null) {
			throw new UsuarioNoExisteException();
		} 
		if (repository.findByEmail(email).getTipoUsuario().equals("Administrador")) {
			throw new UsuarioNoEliminadoException();
		}
		if (repository.findByEmail(email).getTipoUsuario().equals(PACIENTE)) {
			Paciente p = (Paciente) repository.findByEmail(email);
			List<Cita> c = repositoryCita.findAllByDniPaciente(p.getDni());
			
			if(!p.getDosisAdministradas().equals("0")) {
				throw new UsuarioNoEliminadoException();
			}
			
			for(Cita cita: c) {
				repositoryCita.deleteById(cita.getId());
			}
			
		}
		return repository.deleteByEmail(email);
	}
	
	@PostMapping("/modificarUsuarios")
	/***
	 * Metodo para modificar un usuario
	 * @param info
	 * @throws NoSuchAlgorithmException 
	 * @throws DatosIncompletosException 
	 * @throws UsuarioNoExisteException 
	 * @throws Exception
	 */
	public void update(@RequestBody Map<String, Object> info) throws NoSuchAlgorithmException, DatosIncompletosException, UsuarioNoExisteException  {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		JSONObject jso = new JSONObject(info);
		String nombre = jso.optString("nombre");
		String email = jso.optString("email");
		String password = jso.optString("password");
		String centroAsignado = jso.optString("centroAsignado");
		String localidad = jso.optString("localidad");
		String provincia = jso.optString("provincia");
		
		
		byte[] pwd = md.digest(password.getBytes());
		Usuario u =  repository.findByEmail(email);
		if(!formValido(nombre, email, centroAsignado)) {
			throw new DatosIncompletosException();
		}
		if (u == null) {
			throw new UsuarioNoExisteException();
		}
		if(password.length() > 0)
			u.setPassword(pwd);
		
		u.setNombre(nombre);
		u.setCentroAsignado(centroAsignado); 
		
		if(u instanceof Paciente) {
			if(!formValido(provincia, localidad)) {
				throw new DatosIncompletosException();
			}
			Paciente p = (Paciente)u;
			p.setProvincia(provincia);
			p.setLocalidad(localidad);
		}
		repository.save(u);
		
	}
	
	@GetMapping("/buscarEmail/{email}")
	/***
	 * MEtodo para leer los usuarios
	 * @param email
	 * @return email
	 * @throws UsuarioNoExisteException
	 */
	public Usuario readAll(@PathVariable String email) throws UsuarioNoExisteException {
		if (repository.findByEmail(email) == null) {
			throw new UsuarioNoExisteException();
		}
		return repository.findByEmail(email);
	}
	
	@GetMapping("/cogerTipoUsuario/{email}")
	/***
	 * Metodo para obtener los tipos de los usuarios
	 * @param email
	 * @return tipo usuario
	 */
	public String cogerTipoUsuario(@PathVariable String email) {
		Usuario u =  repository.findByEmail(email);
		return u.getTipoUsuario();
		
	}

	/***
	 * Metodo que comprueba que los valores de una cadena son validos
	 * @param values
	 * @return valid
	 */
	public boolean formValido(String... values) {
		boolean valid = values.length > 0;
		for(String value : values) {
			if(value.length() == 0) {
				valid = false;
				break;
			}
		}
		return valid;
	}
	
	/***
	 * Metodo para validar el DNI
	 * @param dni
	 * @return valido
	 * @throws DniNoValidoException
	 */
	public boolean validarDNI(String dni) throws DniNoValidoException {
		boolean valido = true;
		if (dni.length() != 9) {
			throw new DniNoValidoException();
		}
		String entero = dni.substring(0, 8);

		if (!entero.matches("[0-9]+")) {
			throw new DniNoValidoException();
		}
		String letra = dni.substring(8, 9);
		if (letra.matches("[0-9]+")) {
			throw new DniNoValidoException();
		}
		return valido;
	}
	
	/***
	 * Metodo para validar Email
	 * @param email
	 * @return true
	 * @throws EmailIncorrectoException
	 * @throws EmailExistenteException
	 */
	public boolean validarEmail(String email) throws EmailIncorrectoException, EmailExistenteException {
		if(!email.matches(regexEmail)) {
			throw new EmailIncorrectoException();	
		}else if (repository.findByEmail(email) != null) {
			throw new EmailExistenteException();
		}
		return true;
	}
	
	/***
	 * Metodo para validar password
	 * @param password
	 * @return true
	 * @throws PasswordIncorrectaException
	 */
	public boolean validarPassword(String password) throws PasswordIncorrectaException {
		if(!password.matches(regex)) {
			throw new PasswordIncorrectaException();
		}
		return true;
	}
	
	@PostMapping("/marcarVacunado")
	public void marcarVacunado(HttpSession session, @RequestBody Map<String, Object> datosPaciente) {
		try {
			JSONObject jsonPaciente = new JSONObject(datosPaciente);
			String dni = jsonPaciente.getString("dni");
			Usuario usuarioVacunado = repository.findByDniAndTipoUsuario(dni, PACIENTE);
			CentroSanitario centroVacunacion = repositoryCentro.findByNombre(usuarioVacunado.getCentroAsignado());

			String fechaHoy = LocalDate.now().toString();
			Cita citaDeEseDia = repositoryCita.findByDniPacienteAndFecha(usuarioVacunado.getDni(), fechaHoy);

			if (citaDeEseDia.isUsada())
				throw new ResponseStatusException(HttpStatus.CONFLICT,
						"No se puede vacunar un paciente que ha sido vacunado hoy mismo");

			centroVacunacion.setDosisTotales(centroVacunacion.getDosisTotales() - 1);
			repositoryCentro.save(centroVacunacion);

			citaDeEseDia.setUsada(true);
			repositoryCita.save(citaDeEseDia);
		} catch (ResponseStatusException e) {
			throw new ResponseStatusException(e.getStatus(), e.getMessage());
		}

	}
}