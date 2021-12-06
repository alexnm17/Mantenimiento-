define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function(ko, app, moduleUtils, accUtils, $) {


		class ModCitaViewModel {
			constructor() {
				var self = this;

				self.nombre = ko.observable("");
				self.tipoUsuario = ko.observable("");
				self.dniPaciente = ko.observable(localStorage.getItem("dniUsuarioAAdministrar"));
				self.fechaAConsultar = ko.observable("");

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

			modificarCita() {
				var self = this;

				let info = {
					id: app.cita.id,
					dniPaciente: app.cita.dniPaciente,
					fechaPrimeraDosis: this.fechaPrimeraDosis(),
					fechaSegundaDosis: this.fechaSegundaDosis(),
					centrosSanitarios: app.cita.centroAsignado,
				};
				let data = {
					data: JSON.stringify(info),
					url: "cita/modificarCita/" + app.cita.id,
					type: "put",
					contentType: 'application/json',
					success: function(response) {
						app.router.go({ path: "homePaciente" });
						$.confirm({
							title: 'Confirmado',
							content: 'Cita modificada',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function() {
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

						self.nombre(response[0]);
						self.tipoUsuario(response[1]);
					},
					error: function(response) {
						self.error(response.responseJSON.errorMessage);

					}
				};
				$.ajax(data);
			}

			getCuposDeFecha() {
				let self = this;
				let data = {
					url: "cupo/getAllCuposDisponiblesPorFecha/"+this.fechaAConsultar()+'/'+localStorage.getItem("emailUsuarioAAdministrar"),
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						alert("cupos obtenidos")
					},
					error: function(response) {
						alert("cupos no obtenidos");
						self.error(response.responseJSON.errorMessage);

					}
				};
				$.ajax(data);
			}

			connected() {
				accUtils.announce('Inicio page loaded.');
				document.title = "Modificar cita";
				this.getUserConnect();
			};

			disconnected() {
				// Implement if needed
			};

			transitionCompleted() {
				// Implement if needed
			};
		}

		return ModCitaViewModel;
	});
