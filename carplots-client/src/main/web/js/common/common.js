/*
	@ContextManager
	The ContextManager provides dependency injection without the need
	for determining dependency graphs.  Each object that is added via register()
	may either be usable before finalize() (only if it has no other dependencies 
*/

function ContextManager(context) {
	this._startOrder = [];
	this.context = context;
}

ContextManager.prototype = {
	
	register: function(name, object, usableBeforeFinalize) {
		this.context[name] = object;
		this._startOrder.push(name);
	},

	start: function() {
		var context = this.context;
		for (var i=0; i<this._startOrder.length; i++) {
			var objName = this._startOrder[i];
			var obj = context[objName];
			if (typeof(context.decorate) !== 'undefined') {
				context.decorate(obj);
			}
			if (typeof(obj.start) === "function") {				
				obj.start();
			}
		}
	}
}
