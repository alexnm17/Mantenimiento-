define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function(ko, app, moduleUtils, accUtils, $) {

		class InicioViewModel {
			constructor() {
				var self = this;

				self.mostrarsolicitarCita = ko.observable(1);
				self.mostrarverCitas = ko.observable(1);
				self.mostrarMensaje = ko.observable(2);
				self.mostrarGestionUsuario = ko.observable(1);
				self.mostrarModificacionUsuario = ko.observable(1);
				self.citas = ko.observableArray([]);
				self.usuarios = ko.observableArray([]);
				self.nombre = ko.observable("");
				self.apellidos = ko.observable("");
				self.email = ko.observable("");
				self.dniPaciente = ko.observable("");
				self.tipoUsuario = ko.observable("");
				self.centroAsignado = ko.observable("");
				self.dosisAdministradas = ko.observable("");
				self.localidad = ko.observable("");
				self.provincia = ko.observable("");
				self.fecha = ko.observable("");
				self.hora = ko.observable("");

				self.horaInicio = ko.observable("");
				self.horaFin = ko.observable("");

				self.mensaje = ko.observable(2);
				self.mostrarSolicitarCita = ko.observable(1);
				self.getCentros();


				var hoy = new Date();
				self.fecha = ko.observable(hoy.toLocaleString().split(' ')[0]);
				self.fechaHoy = ko.observable(hoy.toLocaleString());

				// Header Config
				self.headerConfig = ko.observable({
					'view': [],
					'viewModel': null
				});
				moduleUtils.createView({
					'viewPath': 'views/header.html'
				}).then(function(view) {
					self.headerConfig({
						'view': view,
						'viewModel': app.getHeaderModel()
					})
				})
			}

			verCitasOtrosDias() {
				app.router.go({ path: "verCitasOtrosDias" })

			}



			getCentros() {
				let self = this;
				let data = {
					url: "centro/getTodos",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						for (let i = 0; i < response.length; i++) {
							let centro = {
								id: response[i].id,
								nombre: response[i].nombre,
								dosisTotales: response[i].dosisTotales,
								aforo: response[i].aforo,
								horaInicio: response[i].horaInicio,
								horaFin: response[i].horaFin,
								localidad: response[i].localidad,
								provincia: response[i].provincia,
								eliminar: function() {
									self.eliminarUsuario(response[i].dni);
								},

								modificarCentros: function() {
									app.centro = this;
									app.router.go({ path: "modificarCentro" });
								},

							};
							self.horaInicio((response[i].horaInicio < 10 ? "0" : "") + response[i].horaInicio + ":00:00");
							self.horaFin((response[i].horaFin < 10 ? "0" : "") + response[i].horaFin + ":00:00");
						}
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}
			getUsuarios() {
				let self = this;
				let data = {
					url: "Usuario/getTodos",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						self.usuarios([]);
						for (let i = 0; i < response.length; i++) {
							let paciente = {
								nombre: response[i].nombre,
								apellidos: response[i].apellidos,
								dni: response[i].dni,
								tipoUsuario: response[i].tipoUsuario,
								centroAsignado: response[i].centroAsignado,
								dosisAdministradas: response[i].dosisAdministradas,
								localidad: response[i].localidad,
								provincia: response[i].provincia,
								eliminar: function() {
									self.eliminarUsuario(response[i].dni);
								},
								modificarUsuarios: function() {
									app.paciente = this;
									self.modificarUsuarios();
									self.paciente = ko.observable();
									self.paciente = app.paciente;
									self.nombre2 = ko.observable(self.paciente.nombre);
								},
							};
							self.usuarios.push(paciente);
						}
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			verCitasSanitario() {
				app.router.go({ path: "verCitasSanitario" });
			}

			paginaInicio() {
				app.router.go({ path: "homeSanitario" });
			}

			logout() {
				localStorage.clear();
				
			}

			getCitas(email) {
				let self = this;
				let data = {
					url: "cita/getCitasHoy/" + email,
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						console.log(response)
						self.citas(response);
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });
					}
				};
				$.ajax(data);
			}

			getUserConnect() {
				let self = this;
				let data = {
					url: "login/getUser",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						self.nombre(response[0]);
						self.tipoUsuario(response[1]);
						self.email(response[2]);
						self.getCitas(response[2]);
						self.dosisMarcadas(response[2]);
						self.centroAsignado(response[3]);
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			primerInicio() {
				let self = this;
				let data = {
					url: "login/primerInicio",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						if (response == "1") {
							app.router.go({ path: "changePassword" });
						}
					},
					error: function(response) {
					}
				};
				$.ajax(data);
			}

			comprobarRol() {
				let self = this;
				let data = {
					url: "login/comprobarRolSanitario",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						if (response == "denegado") {
							app.router.go({ path: "login" });
						}
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			vacunar(dniPaciente) {
				var self = this;
				let info = {
					dni: dniPaciente,
				};
				let data = {
					data: JSON.stringify(info),
					url: "Usuario/marcarVacunado",
					type: "post",
					contentType: 'application/json',
					success: function(response) {
						$.confirm({
							title: 'Confirmado',
							content: 'Usuario vacunado',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function() {
									location.reload();
								}
							}
						});
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			connected() {
				accUtils.announce('Inicio page loaded.');
				document.title = "Inicio";
				this.getUserConnect();
				this.primerInicio();
				this.comprobarRol();
			};

			disconnected() {
				// Implement if needed
			};

			transitionCompleted() {
				// Implement if needed
			};
		}

		return InicioViewModel;
	});
