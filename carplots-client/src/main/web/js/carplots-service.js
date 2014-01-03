if(typeof(carplots)==="undefined"){
	var carplots = {};
};

carplots.service = {};

carplots.service.definitions = {
	plots: {
		name: "plots", 
		url:"http://staging-carplots.squareerror.com:5984/",
		methods: [
		{
			name: "plots",
			methodType: "GET",
			dataType: "jsonp",
			location: "plots/",
			args: [
				":mmid/:yr",
				":mmid/:yr/:st/:end/:eng"]
		}]
	},
	metadata: {
		name: "metadata", 
		url:"http://staging-metadata.squareerror.com:5984/"
	}
};

carplots.service.factory = {
	createService: function(definition) {
		return new carplots.service.RESTService(definition);
	}
};

carplots.service.RESTService = function(serviceDefinition) {
	this.serviceDefinition = serviceDefinition
	return this._createService();
}

carplots.service.RESTService.prototype = {
	
	_createService: function() {
		var service = {};
		var svcDef = this.serviceDefinition;
		for (i=0; i < svcDef.methods.length; i++) {
			var methodDef = svcDef.methods[i];
			if (typeof(service[methodDef.name]) === "function") {
				throw new Error("Duplicate method definition: " + methodDef.name);
			}
			else if (methodDef.methodType == "GET") {
				service[def.name] = this._createServiceMethod(methodDef);
			} else {
				throw new Error("Unsupported method: " + def.method);
			}
		}
		return service;
	},

	_createServiceMethod: function(methodDef) {
		var svcDef = this.serviceDefinition;
		var methodUrl = svcDef.url + methodDef.location;
		var dataType = svcDef.dataType || "jsonp";
		var methodArgs = svcDef.args;
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
					url: "http://staging-carplots.squareerror.com:5984/plots/1/2003",
					dataType: dataType,
					success: success_callback,
					error: error_callback
			});
		}
	},

	_createArgParser: function(argDefinition) {
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
	},

	_defaultSuccessHandler: function() {
		alert("service success");
	},

	_defaultErrorHandler: function() {
		alert("service error");
	}
}
