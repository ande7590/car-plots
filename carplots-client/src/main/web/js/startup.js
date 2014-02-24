/*
	CarPlots initialization
*/
(function () {

	$(document).ready(function() {
		setup(function() {
			finishLoading();
		});			
	});
	
	function finishLoading(){
		$("#loadingIndicator").hide();
		$("#carSelection").show();
	}

	function setup(callback) {

		var context = {};
		var contextManager = new ContextManager(context);
		
		// Create a "decorate" function in the context that
		// will add common functionaly to any objects that chooses
		// to accept it (i.e. by calling decorate(this) ) ;
		contextManager.register("decorate", function(that) {
			that["_extend"] = extend;
			that["_checkArguments"] = checkArguments;
			that["_isNullOrUndef"] = isKeyNullOrUndefined;
		}, true);
		
		// Generate services		
		var carplotsService = new RESTService(ServiceDefinitions.carplots);
		var metadataService = new RESTService(ServiceDefinitions.metadata);
		
		// Setup data service functionality
		contextManager.register("CarplotsService", carplotsService);
		contextManager.register("MetadataService", metadataService);
		
		// Setup view functionality
		contextManager.register("carPlotViewController", 
			new CarPlotViewController(context));
		contextManager.register("infoBubbleFactory", 
			new InfoBubbleFactory(context));
		contextManager.register("autocompleteController", 
			new AutocompleteController(context));
		contextManager.register("loadingController", 
			new LoadingController(context));
		
		//Finalize context and begin application
		contextManager.finalize();
		contextManager.start();		
		
		callback(context);
	}

	//copy properties from src -> target
	function extend(src, target, overwrite) {
		overwrite = overwrite || true;
		target = target || this;
		for (var k in src) {
			if (overwrite || typeof(target[k]) === "undefined") {
				target[k] = src[k];
			}
		}
	}
	
	//utility methods to require parameters
	function checkArguments(requiredFields, options) {
		var invalid = [];
		var isValid = true;
		for (var i=0; i < requiredFields; i++) {
			var name = requiredFields[i];
			if (typeof(requiredFields[name]) === "undefined" || requiredFields[name] === null) {
				invalid.push(name);
				isValid = false;
			}
		}
		return {
			invalid: invalid,
			isValid: isValid
		}
	}

	function isKeyNullOrUndefined(object, key) {
		return (typeof(object[key]) === "undefined" || object[key] === null);
	}

})();
