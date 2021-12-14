define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function(ko, app, moduleUtils, accUtils, $) {


		class GestionUserViewModel {
			constructor() {
				var self = this;

				self.centros = ko.observableArray([]);
				self.tipoUsuario = ko.observable("");
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

			definirFormato() {
				app.router.go({ path: "definirFormato" });
			}

			getCentros() {
				let self = this;
				let data = {
					url: "centro/getTodos",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						self.centros(response);
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
						self.error(response.responseJSON.errorMessage);

					}
				};
				$.ajax(data);
			}

			connected() {
				accUtils.announce('Inicio page loaded.');
				document.title = "Gestion Usuarios";
				this.getCentros();
				this.getUserConnect();
			};

			disconnected() {
				// Implement if needed
			};

			transitionCompleted() {
				// Implement if needed
			};
		}

		return GestionUserViewModel;
	});
