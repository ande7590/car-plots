$(document).ready(function() {

	function getServices(factory, serviceDefinitions) {
		var services = {};
		for (svcName in serviceDefinitions) {
			var def = serviceDefinitions[svcName];
			services[svcName + "Service"] = factory.createService(def);
		}
		return services;
	}

	// very basic dependency injection
	var injector = new Injector(
		getServices(carplots.service.factory, carplots.service.definitions),
		carplots.controller,
		carplots.ui);
});
