/*
	CarPlots initialization
*/
(function () {

	$(document).ready(function() {
		setup(function() {
			finishLoading();
			$("html").trigger("carplotsLoaded");
		});			
	});
	
	function finishLoading(){
		$("#loadingIndicator").hide();
		$("#carSelection").show();
	}
	
	function getCommonUI() {
		return {
			// container
			$container: $("div.container"),
			// selects
			$make: $("select[name='makeSelect']"),
			$model: $("select[name='modelSelect']"),
			$year: $("select[name='yearSelect']"),
			$engine: $("select[name='engineSelect']"),
			$allSelects: $(".entryItem select"),
			// graph			 
			$graph: $("#graphCanvas"),			
			// buttons
			$plotButton: $("#addPlotButton"),
			$clearButton: $("#clearPlotButton"),
		};
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
		contextManager.register("carplotsService", carplotsService);
		contextManager.register("metadataService", metadataService);
		
		// Setup view functionality
		contextManager.register("commonUI", getCommonUI());		
		contextManager.register("infoBubbleFactory", 
			new InfoBubbleControllerFactory(context));
		contextManager.register("carSelectorController", 
			new CarSelectorController(context));
		contextManager.register("loadingController", 
			new LoadingController(context));
		contextManager.register("helpController", 
			new HelpController(context));
		contextManager.register("graphController", 
			new GraphController(context));
		contextManager.register("graphButtonController", 
			new GraphButtonController(context));
		contextManager.register("legendController", 
			new LegendController(context));
		
		//Finalize context and begin application
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
			if (typeof(requiredFields[name]) === "undefined" || 
				 requiredFields[name] === null) {
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
