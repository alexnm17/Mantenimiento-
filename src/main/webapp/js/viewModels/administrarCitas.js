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

				self.dni = localStorage.getItem('dniUsuarioAAdministrar')

				self.citas = ko.observableArray([]);
				self.nombreUsuario = ko.observable("");
				self.apellidos = ko.observable("");
				self.dniPaciente = ko.observable("");
				self.tipoUsuario = ko.observable("");
				self.centroAsignado = ko.observable("");
				self.fechaPrimeraDosis = ko.observable("");
				self.fechaSegundaDosis = ko.observable("");
				self.dosisAdministradas = ko.observable("");
				self.localidad = ko.observable("");
				self.provincia = ko.observable("");

				self.mensaje = ko.observable(2);
				self.mostrarSolicitarCita = ko.observable(1);



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



			getCitas() {
				let self = this;
				let data = {
					url: "cita/getCitasPaciente/" + self.dni,
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						console.log(response)

						for (let cita of response) {
							self.citas.push(cita);
						}
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
					url: "cita/eliminarCita/" + id,
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
						self.getCitas();
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

			modificarCita(id) {
				let self = this;

				let data = {
					url: "cita/modificarCita/" + id,
					type: "put",
					contentType: 'application/json',
					success: function(response) {
						self.fechaPrimeraDosis(response.fechaPrimeraDosis);
						self.fechaSegundaDosis(response.fechaSegundaDosis);
						self.centroAsignado(response.nombreCentro);

						$.confirm({
							title: 'Confirmado',
							content: 'Cita creada',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function() {
								}
							}
						});
						self.getCitas();
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
						self.nombreUsuario(response[0]);
						self.tipoUsuario(response[1]);
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			logout() {
				let self = this;
				let data = {
					url: "login/logout",
					type: "post",
					contentType: 'application/json',
					success: function(response) {
						app.router.go({ path: "login" });
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}


			gestionUsuarios() {
				app.router.go({ path: "gestionUsuarios" });
			}

			crearUsuarios() {
				app.router.go({ path: "crearUsuarios" });
			}

			gestionCentros() {
				app.router.go({ path: "gestionCentros" });
			}

			crearCentros() {
				app.router.go({ path: "crearCentros" });
			}

			paginaInicio() {
				app.router.go({ path: "homeAdmin" });
			}

			gestionCitas() {
				app.router.go({ path: "verCitas" });
			}

			crearCita() {
				app.router.go({ path: "solicitarCita" });
			}

			connected() {
				accUtils.announce('Inicio page loaded.');
				document.title = "Inicio";
				this.getCitas();
			}

			disconnected() {
				// Implement if needed
			}

			transitionCompleted() {
				// Implement if needed
			}
		}

		return InicioViewModel;
	});
