package com.vacuna.vacuna.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cupo;

public interface CupoDAO extends MongoRepository<Cupo, String> {

	Cupo findByFechaAndHora(String fecha, String hora);

	void deleteByFechaAndHoraAndCentroVacunacion(String fecha, String hora, CentroSanitario vacunacion);

	Cupo findByFechaAndHoraAndCentroVacunacion(String fecha, String hora, CentroSanitario centro);

	List<Cupo> findAllByCentroVacunacionAndFecha(CentroSanitario centroVacunacion, String fecha);

	List<Cupo> findAllByCentroVacunacion(CentroSanitario centroVacunacion);

	Cupo findAllByCentroVacunacionAndFechaAndHora(CentroSanitario centroVacunacion, String fecha, String hora);

}
