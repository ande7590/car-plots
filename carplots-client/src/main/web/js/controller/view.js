/*
	@LoadingController
*/
function LoadingController(context) {
	this.context = context;
}

LoadingController.prototype = {
	start: function() {
		this.ui = {
			$loadingIndicator: $("#loadingIndicator"),
			$carSelection: $("#carSelection")
		}
	},
	
	setLoading: function(isLoading) {
		if (isLoading) {
			this.ui.$loadingIndicator.show();
		} else {
			this.ui.$loadingIndicator.hide();
			this.ui.$carSelection.show();
		}
	}
}

/*
	@Help Controller
*/
function HelpController(context) {
	this.context = context;
}

HelpController.prototype = {
	start: function() {
		this.ui = {
			$make: $("select[name='makeSelect']"),
			$model: $("select[name='modelSelect']"),
			$year: $("select[name='yearSelect']"),
			$engine: $("select[name='engineSelect']"),
			$plotButton: $("#addPlotButton"),
			$clearButton: $("#clearPlotButton")			
		}
		
		this._setupHelpText();
		this._setupArrows();
	},
	
	_setupHelpText: function() {
		
		var that = this;
		
		// setup the help text for selection make, model, etc
		var helpText = "Select a Make, Model, Year, and Engine.";					
		var selectorHelp =  this.context.infoBubbleFactory.build({
			of: this.ui.$make.closest(".textBorder"),
			content: helpText			
		});			
		
		// show help text after 3 seconds
		var hndSelectorHelp = setTimeout(function() {
			selectorHelp.show();			
		}, that._getHelpDelayMS());
		this.selectorHelp = selectorHelp;
		
		// cancel/hide the help text if the user selects something
		this.ui.$make.on("selectMake", function() {			
			clearTimeout(hndSelectorHelp);
			selectorHelp.hide();
		});		
		
		// help text for plot buttons
		var buttonHelpText = this.context.infoBubbleFactory.build({
			of: this.ui.$plotButton,
			content: "Click \"Add to plot\"."
		});
		
		var hndButtonHelp = 0;
		this.ui.$engine.one("change", function() {
			hndButtonHelp = setTimeout(function() {
				if (that.ui.$engine.val() && hndButtonHelp > 0) {
					buttonHelpText.show();
				}				
			}, that._getHelpDelayMS());
			that.ui.$engine.one("change", function() {
				clearTimeout(hndButtonHelp);
				hndButtonHelp = 0;
			});
		});		
				
		this.ui.$plotButton.on("addPlot", function() {			
			clearTimeout(hndButtonHelp);
			buttonHelpText.hide();
		});		
	},
	
	_setupArrows: function() {
		var $allArrows = $(".entryItem .iconCalloutLeft");	
		var $allSelectors = $(".entryItem select");		
		
		var getSelectorValues = (function() {
			var values = [];
			$allSelectors.each(function(index, item) {
				var val = $(item).val();
				if (val) values.push(val);
			});
			return values;
		});
		
		var updateArrows = function(index, sel) {		
			$allArrows.hide();
			var vals = getSelectorValues();						
			$($allArrows.get(vals.length)).show();						
		};
				
		$allSelectors.change(function(){
			setTimeout(updateArrows, 1);
		});	
		updateArrows();
	},
	
	_getHelpDelayMS: function() {
		return 5000;
	},
	
	_setupErrorText: function() {
		
	},
	
	_isFirstVisit: function() {
		return true;
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
	@CarSelectorController
*/
function CarSelectorController(context) {
	this.context = context;
}

CarSelectorController.prototype = {
	start: function() {
		this.context.decorate(this);
		this.ui = {
			$make: $("select[name='makeSelect']"),
			$model: $("select[name='modelSelect']"),
			$year: $("select[name='yearSelect']"),
			$engine: $("select[name='engineSelect']")
		}		
		this.service = this.context.metadataService;
		
		// event handler setup
		var that = this;
		this.ui.$make.change(function() {									
			that.reset(1);
			if (that.ui.$make.val() != "") {				
				that.updateModel();
				that.ui.$make.trigger("selectMake");				
			}
		});		
		this.ui.$model.change(function() {
			that.reset(2);
			if (that.ui.$model.val() != "") {				
				that.updateYear();
				that.ui.$model.trigger("selectModel");								
			}						
		});	
			
		this.ui.$year.change(function() {
			that.reset(3);
			if (that.ui.$year.val() != "") {				
				that.updateEngine();
				that.ui.$year.trigger("selectYear");								
			}						
		});
		
		// update make only needs to be called once
		this.updateMake();
	},
	
	updateMake: function() {
		var $make = this.ui.$make;			
		var that = this;
		this.service.getMakes({
			arguments: null,
			onSuccess: function(makeData) {
				$make.find('option').remove();
				$('<option/>', {
					text: that._getEmptySelectionText(),
					value: ""
				}).appendTo($make);
				makeData.sort();
				$(makeData).each(function(index, item) {
					$('<option/>', {
						text: item,
						value: item
					}).appendTo($make);
				});
			},
			onError: this.errorHandler
		});
	},
	
	updateModel: function() {
		var $model = this.ui.$model;
		var that = this;
		this.service.getMakeModels({
			arguments: {
				make: this.ui.$make.val()
			},
			onSuccess: function(makeModelData) {				
				for (key in makeModelData) {
					makeModelData = makeModelData[key];
					break;
				}
				$model.find('option').remove();
				$('<option/>', {
					text: that._getEmptySelectionText(),
					value: ""
				}).appendTo($model);
				
				makeModelData.sort(function(a, b){
					if (that._getModelNameText(a) <
						 that._getModelNameText(b)) {
						return -1;
					}
					else if (that._getModelNameText(a) >
						 that._getModelNameText(b)) {
						return 1;
					}
					return 0;
				});
				$(makeModelData).each(function(index, item) {					
					$('<option/>', {
						text: that._getModelNameText(item),
						value: item.MakeModelID
					}).appendTo($model);
				});
			},
			onError: this.errorHandler			
		});
	},
	
	updateYear: function() {	
		var $year = this.ui.$year;
		var that = this;
		this.service.getYears({
			arguments: {
				mmid: this.ui.$model.val()
			},
			onSuccess: function(yearData) {
				for (key in yearData) {
					yearData = yearData[key];
					break;
				}
				$year.find('option').remove();
				$('<option/>', {
					text: that._getEmptySelectionText(),
					value: ""
				}).appendTo($year);
				yearData.sort();
				yearData.reverse();
				$(yearData).each(function(index, item) {
					$('<option/>', {
						text: item,
						value: item
					}).appendTo($year);
				});
			},
			onError: this.errorHandler
		});
	},
	
	updateEngine: function() {
		var $engine = this.ui.$engine;
		var that = this;
		this.service.getEngines({
			arguments: {
				mmid: that.ui.$model.val(),
				yr: that.ui.$year.val()
			},
			onSuccess: function(engineData) {
				for (key in engineData) {
					engineData = engineData[key];
					break;
				}
				$engine.find('option').remove();
				$('<option/>', {
					text: that._getEmptySelectionText(),
					value: ""
				}).appendTo($engine);
				engineData.sort(function(a,b) {
					if (a.DisplacementCC < b.DisplacementCC)
						return -1;
					else if (a.DisplacementCC > b.DisplacementCC)
						return 1;					
					return 0;
				});
				$(engineData).each(function(index, item) {
					$('<option/>', {
						text: that._getEngineText(item),
						value: item.CarEngineID
					}).appendTo($engine);
				});
			},
			onError: this.errorHandler
		});
	},	
	
	reset: function(itemNumber) {		
		var that = this;
		// if the user selects an item that is 
		var resetChain = [
			function() {
				that.ui.$make.prop('selectedIndex', 0);				
			},
			function() {
				that.ui.$model.find('option').remove();
			},
			function() {
				that.ui.$year.find('option').remove();			
			},
			function() {
				that.ui.$engine.find('option').remove();
			}
		];
		itemNumber = itemNumber || 0;
		for (var i = itemNumber; i < resetChain.length; i++) {
			resetChain[i]();
		}
	},
	
	errorHandler: function() {
		alert("Network error");
	},
	
	_getModelNameText: function(obj) {		
		return obj.ModelName.replace(/^\W*/, "").replace(/\W$/, "");
	},
	
	_getEmptySelectionText: function() {
		return "<Select One>";
	},
	
	_getEngineText: function(item) {
		var liters = item.DisplacementCC / 1000;
		return [liters, 'L ', item.Cylinders, ' cyl. ', 
			' w/ ', item.Horsepower, 'HP'].join('');
	}
}

/*
	@InfoBubbleControllerFactory
*/
function InfoBubbleControllerFactory(context) {	
	this.context = context;
}

InfoBubbleControllerFactory.prototype = {
	
	start: function() {
		this.context.decorate(this);
		this.ui = {
			$container: $("div.container")
		}
	},
	
	build: function(options) {		
		
		options.view = $(this._createInfoBubbleViewHTML())
			.hide()
			.appendTo(this.ui.$container);
				
		var controller = new InfoBubbleViewController(
			this.context, options);
			
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
		$forElement: $(opt.forElement),
		$textArea: $(opt.view).find(opt.textAreaSelector)
	}
		
	this._init();
}

InfoBubbleViewController.prototype = {

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
