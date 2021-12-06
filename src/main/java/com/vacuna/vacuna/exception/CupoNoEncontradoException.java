package com.vacuna.vacuna.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No se ha encontrado ningun cupo disponible")
/***
 * 
 * @author crist
 *
 */
public class CupoNoEncontradoException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4850508859091221335L;

	public CupoNoEncontradoException() {
		//Metodo vacio porque lo hace en el @ResponseStatus
	}
}
