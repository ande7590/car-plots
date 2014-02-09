function Injector() {
	this.registry = {};
	for (var i=0; i<arguments.length; i++){
		jQuery.extend(this.registry, arguments[i]);
	}
}

Injector.prototype.register = function(key, object) {
	if ((key in this.registry)) {
		throw new InjectorError("Key is already registered: " + key);
	}
	this.registry[key] = object;
}

Injector.prototype.resolve = function(key) {
	if (!(key in this.registry)) {
		throw new InjectorError("Key is not registered");
	}
	return this.registry[key];
}

Injector.prototype.inject = function(objects) {

	var that = this;
	// inspect and inject dependencies as defined by object
	$.each(objects, function(key, obj) {
		if (typeof(obj.dependencies) !== "object" || 
				!(obj.dependencies instanceof Array)) {
			return;
		}
		$.each(obj.dependencies, function(idx, dependencyName) {
			object[dependencyName] = that.resolve(dependencyName);
		});
	});
	// call start() method if it exists once injection is done,
	// so that objects may finish constructor actions that require
	// external objects.
	$.each(objects, function(key, obj) {
		if (typeof(obj.start) === "function") {
			obj.start();
		}
	});
}

function InjectorError(message) {
	this.message = message;
}

