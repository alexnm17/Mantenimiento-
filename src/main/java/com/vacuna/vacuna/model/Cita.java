package com.vacuna.vacuna.model;

import java.util.UUID;
import org.springframework.data.annotation.Id;


/***
 * 
 * @author crist
 * @editor Sergio
 *
 */
public class Cita {

	@Id
	private String id;
	private String fecha;
	private String hora;
	private String nombreCentro;
	private String usuario;
	private boolean isUsada;

	public Cita(String fecha, String hora, String nombreCentro, String usuario) {
		this.id = UUID.randomUUID().toString();
		this.fecha = fecha;
		this.hora = hora;
		this.nombreCentro = nombreCentro;
		this.usuario = usuario;
	}

	public Cita() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getNombreCentro() {
		return nombreCentro;
	}

	public void setNombreCentro(String nombreCentro) {
		this.nombreCentro = nombreCentro;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public boolean isUsada() {
		return isUsada;
	}

	public void setUsada(boolean isUsada) {
		this.isUsada = isUsada;
	}

}
