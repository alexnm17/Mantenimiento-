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
				self.email = localStorage.getItem('emailUsuarioAAdministrar')


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
						console.log(response);
						self.citas(response);

					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			solicitarCita() {
				alert("La petición de cita se está procesando. Espere para la resolución.")

				let self = this;
				let info = {
					email: self.email,
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
						this.getCitas();
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

			modificarCita(idCita) {
				localStorage.setItem("idCitaAModificar", idCita)
				app.router.go({ path: "modificarCita" });
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
				localStorage.clear();
				app.router.go({ path: "login" });
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

			irHomePersonalDeCitas() {
				app.router.go({ path: "homePersonalDeCitas" });
			}

			connected() {
				accUtils.announce('Inicio page loaded.');
				document.title = "Inicio";
				this.getCitas();
				this.comprobarRol();
			}

			comprobarRol() {
				let self = this;
				let data = {
					url: "login/comprobarRolPersonalDeCitas",
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

			disconnected() {
				// Implement if needed
			}

			transitionCompleted() {
				// Implement if needed
			}
		}

		return InicioViewModel;
	});
