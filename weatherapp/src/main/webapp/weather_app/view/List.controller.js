/* eslint-disable max-params */
sap.ui.define([
	// Incluya las dependencias por su espacio de nombres
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/Sorter"
],
//Las dependencias se pasan a esta función en el mismo orden
function(Controller, Sorter) {
	"use strict";

	return Controller.extend("sap.hana.cloud.samples.weather_app.view.List", {

		_showWeatherInfoForListItem: function(oListItem) {
			// Obtener el contexto vinculante del elemento de lista
			// El contexto de enlace contiene los datos del modelo para el registro de datos
			// que se muestra por el elemento de lista.
			// Pasando la ruta del contexto de enlace al enrutador,
			// puede usarlo en la vista Detalles y vincular la vista a la ruta de acceso
			// para que se puedan mostrar los mismos datos
			// La comprobación del contexto antes de leer el camino es sólo una
			// solución para el primer ejemplo en el que no se invoca ningún modelo
			var oRouter = this.getOwnerComponent().getRouter(), oContext = oListItem.getBindingContext(), sPath = oContext ? oContext.getPath() : "0";

			oRouter.navTo("Details", {
				id: oContext.getProperty().id,
				path: encodeURIComponent(sPath)
			});
		},

		handleHomeButtonPress: function( /* oEvent */) {
			var oRouter = this.getOwnerComponent().getRouter();
			oRouter.navTo("Launchpad");
		},

		handleListItemPress: function(oEvent) {
			var oListItem = oEvent.getParameter("listItem");
			this._showWeatherInfoForListItem(oListItem);
		},

		handleSearch: function(oEvent) {
			var sTerm = oEvent.getParameter("query");

			var oAppModel = this.getView().getModel();

			jQuery.getJSON("api/v1/weather?q=" + sTerm).done(function(mData) {
				oAppModel.setData([
					{
						id: mData.id,
						name: mData.name
					}
				]);
			});
		}
	});
});
