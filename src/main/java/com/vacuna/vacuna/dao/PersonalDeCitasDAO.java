package com.vacuna.vacuna.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vacuna.vacuna.model.PersonalDeCitas;


@Repository
/***
 * 
 * @author Jaime
 *
 */
public interface PersonalDeCitasDAO extends MongoRepository<PersonalDeCitas, String> {
	
}
