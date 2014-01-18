var testData = {
	carplots: {
		getPlots: [
		{mmid: "1", yr: "2003"},
		{mmid: "1", yr: "2003", st: "2011", end:"2013", eng:"369"}]
	},
	metadata : {
		getMakeModels: [
			null, {make: "Honda"}
		],
		getMakes: [

		],
		getEngines: [
		],
		getYears: [
		]
	}
}


$(document).ready(function() {
		var factory = carplots.service.factory;
		for (defName in carplots.service.definitions) {
			var def = carplots.service.definitions[defName];
			var service = factory.createService(def);
			var testArgs = testData[defName];
			var svcResults = $("<div>").html(
				["<h1>", defName, "</h1>"].join(''));
			$("#testResults").append(svcResults);
			for (methodName in service) {
				var methodResult = $("<div>").addClass("service_result");
				svcResults.append(methodResult);
				if (typeof(testArgs[methodName]) === "undefined") {
					methodResult.html(["<b>Missing: ", methodName ,"</b>"].join(''));
				} else {
					var argArray = testArgs[methodName];
					methodResult.html(["<h3>", methodName, "</h3>"].join(''));
					for (var i=0; i<argArray.length; i++) {
						(function(methodResult) {
							var args = argArray[i];
							try {
								service[methodName](args, function(data) {
									methodResult.append($('<div/>', {
										class: 'method_call',
										html: [
											JSON.stringify(args), ': ', 
											JSON.stringify(arguments)].join('')
									}));
									}, function() {
										var args = arguments;
									});
							} catch (ex) {
								methodResult.append("error");
							}
						})(methodResult);
					}
				}
			}
		}
});
