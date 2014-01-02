if(typeof(carplots)==="undefined"){
	var carplots = {};
};

carplots.serviceFactory = {
	getServiceConfigurations: function() {
		return {
			plots: {
				name: "plots", 
				url:"http://staging-carplots.squareerror.com:5984/",
				definition: [
				{
					name: "plots",
					method: "GET",
					location: "plots/",
					dataType: "jsonp",
					args: [
						":mmid/:yr",
						":mmid/:yr/:st/:end/:eng" ]
				}]
			}, 
			metadata: {
				name: "metadata", 
				url:"http://staging-metadata.squareerror.com:5984/"
			}
		};
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

carplots.RESTService = function(config) {
	this.config = config;
	return this._generateService();
}

carplots.RESTService.prototype._generateService = function() {
	var definitions = this.config.definitions;
	var service = {};
	var baseName = "_generateMethod";
	for (i=0; i<definitions.length; i++) {
		var def = definitions[i];
		if (typeof(service[def.name]) === "function") {
			throw new Error("Duplicate method definition: " + def.name);
		}
		else if (def.method == "GET") {
			service[def.name] = this._createServiceMethod(def);
		} else {
			throw new Error("Unsupported method: " + def.method);
		}
	}
	return service;
}

carplots.RESTService.prototype._createServiceMethod = function(type, definition) {
	var method = this.config.url + definition.location;
	var dataType = definition.dataType || "jsonp";
	var methodArgs = definition.args;
	var methodArgParsers = [];
	for (var i=0; i<methodArgs; i++) {
		methodArgParsers.push(this._createArgParser(methodArgs[i]));
	}
	var that = this;
	return function(args, success_callback, error_callback) {
		success_callback = success_callback || that._defaultSuccessHandler;
		error_callback = error_callback || that._defaultErrorHandler;
		jQuery.ajax({
				type: "GET",
				url: url,
				dataType: dataType,
				success: success_callback,
				error: error_callback
		});
	}
}

carplots.RESTService.prototype._createArgParser = function(argDefinition) {
	var that = this;
	var validArgNames = {};
	var argParts = argDefinition.replace(/:/g, "").split(/\//);
	for(var i=0; i<argParts.length; i++) {
		validArgNames[argParts[i]] = true;
	}
	return function(args) {
		var numFound = 0;
		for (argName in args) {
			if (!argName in validArgNames) {
				break;
			} else {
				numFound++;
			}
		}
		return numFound == validArgNames.length;
	}
}

carplots.RESTService.prototype._defaultSuccessHandler = function() {
	alert("service success");
}

carplots.RESTService.prototype._defaultErrorHandler = function() {
	alert("service error");
}
