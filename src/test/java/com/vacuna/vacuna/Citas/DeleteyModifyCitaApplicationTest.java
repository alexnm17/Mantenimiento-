package com.vacuna.vacuna.Citas;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.vacuna.vacuna.VacunaApplication;
import com.vacuna.vacuna.dao.CentroSanitarioDAO;
import com.vacuna.vacuna.dao.CitaDAO;
import com.vacuna.vacuna.dao.UsuarioDAO;
import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cita;
import com.vacuna.vacuna.model.Paciente;

/***
 * 
 * @author crist
 *
 */
@ExtendWith(SpringExtension.class)

@SpringBootTest(classes = VacunaApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class DeleteyModifyCitaApplicationTest {
	@Autowired 
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;

	@Autowired
	private CitaDAO citaDAO;
	private Cita c;
	@Autowired
	private UsuarioDAO DAO;
	private Paciente p;
	@Autowired
	private CentroSanitarioDAO centroDAO;
	private CentroSanitario centro;
	
	public void setupMockMvc(){
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).build();
	}
	@BeforeEach
	void setupTest() {
		centro = new CentroSanitario("Centro Cita", 2000, "Ciudad Real", "Ciudad Real");
		centroDAO.save(centro);
		p = new Paciente("Cristina eliminarCita", "eliminarcita@gmail.com","Hola1234=".getBytes(), "05724787H", "Paciente", "Centro Cita", "0", "CR", "CR");
		DAO.save(p);
		c= new Cita("2021-12-01","09:00", "Centro Prueba Cita", "05724787H");
		citaDAO.save(c);
	}
	
	@Test
	void testAnularCitaCorrecto() {
		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("id", "d07ff562-00a7-482f-8d88-70c87746871b");
		
		JSONObject json = new JSONObject(mapa);
		String body = json.toString();
		
		try {
			mockMvc.perform(MockMvcRequestBuilders.delete("/cita/anularCita/d07ff562-00a7-482f-8d88-70c87746871b")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
					.andExpect(MockMvcResultMatchers.status().isOk());
			//si no hay excepciones va bien
			assertTrue(true);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	/***
	 * Test obtener citas completo
	 * @throws Exception
	 */
	@Test
	void getCitasCompleto() throws Exception { 
		final ResultActions resultado = mockMvc.perform(MockMvcRequestBuilders.get("/cita/getCitaPaciente/"+p.getDni())
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().is(200));
	}
	
	@Test
	void testGetCitasOtroDia() {
		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("emailUsuario", "rafa@gmail.com");
		mapa.put("fecha","2021-12-01");
		
		JSONObject json = new JSONObject(mapa);
		String body = json.toString();
		
		List<Cita> listaCitasUsuario = new ArrayList<Cita>();
		listaCitasUsuario.add(c);
		
		try {
			when(citaDAO.findAllByNombreCentroAndFecha(any(), any())).thenReturn(listaCitasUsuario);
			lenient().when(DAO.findByEmail(any())).thenReturn(p);
			mockMvc.perform(MockMvcRequestBuilders.post("/getCitasOtroDia")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
					.andExpect(MockMvcResultMatchers.status().isOk());
			//si no hay excepciones va bien
			assertTrue(true);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@AfterEach
	public void deleteAll() {
		Paciente p = (Paciente) DAO.findByEmail("eliminarcita@gmail.com");
		if(p!=null) {
			DAO.delete(p);
		}
		centroDAO.delete(centro);
		Cita c = citaDAO.findByDniPaciente("05724787H");
		if(c!=null) 
			citaDAO.delete(c);
	}
}
