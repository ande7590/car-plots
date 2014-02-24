/*
	@LoadingController
*/
function LoadingController(context) {
	this.context = context;
}

LoadingController.prototype = {
	start: function() {
		this.ui = {
			
		}
	},
	setLoading: function(itemName, isLoading) {
		
	}
}

/*
	@GraphController
*/
function GraphController(context) {
}

GraphController.prototype = {
	start: function() {
		this.ui = {
		}
	},
	addGraph: function() {
	},
	clear: function() {
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
			$autoComplete: $("input[name='selectCar']")
		}
	},
	_fetchData: function() {
		
	}
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
