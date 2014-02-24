/*
 * A RESTService takes a declarative (json) service definition
 * and generates a proxy object for interacting with the RESTful
 * service.  The actual object returned by the RESTService constructor
 * is NOT an instance of RESTService.
 */
function RESTService(serviceDefinition) {
	this.serviceDefinition = serviceDefinition

	//create and return a service proxy for the serviceDefinition.
	return this._createService();
}

RESTService.prototype = {
	
	_createService: function() {
		var service = {};
		var svcDef = this.serviceDefinition;
		for (i=0; i < svcDef.methods.length; i++) {
			var methodDef = svcDef.methods[i];
			if (typeof(service[methodDef.name]) === "function") {
				throw new Error("Duplicate method definition: " + methodDef.name);
			}
			else if (methodDef.methodType == "GET") {
				service[methodDef.name] = this._createServiceMethod(methodDef);
			} else {
				throw new Error("Unsupported method: " + def.method);
			}
		}
		return service;
	},

	_createServiceMethod: function(methodDef) {
		var svcDef = this.serviceDefinition;
		var methodUrl = [svcDef.url, "/", methodDef.location].join('');
		var dataType = svcDef.dataType || "json";
		var methodArgs = methodDef.args || [];
		var methodArgParsers = [];

		//arguments are passed into the service methods as an object,
		//e.g. ":arg1/:arg2" in the method definition will expect 
		//{arg1: "foo", arg2: "bar"} as the argument to the js function. 
		for (var i=0; i<methodArgs.length; i++) {
			//Create a parser to validate argument objects
			methodArgParsers.push(this._createArgParser(methodArgs[i]));
		}
		//create the service method
		var that = this;
		return function(args, success_callback, error_callback) {
			success_callback = args.onSuccess || that._defaultSuccessHandler;
			error_callback = args.onError || that._defaultErrorHandler;
			var argsUrl = null;
			for (var i=0; i<methodArgParsers.length; i++) {
				var parserResult = methodArgParsers[i](args.arguments);
				if (parserResult) {
					argsUrl = parserResult;
					break;
				}
			}
			if (argsUrl === null) {
				throw new Error("Invalid arguments");
			}
			jQuery.ajax({
					type: "GET",
					url: methodUrl + argsUrl,
					dataType: dataType,
					success: success_callback,
					error: error_callback
			});
		}
	},

	_createArgParser: function(argDefinition) {
		var argParts = (argDefinition)? 
		 	argDefinition.replace(/:/g, "").split(/\//) : 
			[];

		//if there are no arguments, return a generic
		//parser that expects zero arguments
		if (argParts.length == 0) {
			return this._emptyArgParser;
		}

		//if there are arguments, create a parser
		//specific to the argument names
		var validArgNames = {};
		var numValidArgNames = argParts.length;
		for(var i=0; i<argParts.length; i++) {
			validArgNames[argParts[i]] = true;
		}

		//The parser checks to see if the expected arguments 
		//are present in the "args" object, it does not allow
		//extraneous arguments.  If the correct arguments are present, it
		//returns a string with the arguments in the proper order
		//for the restful service as specified in the service definition.
		return function(args) {
			var numFound = 0;
			var hasValidArgs = true;
			for (argName in args) {
				if (!(argName in validArgNames)) {
					hasValidArgs = false;
					break;
				} else {
					numFound++;
				}
			}
			if (hasValidArgs && numFound == numValidArgNames) {
				// good arguments, return URL for parameters
				var parsedArgs = [];
				for (var i=0; i<argParts.length; i++) {
					var argName = argParts[i];
					var argVal = args[argName];
					parsedArgs.push(argVal);
				}
				parsedArgs.push('');
				return parsedArgs.join('/');
			} else {
				// bad arguments
				return null;
			}
		}
	},

	// a default parser that can be used for methods that take zero arguments
	_emptyArgParser: function() {
		return (arguments.length == 0 ||
				arguments.length === 1 && arguments[0] === null)? 
			"/" 
			: null;
	},

	_defaultSuccessHandler: function() {
		alert("service success");
	},

	_defaultErrorHandler: function() {
		alert("service error");
	}
}
