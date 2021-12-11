define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function(ko, app, moduleUtils, accUtils, $) {


		class ModCitaViewModel {
			constructor() {
				var self = this;

				self.nombre = ko.observable("");
				self.tipoUsuario = ko.observable("");
				self.dniPaciente = ko.observable(localStorage.getItem("dniUsuarioAAdministrar"));
				self.fechaAConsultar = ko.observable("");
				self.cupos = ko.observableArray([]);


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
				console.log(localStorage.getItem("emailUsuarioAAdministrar"));
				let self = this;
				let data = {
					url: "cupo/getAllCuposDisponiblesPorFecha/" + this.fechaAConsultar() + '/' + localStorage.getItem("emailUsuarioAAdministrar"),
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						self.cupos(response)
						console.log(self.cupos())
					},
					error: function(response) {
						alert("cupos no obtenidos");
						self.error(response.responseJSON.errorMessage);

					}
				};
				$.ajax(data);
			}

			seleccionarCupo(idCupo) {
				var self = this;

				let info = {
					idCita : localStorage.getItem("idCitaAModificar"),
					idCupo : idCupo,
					dniPaciente : localStorage.getItem("dniUsuarioAAdministrar")
				};
				let data = {
					data: JSON.stringify(info),
					url: "cita/modificarCita",
					type: "post",
					contentType: 'application/json',
					success: function() {
						app.router.go({ path: "administrarCitas" });
						$.confirm({
							title: 'Confirmado',
							content: 'Cita modificada',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function() {
									//Boton para cerrar el popup de confirmación
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
