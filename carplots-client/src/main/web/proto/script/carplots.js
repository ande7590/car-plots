/*
	CarPlots initialization
*/
(function () {

	$(document).ready(function() {
		bootstrap();
	});

	function bootstrap() {

		var context = {};
		var contextManager = new ContextManager(context);
		
		//Add function to "decorate" objects with the below properties
		contextManager.register("decorate", function(that) {
			that["_extend"] = extend;
			that["_checkArguments"] = checkArguments;
			that["_isNullOrUndef"] = isKeyNullOrUndefined;
		}, true);
		
		//Register objects that may have dependencies in context
		contextManager.register("carPlotViewController", 
			new CarPlotViewController(context));
		contextManager.register("infoBubbleFactory", 
			new InfoBubbleFactory(context));
		contextManager.register("autocompleteController", 
			new AutocompleteController(context));
		
		//Finalize context and begin application
		contextManager.finalize();
		contextManager.start();
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

/*
	@AutocompleteController
*/
function AutocompleteController(context) {
	this.context = context;
}

AutocompleteController.prototype = {
	start: function() {
		this.context.decorate(this);
		this.ui = {
			$autoComplete: $("")
		}
	},
	
	_fetchData: function() {}
}

/*
	@CarPlotViewController
*/
function CarPlotViewController(context) {
	this.context = context;
}

CarPlotViewController.prototype = {
	
	start: function() {
		this.context.decorate(this);
		this.ui = {
			$selectCar: $("input[name='selectCar']"),
			$selectLocation: $("input[name='selectLocation']")
		};

		this._createInfoBubbles();
		this._registerEventHandlers();
	},

	_createInfoBubbles: function() {
		
		var ctx = this.context;

		var selectCarContent = "Enter a car name like \"Toyota Camry\",<small> options will appear as you type.</small>";
		var selectLocationContent = "Enter a zipcode like \"90210\", <small>or click Advanced Selection</small>.";

		this.infoBubbles = {
			selectCar: ctx.infoBubbleFactory.build({
				of: this.ui.$selectCar.closest(".textBorder"),
				content: selectCarContent
			}),			
			selectLocation: ctx.infoBubbleFactory.build({
				of: this.ui.$selectLocation.closest(".textBorder"),
				content: selectLocationContent
			})
		}
	},

	_registerEventHandlers: function() {
		var that = this;
		$(".infoBubbleItem").focus(function() {
			var name = this.name || "";
			$(this).data("placeholderText", $(this).attr("placeholder"));
			$(this).attr("placeholder", "");
			if (name in that.infoBubbles) {
				that.infoBubbles[name].show();
			}
		});
		$(".infoBubbleItem").blur(function() {
			$(this).attr("placeholder", $(this).data("placeholderText"));
			var name = this.name || "";
			if (name in that.infoBubbles) {
				that.infoBubbles[name].hide();
			}
		});
	}
}

/*
	@InfoBubbleFactory
*/
function InfoBubbleFactory(context) {
	context.decorate(this);
	this.context = context;
}

InfoBubbleFactory.prototype = {
	build: function(options) {
		
		var viewHTML = this._createInfoBubbleViewHTML();
		var view = $(viewHTML).hide();
		$("div.container").append(view);
		options["view"] = view;
		
		var controller = new InfoBubbleViewController(this.context, options);
		return controller;		
	},

	_createInfoBubbleViewHTML: function(infoBubbleId) {
		infoBubbleIdText = (typeof(infoBubbleId) !== "undefined")? 
			" id='" + infoBubbleId + "' " : "";
		return [
			"<div class='infoBubbleContainer'", infoBubbleIdText, ">",
				"<div class='icon iconInformation'></div>",
				"<div class='infoBubbleText'>",
					"<div class='infoBubbleTextInner'>",
						"<span class='text'></span>",
					"</div>",
				"</div>",
			"</div>"].join('');
	}
}

/*
	@InfoBubbleController
*/
function InfoBubbleViewController(context, options) {
	
	context.decorate(this);
	
	this.options = {
		view: null,
		content: null,
		of: null,
		textAreaSelector: ".text",
		my: "left bottom",
		at: "right center",
		isContentHTML: true 
	};
	this._extend(options, this.options);

	var opt = this.options;
	this.ui = {
		$view: $(opt.view),
		$forElement: $(opt.forElement)
	}
	this.ui["$textArea"] = this.ui.$view.find(opt.textAreaSelector);
	
	this._init();
}

InfoBubbleViewController.prototype = {
	start: function() {

	},

	_init: function() {
		if (this.options.isContentHTML) {
			this.ui.$textArea.html(this.options.content);
		}
		else {
			this.ui.$textArea.text(this.options.content);
		}
	},

	show: function() {
		this._showEffect();
	},

	hide: function() {
		this._hideEffect();
	},

	_showEffect: function() {
		var that = this;
		that.ui.$view.show().position({
			my: that.options.my,
			at: that.options.at,
			of: that.options.of
		}).hide();
		this.ui.$view.find(".infoBubbleText, .icon").hide(0, function() {
			that.ui.$view.show("puff", "swing", 200, function() {
				that.ui.$view.find(".infoBubbleText, .icon").show("bounce", "linear", 30);
			});
		});
	},

	_hideEffect: function() {
		this.ui.$view.hide();
		this.ui.$view.position({at: $("body")});
	}
}	



