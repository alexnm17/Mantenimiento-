package com.vacuna.vacuna.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vacuna.vacuna.dao.CentroSanitarioDAO;
import com.vacuna.vacuna.dao.UsuarioDAO;
import com.vacuna.vacuna.exception.CentroNoEliminadoException;
import com.vacuna.vacuna.exception.CentroNoExisteException;
import com.vacuna.vacuna.exception.CentrosNoEncontradosException;
import com.vacuna.vacuna.exception.ControlHorasVacunacionException;
import com.vacuna.vacuna.exception.DatosIncompletosException;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Usuario;

@RestController
/***
 * 
 * @author crist
 *
 */
@RequestMapping("centro")
public class CentroController {

	@Autowired
	private CentroSanitarioDAO repository;

	 
	@Autowired
	private UsuarioDAO repositoryUsuario; 

	@PutMapping("/add")
	/***
	 * Creamos un nuevo centro 
	 * @param info
	 * @return centroSanitario creado
	 * @throws ControlHorasVacunacionException
	 * @throws DatosIncompletosException
	 */
	public CentroSanitario add(@RequestBody Map<String, Object> info) throws DatosIncompletosException {
		JSONObject jso = new JSONObject(info);
		String nombre = jso.optString("nombre");
		String dosisT = jso.optString("dosisTotales");
		String localidad = jso.optString("localidad"); 
		String provincia = jso.optString("provincia");
		if(!formValido(nombre, dosisT, localidad, provincia)) {
			throw new DatosIncompletosException();
		}
		int dosisTotales = Integer.parseInt(dosisT);
		CentroSanitario c = new CentroSanitario(nombre, dosisTotales, localidad, provincia);
		  
		return repository.insert(c);

	}
	
	/***
	 * Comprobamos si los valores de la cadena son validos
	 * @param values
	 * @return valido
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


	@GetMapping("/getTodos")
	/***
	 * Leemos la lista de Centros
	 * @throws CentrosNoEncontradosException
	 * @return repositorio de centros
	 */
	public List<CentroSanitario> get() throws CentrosNoEncontradosException {
		try {
			return repository.findAll();
		} catch (Exception e) {
			throw new CentrosNoEncontradosException();
		}
	}
	
	@GetMapping("/comprobarCentros")
	/***
	 * Comprobamos si hay centros
	 * @return 1
	 * @return 0
	 */
	public String comprobarCentros() {
			 if(repository.findAll().isEmpty()) {
				 return "1";	 
			 }
		return "0";

	}

	@Transactional
	@PutMapping("/modificarCentro/{id}")
	/***
	 * Modificamos un centro
	 * @param id
	 * @param info
	 * @return centro modificado
	 * @throws DatosIncompletosException
	 * @throws ControlHorasVacunacionException
	 */
	public CentroSanitario modificarCentro(@PathVariable String id, 
			@RequestBody Map<String, Object> info)
			throws DatosIncompletosException, CentroNoExisteException {

		JSONObject jso = new JSONObject(info);
		String nombre = jso.optString("nombre");
		String dosisT = jso.optString("dosisTotales");
		String localidad = jso.optString("localidad");
		String provincia = jso.optString("provincia");
		
		CentroSanitario centroSanitario = repository.findByNombre(nombre);

		if(!formValido(nombre, dosisT, localidad, provincia)) {
			throw new DatosIncompletosException();
		}
		
		int dosisTotales = Integer.parseInt(dosisT);
		
		if (centroSanitario==null) {
			throw new CentroNoExisteException();
		}
	
		centroSanitario.setNombre(nombre);
		centroSanitario.setLocalidad(localidad);
		centroSanitario.setProvincia(provincia);
		centroSanitario.setDosisTotales(dosisTotales);

		return repository.save(centroSanitario);
	}

	@Transactional
	@DeleteMapping("/eliminarCentro/{id}") 
	/***
	 * Intentamos eliminar un centro
	 * @param id
	 * @throws CentroNoExisteException
	 * @throws CentroNoEliminadoException
	 */
	public void eliminarCentro(@PathVariable String id) throws CentroNoExisteException, CentroNoEliminadoException {
		Optional<CentroSanitario> cs = repository.findById(id);
		if (cs.isPresent()) {
			List<Usuario> listaUsuarios=repositoryUsuario.findAllByCentroAsignado(cs.get().getNombre());
			if(!listaUsuarios.isEmpty()) {
					throw new CentroNoEliminadoException();
				}
			repository.deleteById(id);
		}else {
			throw new CentroNoExisteException();
		}
		
	}
}