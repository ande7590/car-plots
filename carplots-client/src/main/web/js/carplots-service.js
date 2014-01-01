if(typeof(carplots)==="undefined"){
	var carplots = {};
};

carplots.serviceFactory = {
	getServiceConfigurations: function() {
		return {
			plots: {name: "plots", url:"http://staging-carplots.squareerror.com:5984/"}, 
			metadata: {name: "metadata", url:"http://staging-metadata.squareerror.com:5984/"}}
	},
	getService: function(serviceConfiguration) {
		var svcName = serviceConfiguration.name;
		var fnName = ["_get", svcName.charAt(0).toUpperCase(), 
			svcName.slice(1), "Service"].join('');
		if(typeof(this[fnName]) != "function") {
			throw new Error("Service is not registered: " + svcName);
		}
		return this[svcName](serviceConfiguration);
	},
	_getPlotsService: function(config){
		return new carplots.plotsService(config);
	},
	_getMetaDataService: function(config){
		return new carplots.metadataService(config);
	}
};

carplots.plotsService = function(config) {

}

carplots.plotsService.prototype = {
	
};

carplots.metadataService = function(config) {

}

carplots.metadata.prototype = {

};
