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
			$allIcons: $(".entryItem .icon")
		};		
		this._extend(this.context.commonUI, this.ui); 
				
		this._setupHelpText();
		this._setupSelectorIcons();
		this._setupMissingDataWarnings();				
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
		this.ui.$make.change(function() {			
			clearTimeout(hndSelectorHelp);
			hndSelectorHelp = 0;
			selectorHelp.hide();
		});		
		
		// help text for plot buttons
		var buttonHelpText = this.context.infoBubbleFactory.build({
			of: this.ui.$plotButton,
			content: "Click here to view the plot."
		});
		
		// cancel/hide plot button help if engine 
		// changes or plot button is clicked
		var hndButtonHelp = 0;
		var hndHideButtonHelp = 0;
		this.ui.$engine.one("change", function() {
			hndButtonHelp = setTimeout(function() {
				if (that.ui.$engine.val() && hndButtonHelp > 0) {
					buttonHelpText.show();
				}				
			}, that._getHelpDelayMS());			
			that.ui.$plotButton.click(function() {
				clearTimeout(hndButtonHelp);
				clearTimeout(hndHideButtonHelp);
			});			
			that.ui.$engine.one("updated", function() {
				clearTimeout(hndButtonHelp);
				clearTimeout(hndHideButtonHelp);
				hndButtonHelp = 0;
			});
		});		
				
		this.ui.$plotButton.on("addPlot", function() {			
			clearTimeout(hndButtonHelp);
			buttonHelpText.hide();
		});		
	},
	
	_setupSelectorIcons: function() {
		
		var that = this;
		var $allIcons = this.ui.$allIcons;	
		var $allSelects = this.ui.$allSelects;

		// update the little green arrows
		var updateIcons = function() {			
			$allIcons.hide();
			var vals = that._getSelectValues();						
			// show the default icon (arrow)
			that._getIcon(vals.length).show();					
		};
		
		// setup event handlers for updating arrows,
		// AND for missing data icons		
		$allSelects.change(function() {
			updateIcons();
		});	
		updateIcons();
	},
	
	_setupMissingDataWarnings: function() {
		
		var that = this;
		var $allIcons = this.ui.$allIcons;
		var $allSelects = this.ui.$allSelects;		
		
		var helpText = "Please make another selection.";
		var missingDataHelp = this.context.infoBubbleFactory.build({
			of: this.ui.$make.closest(".textBorder"),
			content: helpText			
		});
		
		$allSelects.on("updated", function() {
			var vals = that._getSelectValues();
			var lastSelectIdx = Math.max(0, vals.length - 1);
			var lastSelect = that._getSelect(lastSelectIdx);				
			var lastIcon = that._getIcon(lastSelectIdx);
			var nextSelect = that._getSelect(vals.length);
			$allIcons.removeClass("iconWarning");
			var nextOptions = nextSelect.find("option");
			if (nextOptions.length == 1) {
				lastIcon.addClass("iconWarning");
				$allIcons.hide();
				lastIcon.show();
				nextOptions.text("<No Data>");
				missingDataHelp.options.of = lastSelect
					.closest(".textBorder");
				missingDataHelp.show(false);
				$("html").click(function() {
					missingDataHelp.hide();
				});
			} else {
				missingDataHelp.hide();
			}
		});		
	},
	
	_getHelpDelayMS: function() {
		return 10000;
	},
	
	_getHelpShowMS: function() {
		return 6000;
	},
	
	_isFirstVisit: function() {
		return true;
	},
	
	_getSelectValues: function() {
		var values = [];
		this.ui.$allSelects.each(function(index, item) {
			var val = $(item).val();
			if (val) values.push(val);
		});
		return values;
	},
	
	_getIcon: function(idx) {
		return $(this.ui.$allIcons.get(idx));
	},
	
	_getSelect: function(idx) {
		return $(this.ui.$allSelects.get(idx));
	}
}

/*
	@GraphButton Controller	
*/
function GraphButtonController(context) {
	this.context = context;
}

