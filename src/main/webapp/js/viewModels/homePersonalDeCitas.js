define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function(ko, app, moduleUtils, accUtils, $) {


		class GestionUserViewModel {
			constructor() {
				var self = this;

				self.usuarios = ko.observableArray([]);
				self.nombre = ko.observable("");
				self.email = ko.observable("");
				self.dni = ko.observable("");
				self.tipoUsuario = ko.observable("");
				self.password = ko.observable("");
				self.centroAsignado = ko.observable("");
				self.localidad = ko.observable("");
				self.provincia = ko.observable("");


				self.nombreUsuario = ko.observable("");

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


			enable() {
				document.getElementById("menuCentros").disabled = false;
			}

			disable() {
				document.getElementById("menuCentros").disabled = true;
			}

			irHomePersonalDeCitas() {
				app.router.go({ path: "homePersonalDeCitas" });
			}

			administrarCitas(dni, email) {
				localStorage.setItem('dniUsuarioAAdministrar', dni)
				localStorage.setItem('emailUsuarioAAdministrar', email)
				app.router.go({ path: "administrarCitas" });
			}

			paginaInicio() {
				app.router.go({ path: "homeAdmin" });
			}


			getUsuarios() {
				let self = this;
				let data = {
					url: "Usuario/getTodos",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						self.usuarios([]);
						for (let usuario of response) {
							let paciente = {
								nombre: usuario.nombre,
								email: usuario.email,
								dni: usuario.dni,
								tipoUsuario: usuario.tipoUsuario,
								centroAsignado: usuario.centroAsignado,
								eliminar: function() {
									self.eliminarUsuario(usuario.email);
								},
								modificarUsuarios: function() {
									app.paciente = this;
									app.router.go({ path: "modificarUsuario" });
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
						self.error(response.responseJSON.errorMessage);

					}
				};
				$.ajax(data);
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

			connected() {
				accUtils.announce('Inicio page loaded.');
				document.title = "Gestion Usuarios";
				this.comprobarRol();
				this.getUsuarios();
				this.getUserConnect();
			}

			disconnected() {
				// Implement if needed
			}

			transitionCompleted() {
				// Implement if needed
			}
		}

		return GestionUserViewModel;
	});
