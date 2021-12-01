package com.vacuna.vacuna.model;
/***
 * 
 * @author Jaime
 *
 */

public class PersonalDeCitas extends Usuario {
	/***
	 * Constructor del administrador
	 * @param nombre
	 * @param email
	 * @param password
	 * @param dni
	 * @param tipoUsuario
	 * @param centroAsignado
	 */
	public PersonalDeCitas(String nombre, String email, byte[] password, String dni, String tipoUsuario, String centroAsignado) {
		super(nombre, email, password, dni, tipoUsuario, centroAsignado);
	}
	
}