GraphButtonController.prototype = {
	start: function() {
		var commonUI = this.context.commonUI;
		this.ui = {
			$plotButton: commonUI.$plotButton,
			$clearButton: commonUI.$clearButton,
			$allSelects: commonUI.$allSelects,
			$engine: commonUI.$engine,
			$model: commonUI.$model,
			$make: commonUI.$make,
			$year: commonUI.$year
		}
		
		var graphController = this.context.graphController;
				
		// setup error messages
		var errorText = "There isn't any data for your selection." +
			" Please select again.";		
		var noDataInfoController = this.context.infoBubbleFactory.build({
			of: this.ui.$plotButton.closest(".textBorder"),
			content: errorText,
			icon: "iconWarning"		
		});
		
		var maxPlotsText = "Too many plots. Click \"Clear Plot\"" +
			" or remove one from the legend.";		
		var maxPlotsInfoController = this.context.infoBubbleFactory.build({
			of: this.ui.$clearButton.closest(".textBorder"),
			content: maxPlotsText,
			icon: "iconWarning"		
		});
		
		var that = this;
		var carDataService = this.context.carplotsService;

		// enable/disable buttons
		var buttonEnabler = function() {
			if (that._isSearchDone()) {
				that.ui.$plotButton.show();
				that.ui.$clearButton.show();
			} else {
				that.ui.$plotButton.hide();
				if (!that.context.graphController.hasPlot()) {
					that.ui.$clearButton.hide();
				}
			}
		}
		buttonEnabler();
		this.ui.$allSelects.change(buttonEnabler);
		
		var legendController = that.context.legendController;
	
		// handle search		
		this.ui.$plotButton.click(function() {			
			if (graphController.hasMaxPlots()) {
				maxPlotsInfoController.show(false);
				$("html").click(function() {
					maxPlotsInfoController.hide();
				});
			} else {
				carDataService.getPlots({
					arguments: {
						mmid: that.ui.$model.val(),
						yr: that.ui.$year.val(),					
						eng: that.ui.$engine.val()
					},
					onSuccess: function(data) {
						if (data instanceof Array && data.length > 0) {
							var dataItem = graphController.add(
								that._zipPoints(data));
							legendController.add(
								that._getText(dataItem), 
								dataItem.color,				
								function() {
									graphController.remove(dataItem.id);
									return true;
								}
							 );						
						} else {
							noDataInfoController.show();
						}										
					},
					onError: function() {
						alert("Network error");
					}				
				});				
			}															
		});
		
		this.ui.$clearButton.click(function() {
			graphController.clear();
			legendController.clear();
		});
	},
	
	_getText: function(data) {
		var text = [
			this.ui.$year.val(),
			this.ui.$make.val(),
			this.ui.$model.find("option:selected").text(),
			this.ui.$engine.find("option:selected").text()
		].join(' ');
		
		return (text.length > 28)?
			(text.substring(0, 25) + "...")
			: text;			
	},
	
	_isSearchDone: function() {
		var filledIn = true;
		this.ui.$allSelects.each(function(idx, item) {			
			filledIn &= (!!$(item).val());
		});
		return filledIn;
	},
	
	_zipPoints: function(data) {
		
		var dataItem = null;
		var newestDataItem = 0;
		for (var i=0; i<data.length; i++) {
			var di = data[i];
			if (di.value.startYear == di.value.endYear && 
				Number(newestDataItem) < Number(di.value.endYear)) {
				newestDataItem = di.value.endYear;
				dataItem = di;
			}
		}
		if (dataItem == null) {
			dataItem = data[0];
		}

		var xData = dataItem.value.x;
		var yData = dataItem.value.y;
		var points = [];
		for (var i=0; i<xData.length; i++) {
			points.push([xData[i], yData[i]]);
		}
		
		return points;
	},

	_getColor: function() {
		return this.data[this.dataItemId];
	}
}

/*
	@LegendController
*/
function LegendController(context) {
	this.context = context;
}

LegendController.prototype = {
	
	start: function() {
		this.ui = {
			$legend: $("#graphLegend") 
		}
	},
	
	add: function(text, color, onRemove) {
		var $item = $(this._getLegendItem(text, color));
		$item.appendTo(this.ui.$legend.find("ul"))
			.find(".button").click(function() {
				if (onRemove()) {
					$item.remove();
				}
			});
	}, 
	
	clear: function() {
		this.ui.$legend.find("li").remove();
	},
	
	_getLegendItem: function(text, hexColor) {
		return [
			"<li style=\"color:", hexColor, ";\">",
				"<div class=\"textArea\">",
					"<span class=\"text\">", text, "</span>",
				"</div>",
				"<div class=\"accessoryArea\">",
					"<div class=\"button\"></div>",
				"</div>",
			"</li>"].join('');
	}
}

