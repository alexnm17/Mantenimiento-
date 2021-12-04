package com.vacuna.vacuna.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class Cupo {
	@Id
	private String idCupo;
	private String fecha;
	private String hora;
	private int personasRestantes;
	@DBRef
	private CentroSanitario centroSanitario;

	public Cupo() {
		this.idCupo = UUID.randomUUID().toString();
	}

	public Cupo(String fecha, String hora, CentroSanitario centro, int personasRestantes) {
		this.fecha = fecha;
		this.hora = hora;
		this.centroSanitario = centro;
		this.personasRestantes = personasRestantes;
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

	public CentroSanitario getCentroVacunacion() {
		return centroSanitario;
	}

	public void setCentroVacunacion(CentroSanitario centroSanitario) {
		this.centroSanitario = centroSanitario;
	}

	public int getPersonasRestantes() {
		return personasRestantes;
	}

	public void setPersonasRestantes(int personasRestantes) {
		this.personasRestantes = personasRestantes;
	}

	public void restarPersona(int numero) {
		this.personasRestantes -= numero;
	}

	public String getIdCupo() {
		return idCupo;
	}

	public void setIdCupo(String idCupo) {
		this.idCupo = idCupo;
	}

}
