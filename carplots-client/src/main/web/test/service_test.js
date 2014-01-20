var testData = {
	carplots: {
		getPlots: [
		{MakeModelID: "1", Year: "2003"},
		{MakeModelID: "1", Year: "2003", StartYear: "2011", EndYear:"2013", CarEngineID:"369"}]
	},
	metadata : {
		getMakeModels: [
			null, {make: "Honda"}
		],
		getMakes: [
			null
		],
		getEngines: [
			{mmid: 1}, {mmid:1, yr: 2003}
		],
		getYears: [
			{mmid: 1}
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

			// Call every method in each service using the data (arguments)
			// specified at the head of the file.  Append the results to 
			// the page when the callback succeeds or fails.
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
								service[methodName]({
									arguments: args,
									onSuccess: function(data) {
										methodResult.append($('<div/>', {
											class: 'method_call',
											html: [
												JSON.stringify(args), ': ', 
												JSON.stringify(arguments)].join('')
										}));
									},
									onError: function() {
										methodResult.append($('<div/>', {
											class: 'method_call_error',
											html: [
												"**ERROR** ",
												JSON.stringify(args), ': ', 
												JSON.stringify(arguments)].join('')
										}));
									}
								});
							} catch (ex) {
										methodResult.append($('<div/>', {
											class: 'method_call_error',
											html: [
												"**ERROR** ",
												ex.toString(), 
												" ",
												JSON.stringify(args)].join('')
										}));
							}
						})(methodResult);
					}
				}
			}
		}
});
