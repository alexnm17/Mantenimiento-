package com.vacuna.vacuna.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.vacuna.vacuna.model.FormatoVacunacion;
/***
 * 
 * @author Rafa
 *
 */
public interface FormatoVacunacionDAO extends MongoRepository<FormatoVacunacion, String> {


	
}
