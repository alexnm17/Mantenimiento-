
define(['knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery'], function(ko, app, moduleUtils, accUtils, $) {

		class InicioViewModel {
			constructor() {
				var self = this;

				self.nombre = ko.observable("");
				self.tipoUsuario = ko.observable("");

				self.horaInicio = ko.observable("");
				self.horaFin = ko.observable("");
				self.duracionFranja = ko.observable(0);
				self.personasAVacunar = ko.observable(0);

				self.hayDatos = ko.observable(true);
				self.noHayDatos = ko.observable(false);

				var dropdown = document.getElementsByClassName("dropdown-btn");
				var i;
				for (i = 0; i < dropdown.length; i++) {
					dropdown[i].addEventListener("click", function() {
						this.classList.toggle("active");
						var dropdownContent = this.nextElementSibling;
						if (dropdownContent.style.display === "block") {
							dropdownContent.style.display = "none";
						} else {
							dropdownContent.style.display = "block";
						}
					});
				}

				self.message = ko.observable(null);
				self.error = ko.observable(null);

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

			w3_open() {
				document.getElementById("mySidebar").style.display = "block";
				document.getElementById("myOverlay").style.display = "block";
			}

			w3_close() {
				document.getElementById("mySidebar").style.display = "none";
				document.getElementById("myOverlay").style.display = "none";
			}

			onClick(element) {
				document.getElementById("img01").src = element.src;
				document.getElementById("modal01").style.display = "block";
				var captionText = document.getElementById("caption");
				captionText.innerHTML = element.alt;
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
						$.confirm({ title: 'Error', content: response.responseJSON.message, type: 'red', typeAnimated: true, buttons: { tryAgain: { text: 'Cerrar', btnClass: 'btn-red', action: function() { } } } });

					}
				};
				$.ajax(data);
			}

			getFormatoVacunacion() {
				let self = this;
				let data = {
					url: "formato/getFormatoVacunacion",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						if (response != null) {
							self.hayDatos(true);
							self.noHayDatos(false);
							
							self.horaInicio(response.horaInicioVacunacion);
							self.horaFin(response.horaFinVacunacion);
							self.duracionFranja(response.duracionFranjaVacunacion);
							self.personasAVacunar(response.personasPorFranja);
						} else {
							self.hayDatos(false);
							self.noHayDatos(true);
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

			definirFormato() {
				app.router.go({ path: "definirFormato" });
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

			comprobarRol() {
				let self = this;
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

			definirFormatoHandler() {
				var self = this;
				let info = {
					horaInicio: this.horaInicio(),
					horaFin: this.horaFin(),
					duracionFranja: this.duracionFranja(),
					personasAVacunar: this.personasAVacunar()
				};
				let data = {
					data: JSON.stringify(info),
					url: "formato/definirFormatoVacunacion",
					type: "post",
					contentType: 'application/json',
					success: function(response) {
						self.crearCupos();
						$.confirm({
							title: 'Confirmado',
							content: 'Formato definido',
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
			
			crearCupos(){
				let data = {
					url: "formato/crearPlantillasCitaVacunacion",
					type: "post",
					success: function(response) {
						$.confirm({
							title: 'Confirmado',
							content: 'Cupos creados',
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
				document.title = "Definición de Formato";
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


			w3_open() {
				document.getElementById("mySidebar").style.display = "block";
				document.getElementById("myOverlay").style.display = "block";
			}

			w3_close() {
				document.getElementById("mySidebar").style.display = "none";
				document.getElementById("myOverlay").style.display = "none";
			}

			// Modal Image Gallery
			onClick(element) {
				document.getElementById("img01").src = element.src;
				document.getElementById("modal01").style.display = "block";
				var captionText = document.getElementById("caption");
				captionText.innerHTML = element.alt;


				var dropdown = document.getElementsByClassName("dropdown-btn");
				var i;
				for (i = 0; i < dropdown.length; i++) {
					dropdown[i].addEventListener("click", function() {
						this.classList.toggle("active");
						var dropdownContent = this.nextElementSibling;
						if (dropdownContent.style.display === "block") {
							dropdownContent.style.display = "none";
						} else {
							dropdownContent.style.display = "block";
						}
					});
				}
			}
		}

		return InicioViewModel;
	});