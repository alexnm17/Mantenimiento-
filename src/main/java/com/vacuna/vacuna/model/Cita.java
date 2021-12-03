package com.vacuna.vacuna.model;

import java.util.UUID;
import org.springframework.data.annotation.Id;
/***
 * 
 * @author crist
 *
 */
public class Cita {

	@Id 
	private String id ;
	private String fechaPrimeraDosis;
	private String fechaSegundaDosis;
	private String nombreCentro;
	private String dniPaciente;
	private String nombrePaciente;
	/***
	 * Constructor de cita
	 * @param fechaPrimeraDosis
	 * @param fechaSegundaDosis
	 * @param nombreCentro
	 * @param dniPaciente
	 * @param nombrePaciente
	 */
	public Cita(String fechaPrimeraDosis, String fechaSegundaDosis, String nombreCentro, String dniPaciente,String nombrePaciente) {
		super();
		this.fechaPrimeraDosis = fechaPrimeraDosis;
		this.fechaSegundaDosis = fechaSegundaDosis;
		this.nombreCentro = nombreCentro;
		this.dniPaciente = dniPaciente;
		this.nombrePaciente = nombrePaciente;
	}
	
	/***
	 * getNombrePaciente
	 * @return nombrePaciente
	 */

	public String getNombrePaciente() {
		return nombrePaciente;
	}
	
	/***
	 * setNombrePaciente
	 * @param nombrePaciente
	 */
	public void setNombrePaciente(String nombrePaciente) {
		this.nombrePaciente = nombrePaciente;
	}
	
	/***
	 * getDniPaciente
	 * @return dniPaciente
	 */
	public String getDniPaciente() {
		return dniPaciente;
	}
	
	/***
	 * setDniPaciente
	 * @param dniPaciente
	 */
	public void setDniPaciente(String dniPaciente) {
		this.dniPaciente = dniPaciente;
	}
	
	/***
	 * getNombreCentro
	 * @return nombreCentro
	 */
	public String getNombreCentro() {
		return nombreCentro;
	}
	
	/***
	 * setNombreCentro
	 * @param nombreCentro
	 */
	public void setNombreCentro(String nombreCentro) {
		this.nombreCentro = nombreCentro;
	}

	public Cita() {
		this.id = UUID.randomUUID().toString();
	}
	
	/***
	 * getId
	 * @return id
	 */
	public String getId() {
		return id;
	}
	
	/***
	 * getFechaPrimeraDosis
	 * @return fechaPrimeraDosis
	 */
	public String getFechaPrimeraDosis() {
		return fechaPrimeraDosis;
	}
	
	/***
	 * setFechaPrimeraDosis
	 * @param fechaPrimeraDosis
	 */
	public void setFechaPrimeraDosis(String fechaPrimeraDosis) {
		this.fechaPrimeraDosis = fechaPrimeraDosis;
	}
	
	/***
	 * getFechaSegundaDosis
	 * @return fechaSegundaDosis
	 */
	public String getFechaSegundaDosis() {
		return fechaSegundaDosis;
	}
	
	/***
	 * setFechaSegundaDosis
	 * @param fechaSegundaDosis
	 */
	public void setFechaSegundaDosis(String fechaSegundaDosis) {
		this.fechaSegundaDosis = fechaSegundaDosis;
	}

}
