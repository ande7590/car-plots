/*
	@ContextManager
	The ContextManager provides dependency injection without the need
	for determining dependency graphs.  Each object that is added via register()
	may either be usable before finalize() (only if it has no other dependencies 
*/

function ContextManager(context) {
	this._hasFinalized = false;
	this._waitForFinalizeContext = {};
	this.context = context;
}

ContextManager.prototype = {
	
	register: function(name, object, usableBeforeFinalize) {
		var usable = usableBeforeFinalize || false;
		if (usable) {
			this.context[name] = object; 
		}
		else {
			this._waitForFinalizeContext[name] = object;
		}
	},

	finalize: function() {
		for (k in this._waitForFinalizeContext) {
			this.context[k] = this._waitForFinalizeContext[k];
		}
		this._waitForFinalizeContext = null;
		this._hasFinalized = true;
	},

	start: function() {
		if (!this._hasFinalized) {
			throw new Error("start cannot be called before finalize.");
		}
		var context = this.context;
		for (objName in context) {
			if (typeof(context[objName].start) === "function") {
				context[objName].start();
			}
		}
	}
}
