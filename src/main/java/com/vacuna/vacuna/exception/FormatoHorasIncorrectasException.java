package com.vacuna.vacuna.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value=HttpStatus.CONFLICT, reason="Las horas especificadas en el formato no son válidas. Por favor revise que se han introducido correctamente")
/***
 * 
 * @author Rafa
 *
 */

public class FormatoHorasIncorrectasException extends Exception{

	private static final long serialVersionUID = -3944299722578489097L;
	public FormatoHorasIncorrectasException() {
		//Metodo vacio porque lo hace en el @ResponseStatus
	}

}