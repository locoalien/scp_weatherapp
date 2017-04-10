/* eslint-disable max-params */
sap.ui.define([
	// Incluya las dependencias por su espacio de nombres
	"sap/ui/core/mvc/Controller"
],
// Las dependencias se pasan a esta función en el mismo orden
function(Controller) {
	"use strict";

	return Controller.extend("sap.hana.cloud.samples.weather_app.view.Details", {

		// El método onInit es llamado automáticamente por el framework cuando
		// se ha creado la instancia de vista
		onInit: function() {
			var oView = this.getView(), oRouter = this.getOwnerComponent().getRouter();

			oRouter.attachRouteMatched(function(oEvent) {
				// El evento 'routeMatched' se activa siempre que una ruta de navegación es accionada
				var mParameters = oEvent.getParameters();
				if (mParameters.view === oView) {
					// ... pero solo queremos responder al evento si la ruta de este
					// vista / controlador se igualó
					// Luego obtenemos algunos parámetros de los parámetros de la ruta
					// y enlaza la vista al producto desde la url
					var mArguments = oEvent.getParameter("arguments");
					var sId = mArguments.id;

					var aData = oView.getModel().getData();

					for (var i = 0; i < aData.length; i++) {

						if (aData[i].id === sId) {
							break;
						}

					}

					oView.bindElement("/" + i);

					// Detalles del modelo a consultar
					var oAppModel = new sap.ui.model.json.JSONModel();

					jQuery.getJSON("api/v1/cities/" + sId + "/weather").done(function(mData) {
						oAppModel.setData(mData);
					});

					oView.setModel(oAppModel, "details");

					oView.getParent().to(oView);
				}
			});
		},

		formatAddToFavVisible: function(sGuid) {

			if (sGuid) {
				return false;
			} else {
				return true;
			}

		},

		formatRemoveFromFavVisible: function(sGuid) {
			return !this.formatAddToFavVisible(sGuid);
		},

		handleRemoveFromFavorite: function(oEvent) {

			var oContext = oEvent.getSource().getBindingContext();
			var sId = oContext.getProperty().id;

			var that = this;
			$.ajax({
				url: "api/v1/cities/" + sId,
				method: "DELETE",
				success: function(oResult, oResponse) {

					that.getView().getModel().setData(oResult);

				}
			});

		},

		handleAddToFavorite: function(oEvent) {

			var oData = oEvent.getSource().getModel("details").getData();

			var that = this;
			$.ajax({
				url: "api/v1/cities/",
				method: "POST",
				contentType: "application/json",
				dataType: "json",
				data: JSON.stringify({
					"id": oData.id,
					"name": oData.name,
					"countryCode": oData.sys.country
				}),
				success: function(oResult, oResponse) {

					that.getView().getModel().setData(oResult);

				}
			});

		}

	});
});
