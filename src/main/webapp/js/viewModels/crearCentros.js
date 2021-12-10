define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function(ko, app, moduleUtils, accUtils, $) {


		class CreateCenterViewModel {
			constructor() {
				var self = this;

				self.centros = ko.observableArray([]);
				self.id = ko.observable("");
				self.nombre = ko.observable("");
				self.dosisTotales = ko.observable("");
				self.localidad = ko.observable("");
				self.provincia = ko.observable("");

				self.nombreUsuario = ko.observable("");
				self.tipoUsuario = ko.observable("");

				self.aforo = ko.observable("");
				self.horaInicioVacunacion = ko.observable("");
				self.horaFinVacunacion = ko.observable("");

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

			comprobarRol() {
				let self = this;
				console.log("hola");
				let data = {
					url: "login/comprobarRolAdmin",
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

			getFormatoVacunacion() {
				let self = this;
				let data = {
					url: "formato/getFormatoVacunacion",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						self.horaInicioVacunacion(response.horaInicioVacunacion);
						self.horaFinVacunacion(response.horaFinVacunacion);
						self.aforo(response.personasPorFranja);
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

			eliminarCentro(id) {
				let self = this;
				let data = {
					url: "centro/eliminarCentro/" + id,
					type: "delete",
					contentType: 'application/json',
					success: function(response) {
						console.log(response);

						$.confirm({
							title: 'Confirmado',
							content: 'Centro eliminado',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function() {
								}
							}
						});
						self.getCentros();
						console.log(self.error);
						self.getCentros();
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


			add() {
				var self = this;
				let info = {
					nombre: this.nombre(),
					dosisTotales: this.dosisTotales(),
					localidad: this.localidad(),
					provincia: this.provincia(),
				};
				let data = {
					data: JSON.stringify(info),
					url: "centro/add",
					type: "put",
					contentType: 'application/json',
					success: function(response) {
						$.confirm({
							title: 'Confirmado',
							content: 'Centro Guardado',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function() {
								}
							}
						});
						self.getCentros();
					},
					error: function(response) {
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			modificarCentro(id) {
				app.idc = id;
				app.centro = this;
				app.router.go({ path: "modificarCentro" });
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

			connected() {
				accUtils.announce('Inicio page loaded.');
				document.title = "Crear centro";
				this.comprobarRol();
				this.getFormatoVacunacion();
				this.getUserConnect();
			};

			disconnected() {
				// Implement if needed
			};

			transitionCompleted() {
				// Implement if needed
			};
		}

		return CreateCenterViewModel;
	});
