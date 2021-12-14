define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class verCitasOtrosDiasViewModel {
		constructor() {
			var self = this;

			self.citas = ko.observableArray([]);
			self.nombreUsuario = ko.observable("");
			self.email = ko.observable("");
			self.dniPaciente = ko.observable("");
			self.dniPaciente2 = ko.observable("");
			self.tipoUsuario = ko.observable("");
			self.centroAsignado = ko.observable("");
			self.fecha = ko.observable("");
			self.hora = ko.observable("");
			self.dosisAdministradas = ko.observable("");

			self.mensaje= ko.observable(2);
			self.mostrarSolicitarCita = ko.observable(1);

			

			// Header Config
			self.headerConfig = ko.observable({
				'view' : [],
				'viewModel' : null
			});
			moduleUtils.createView({
				'viewPath' : 'views/header.html'
			}).then(function(view) {
				self.headerConfig({
					'view' : view,
					'viewModel' : app.getHeaderModel()
				})
			})
		}	

		VerCitasOtroDia() {
			let self = this;
			let data = {
					url : "cita/getCitasOtroDia/"+localStorage.getItem("emailUsuario")+"/"+ self.fecha(),
					type : "get",
					contentType : 'application/json',
					success : function(response) {
						self.citas(response);
					},
					error : function(response) {
						$.confirm({title: 'Error',content: response.responseJSON.message,type: 'red',typeAnimated: true,buttons: {tryAgain: {text: 'Cerrar',btnClass: 'btn-red',action: function(){}}}});
						
					}
			};
			$.ajax(data);
		}
		
		
		
		logout() {
			let self = this;
			let data = {
					url : "login/logout",
					type : "post",
					contentType : 'application/json',
					success : function(response) {
						localStorage.clear();
						app.router.go( { path : "login" } );
					},
					error : function(response) {
						$.confirm({title: 'Error',content: response.responseJSON.message,type: 'red',typeAnimated: true,buttons: {tryAgain: {text: 'Cerrar',btnClass: 'btn-red',action: function(){}}}});
						
					}
			};
			$.ajax(data);
		}
		
	
		
		paginaInicio() {
			app.router.go({ path: "homeSanitario" });
		}
		
		verCitasOtrosDias() {
			app.router.go({ path: "verCitasOtrosDias" });
		}

		verCitasSanitario() {
			app.router.go({ path: "homeSanitario" });
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
						self.email(response[2]);
						self.centroAsignado(response[3]);
					},
					error: function(response) {
						$.confirm({title: 'Error',content: response.responseJSON.message,type: 'red',typeAnimated: true,buttons: {tryAgain: {text: 'Cerrar',btnClass: 'btn-red',action: function(){}}}});
						
					}
				};
				$.ajax(data);
		}
		
		getCentros() {
			let self = this;
			let data = {
				url : "centro/getTodos",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					 for (let i=0; i<response.length; i++) {
						let centro = {
							id : response[i].id,
							nombre : response[i].nombre,
							dosisTotales: response[i].dosisTotales,
							aforo : response[i].aforo,
							horaInicio : response[i].horaInicio,
							horaFin : response[i].horaFin,
							localidad : response[i].localidad,
							provincia : response[i].provincia,
							eliminar : function() {
								self.eliminarUsuario(response[i].dni); 
							},
							
							modificarCentros : function() {
								app.centro = this;
								app.router.go({ path: "modificarCentro" });
							},
												
						};
					}
				},
				error : function(response) {
					$.confirm({title: 'Error',content: response.responseJSON.message,type: 'red',typeAnimated: true,buttons: {tryAgain: {text: 'Cerrar',btnClass: 'btn-red',action: function(){}}}});
					
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
						if(response=="denegado"){
							app.router.go( { path : "login"} );
						}
					},
				};
				$.ajax(data);
		}
		
			
		connected() {
			accUtils.announce('Inicio page loaded.');
			document.title = "Citas posteriores";
			this.getUserConnect();
			this.comprobarRol();
			this.getCentros();
		};

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return verCitasOtrosDiasViewModel;
});
