define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
	'jquery' ], function(ko, app, moduleUtils, accUtils, $) {


	class ModCitaViewModel {
		constructor() {
			var self = this;

			self.cita = ko.observable();
			self.cita = app.cita;
			self.botonFecha = ko.observable(1);
			var date = app.cita.fecha.toString().split(" ",1);
			var f = new Date();
			var final = f.toISOString().slice(0, 10);
			self.inputFecha=ko.observable(true);
			var today = Date.now();
			var final3 =  app.cita.fecha;
			self.nombre = ko.observable("");
			self.tipoUsuario = ko.observable("");

			if(today>f.getTime()){
				console.log("No se puede eliminar");
				self.botonFecha(2);
			}else{
				console.log("Se puede eliminar");
				self.botonFecha(1);
			}
			
			self.fecha = ko.observable(final3);
			self.hora = ko.observable(final4);
			
			self.dniPaciente = ko.observable();




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


		eliminarCita() {
			var self = this;

			let info = {
					id : app.cita.id,
					dniPaciente : app.cita.dniPaciente,
					fecha: app.cita.fecha,
					centrosSanitarios : app.cita.centroAsignado,
			};
			let data = {
					data : JSON.stringify(info),
					url : "cita/eliminarCita/" + app.cita.id,
					type : "put",
					contentType : 'application/json',
					success : function(response) {
						app.router.go({ path: "homePaciente" });
						$.confirm({
							title: 'Confirmado',
							content: 'Cita eliminada',
							type: 'green',
							typeAnimated: true,
							buttons: {
								Cerrar: function () {
								}
							}
						});
					},
					error: function(response) {
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
						app.router.go( { path : "login" } );
					},
					error : function(response) {
						$.confirm({title: 'Error',content: response.responseJSON.message,type: 'red',typeAnimated: true,buttons: {tryAgain: {text: 'Cerrar',btnClass: 'btn-red',action: function(){}}}});

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
		
		comprobarRol() {	
				let self = this;
				let data = {
					url: "login/comprobarRolPersonalDeCitasAndPaciente",
					type: "get",
					contentType: 'application/json',
					success: function(response) {
						if(response=="denegado"){
							app.router.go( { path : "login"} );
						}
					},
					error: function(response) {
						$.confirm({title: 'Error',content: response.responseJSON.message,type: 'red',typeAnimated: true,buttons: {tryAgain: {text: 'Cerrar',btnClass: 'btn-red',action: function(){}}}});
						
					}
				};
				$.ajax(data);
		}

		connected() {
			accUtils.announce('Inicio page loaded.');
			document.title = "Modificar cita";
			this.comprobarRol();
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