/*
	@GraphController
*/
function GraphController(context) {
	this.context = context;	
	this.clear();
}

GraphController.prototype = {
	start: function() {
		this.ui = {
			$graph: this.context.commonUI.$graph,
			$contentBorder: $("#contentBorder"),
			graph: this.context.commonUI.$graph.get(0),
			scatter: null			
		}
		
		var xaxisLabels = [];
		for (var i = 0; i<=200000; i+= 25000) {
			xaxisLabels.push(i);
		}
		
		this.graphOptions = {
			rgraphProperties: {
				'chart.gutter.bottom': 35,
				'chart.gutter.left': 60,
				'chart.autofit': true,
				'chart.xmax': 200000,
				'chart.xmin': 0,
				'chart.ymax': 100,
				'chart.ymin': 0,
				'chart.background.grid.autofit.numvlines': 10,
				'chart.xscale': true,
				'chart.title.xaxis': "miles",
				'chart.title.yaxis': "price",
				'chart.line': true,
				'chart.line.linewidth': 1.5
			}
		}
		
		this._create();
		
		var that = this;
		$(window).resize(function() {
			that._resize();
		});
		
		// <uniqueId> -> { color: "#AABBCC", x: [1,2], y:[1,2] }
		this.data = {};
	},
	
	// points = [[x1,y1], [x2,y2], ...];
	add: function(points) {				
		var id = false;
		if (this.colorPool.length > 0) {
			id = this.dataItemId;			
			this.data[id] = {
				id: id,
				points: points,
				color: this.colorPool.pop()
			};
			this.dataItemId++;
			this._draw();
		}		
		return this.data[id];
	},
	
	remove: function(dataItemId) {
		var dataItem = this.data[dataItemId];
		if (dataItem) {
			this.colorPool.push(dataItem.color);
			delete this.data[dataItemId];
			this._draw();
		}
	},
	
	clear: function() {
		this.dataItemId = 0;
		this.colorPool = this._getColorPool();
		this.data = {};
		this._draw();
	},
	
	hasPlot: function() {
		return this.numPlots() > 0;
	},
	
	numPlots: function() {
		var numPlots = 0;
		if (typeof(this.data) == "object") {
			for (var key in this.data) {				
				numPlots++;
			}	
		}
		return numPlots
	},
	
	hasMaxPlots: function() {
		return this.colorPool.length == 0;
	},
	
	_create: function() {
		
		var graph = this.ui.graph;
		var scatter = new RGraph.Scatter(this.ui.graph, []);		
		var props = this.graphOptions.rgraphProperties;		
		for (var propName in props) {
			scatter.Set(propName, props[propName]);
		}
		
		var coordinateDisplay = $("#graphCoordinates span");		
		scatter.canvas.onmousemove = function (e) {				
			var obj = e.target.__object__;
			var coordStr = [
				Math.round(obj.getXValue(e), 1), " miles, ",
				"$", Math.round(obj.getYValue(e), 1)].join('');
			coordinateDisplay.text(coordStr);
		}		
		this.scatter = scatter;		
		this._resize();	
		this.ui.$graph.show();			
	},
	
	_resize: function() {
		var $c = this.ui.$contentBorder;		
		this.ui.$graph
			.attr("height", $c.height())
			.attr("width", $c.width());		
		this._draw();					
	},
		
	_draw: function() {		
		if (this.scatter != null) {
			var pointSets = [];
			var pointSetColors = [];			
			var yMax = 1000;			
			for (var dataItemId in this.data) {
				var dataItem = this.data[dataItemId];
				// compute y scale
				yMax = Math.max(dataItem.points[0][1], yMax);
				// color the points
				var colorPoints = dataItem.points.slice(0);
				for (var i=0; i<colorPoints.length; i++) {
					colorPoints[i].push('black');
				}				
				pointSets.push(colorPoints);				
				pointSetColors.push(dataItem.color);
			}			
			this.scatter.data = pointSets;
            this.scatter.Set('chart.line.colors', pointSetColors);
            this.scatter.Set('chart.ymax', 
				Math.round(yMax / 1000, 4) * 1000);			
			this._clearPlot();
			this.scatter.Draw();
		}
	},
	
	_getColorPool: function() {
		var graphColors = [
			"#199889","#fd950d","#36fbbe","#968892",
			"#84f318","#ff3838","#6d5900","#003891",
			"#ab5cf1","#5da5dc", "#fa379c"];
		
		var shuffleArray = function(array) {
			for (var i = array.length - 1; i > 0; i--) {
				var j = Math.floor(Math.random() * (i + 1));
				var temp = array[i];
				array[i] = array[j];
				array[j] = temp;
			}
			return array;
		}
		
		return shuffleArray(graphColors);
	},	
	
	_clearPlot :function() {
		var canvasContext = this.ui.graph.getContext("2d");		
		var canvas = canvasContext.canvas;
		canvasContext.clearRect(0, 0, canvas.width, canvas.height);
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
		this.ui = this.context.commonUI;	
		this.service = this.context.metadataService;
		
		// event handler setup
		var that = this;
		this.ui.$make.change(function() {									
			that.reset(1);
			if (that.ui.$make.val() != "") {					
				that.updateModel();
				that.ui.$model.trigger("updating");
			}
		});		
		this.ui.$model.change(function() {
			that.reset(2);
			if (that.ui.$model.val() != "") {				
				that.updateYear();
				that.ui.$year.trigger("updating");								
			}						
		});	
			
		this.ui.$year.change(function() {
			that.reset(3);
			if (that.ui.$year.val() != "") {				
				that.updateEngine();
				that.ui.$engine.trigger("updating");								
			}
		});		
		
		// lock during select updates
		this.ui.$allSelects.on("updating", function() {
			that._lockControls();
		});
		this.ui.$allSelects.on("updated", function() {
			that._unlockControls();			
		});
		
		// lock during graph searches
		this.ui.$graph.on("updating", function() {
			that._lockControls();
		});		
		this.ui.$graph.on("updated", function() {
			that._unlockControls();			
		});
		
		// updating make only needs to happen once
		this.updateMake();
		that.ui.$make.trigger("updating");
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
				$make.trigger("updated");
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
				$model.trigger("updated");
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
				
				if (yearData && yearData instanceof Array) {
					yearData.sort();
					yearData.reverse();
					$(yearData).each(function(index, item) {
						$('<option/>', {
							text: item,
							value: item
						}).appendTo($year);
					});
				}							
				$year.trigger("updated");
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
				$engine.trigger("updated");
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
	
	_lockControls: function() {
		this.ui.$allSelects.prop("disabled", true);
	},
	
	_unlockControls: function() {
		this.ui.$allSelects.prop("disabled", false);
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
			$container: this.context.commonUI.$container
		}
	},
	
	build: function(options) {		
		
		var iconClass = options.icon || "iconInformation";
		
		options.view = $(this._createInfoBubbleViewHTML())
			.hide()
			.appendTo(this.ui.$container)
			.find(".icon").addClass(iconClass).end();

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
		this.isVisible = false;
		
		var that = this;
		$(window).resize(function() {
			if (that.isVisible) {		
				that.reposition();			
			}
		});
		this.currentShowId = 0;
	},

	show: function(autohide) {				
		var that = this;
		this.isVisible = true;
		this._showEffect();
		autohide = typeof(autohide === "undefined")? 
			true 
			: autohide;			
		if (autohide) {
			(function(id) {
				setTimeout(function() {
					if (that.currentShowId == id) {
						that.hide();
					}
				}, 6000);
			})(++this.currentShowId);			
		}
	},

	hide: function() {
		this.isVisible = false;
		this._hideEffect();
	},

	reposition: function() {
		var that = this;		
		that.ui.$view.show().position({
			my: that.options.my,
			at: that.options.at,
			of: that.options.of
		});
	},

	_showEffect: function() {
		var that = this;
		that.reposition();
		that.ui.$view.hide();
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
