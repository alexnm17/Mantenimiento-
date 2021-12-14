define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function(ko, app, moduleUtils, accUtils, $) {


		class HomePacienteViewModel {
			constructor() {
				var self = this;
				self.user = app.user;
				self.botonCita = ko.observable(1);
				self.botonFecha = ko.observable(2);
				self.tablaCita = ko.observable(1);
				self.usuarios = ko.observableArray([]);
				self.nombre = ko.observable("");
				self.apellidos = ko.observable("");
				self.dniUsuario = ko.observable("");
				self.emailUsuario = ko.observable("");
				self.tipoUsuario = ko.observable("");
				self.centroAsignado = ko.observable("");
				self.dosisAdministradas = ko.observable("");

				self.cita = ko.observable("");
				self.fechaPrimeraDosis = ko.observable("");
				self.fechaSegundaDosis = ko.observable("");
				self.citas = ko.observableArray([]);

				self.mensaje = ko.observable(2);
				self.mostrarSolicitarCita = ko.observable(1);
				self.getCentros();


				var hoy = new Date();
				self.fecha = ko.observable(hoy.toLocaleString().split(' ')[0]);


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

			getCentros() {
				let self = this;
				let data = {
					url: "centro/getTodos",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						for (let c of response) {
							let centro = {
								id: c.id,
								nombre: c.nombre,
								dosisTotales: c.dosisTotales,
								localidad: c.localidad,
								provincia: c.provincia,
								eliminar: function() {
									self.eliminarUsuario(centro.dni);
								},

								modificarCentros: function() {
									app.centro = this;
									app.router.go({ path: "modificarCentro" });
								},
							};
						}
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			solicitarCita() {
				let self = this;
				let info = {
					email: self.emailUsuario(),
				};

				let data = {
					data: JSON.stringify(info),
					url: "cita/solicitarCita",
					type: "post",
					contentType: 'application/json',
					success: function(response) {
						$.confirm({
							title: 'Confirmado',
							content: 'Cita Creada',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function() {
									location.reload();
								}
							}
						});
						self.tablaCita(1);
						self.getCitaPaciente();
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			logout() {
				localStorage.clear();
				app.router.go({ path: "login" });
			}


			getCitaPaciente() {
				let self = this;
				let data = {
					url: "cita/getCitaPaciente/" + self.dniUsuario(),
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						self.citas(response);
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}


			eliminarCita(id) {
				let self = this;
				let data = {
					url: "cita/anularCita/" + id,
					type: "delete",
					contentType: 'application/json',
					success: function(response) {

						$.confirm({
							title: 'Confirmado',
							content: 'Cita eliminada',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function() {
								}
							}
						});
						self.getCitaPaciente();
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.confirm({
					title: 'Eliminar',
					content: '¿Seguro que desea eliminar?',
					buttons: {
						Confirmar: function() {
							$.ajax(data);
						},
						Cancelar: function() {
						},
					}
				});
			}
			comprobarRol() {
				let self = this;
				let data = {
					url: "login/comprobarRolPaciente",
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

			getUserConnect(onComplete) {



				let self = this;
				let data = {
					url: "login/getUser",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						self.nombre(response[0]);
						self.tipoUsuario(response[1]);
						self.emailUsuario(response[2]);
						self.centroAsignado(response[3]);
						self.dniUsuario(response[4]);
						self.dosisAdministradas(response[5]);
						onComplete();
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
						console.log(response);
						if (response == "1") {
							app.router.go({ path: "changePassword" });
						}
					},
					error: function(response) {
					}
				};
				$.ajax(data);
			}


			modificarCita(idCita, dniPaciente) {
				localStorage.setItem("idCitaAModificar", idCita)
				localStorage.setItem("dniUsuarioAAdministrar", dniPaciente)
				localStorage.setItem("emailUsuarioAAdministrar", localStorage.getItem("emailUsuario"))

				app.router.go({ path: "modificarCita" });
			}


			connected() {
				accUtils.announce('Inicio page loaded.');
				document.title = "Inicio";
				this.getUserConnect(() => {
					this.comprobarRol();
					this.primerInicio();
					this.getCitaPaciente();
				});


			};

			disconnected() {
				// Implement if needed
			};

			transitionCompleted() {
				// Implement if needed
			};
		}

		return HomePacienteViewModel;
	});
