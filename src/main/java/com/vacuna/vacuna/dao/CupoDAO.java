package com.vacuna.vacuna.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.vacuna.vacuna.model.CentroSanitario;
import com.vacuna.vacuna.model.Cupo;

public interface CupoDAO extends MongoRepository<Cupo, String> {

	Cupo findByFechaAndHora(String fecha, String hora);

	void deleteByFechaAndHoraAndCentroSanitario(String fecha, String hora, CentroSanitario vacunacion);

	Cupo findByFechaAndHoraAndCentroSanitario(String fecha, String hora, CentroSanitario centro);

	List<Cupo> findAllByCentroSanitarioAndFecha(CentroSanitario centroSanitario, String fecha);

	List<Cupo> findAllByCentroSanitario(CentroSanitario centroVacunacion);

	Cupo findAllByCentroSanitarioAndFechaAndHora(CentroSanitario centroVacunacion, String fecha, String hora);

}
