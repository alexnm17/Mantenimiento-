package com.vacuna.vacuna.model;

import java.util.UUID;
import org.springframework.data.annotation.Id;
/***
 * 
 * @author crist
 *
 */
public class CentroSanitario {

		@Id 
		private String id;
		private String nombre;
		private int dosisTotales;
		private String localidad;
		private String provincia;
		
		/***
		 *  Constructor del centro sanitario
		 * @param nombre
		 * @param dosisTotales
		 * @param aforo
		 * @param horaInicio
		 * @param horaFin
		 * @param localidad
		 * @param provincia
		 */
		public CentroSanitario(String nombre, int dosisTotales, String localidad, String provincia) {
			super();
			this.nombre = nombre;
			this.dosisTotales = dosisTotales; //Valor constante de momento
			this.localidad = localidad;
			this.provincia = provincia;
		}


		/***
		 * getHoraInicio
		 * @return horaInicio
		 */
				

		public void restarDosis() {
			dosisTotales -= 2;
		}
		
		/***
		 * getNombre
		 * @return nombre
		 */
		public String getNombre() {
			return nombre;
		}
		
		/***
		 * setNombre
		 * @param nombre
		 */
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		
		/***
		 * getDosisTotales
		 * @return dosisTotales
		 */
		public int getDosisTotales() {
			return dosisTotales;
		}
		
		/***
		 * setDosisTotales
		 * @param dosisTotales
		 */
		public void setDosisTotales(int dosisTotales) {
			this.dosisTotales = dosisTotales;
		}
		
		/***
		 * getAforo
		 * @return aforo
		 */
		
		
		/***
		 * setAforo
		 * @param aforo
		 */
		
		
		/***
		 * getLocalidad
		 * @return localidad
		 */
		public String getLocalidad() {
			return localidad;
		}
		
		/***
		 * setLocalidad
		 * @param localidad
		 */
		public void setLocalidad(String localidad) {
			this.localidad = localidad;
		}
		
		/***
		 * getProvincia
		 * @return provincia
		 */
		public String getProvincia() {
			return provincia;
		}
		
		/***
		 * setProvincia
		 * @param provincia
		 */
		public void setProvincia(String provincia) {
			this.provincia = provincia;
		}

		
		public CentroSanitario() {
			this.id = UUID.randomUUID().toString();
		}
		
		/***
		 * getId
		 * @return id
		 */
		public String getId() {
			return id;
		}
}
