/*

grpVC
 * update
 * show
 * hide
 * onSelection
 * onSelectionCancel


grpView
 * showItems


*/


function GroupedAutoCompleteController(options) {
	
	if (this._isValidField(options._field)) {
		throw new Error("Invalid field argument, must be single jQuery object");
	}
	this._view = options.view;
	this._field = options.field;
	this._dataSource = options.dataSource;
	this._re = /^\s*(.*?)\s*$/g;

	this._initData();
	this._initAutoComplete();
	this._initEventHandlers();
}

GroupedAutoCompleteController.prototype = {
	
	_initData: function() {
		this._data = this._dataSource.getData();
	},

	_initAutoComplete: function() {
		
		this._view.hide();
		
		var autoCompleteId = 0;
		var autoCompleteDict = {};

		var $topLevel = this._view.find(".outerGroupList > li > .searchItem");
		for (var i=0; i<$topLevel.length; i++) {
			var $itm = $topLevel.eq(i);
			var txt = $itm.text();
			var subItems = $itm.closest("li")
				.find(".innerGroupList li.searchItem");
			
			if (typeof(autoCompleteDict[txt]) !== "undefined") {
				throw new Error("Duplicate item detected in autocomplete list");
			}

			autoCompleteDict[txt] = {
				element: $itm,
				subItems: subItems
			};

			this._createExpanderRow($itm);
		}

		this._autoCompleteDict = autoCompleteDict;
	},

	_createExpanderRow: function($itm) {
		
		var expandable = "expandableItem";
		var expanded = "expandedItem";

		$itm.closest("li").addClass(expandable);
		$itm.click(function() {
			var li = $(this).closest("li");
			if (li.hasClass(expandable)) {
				li.removeClass(expandable);
				li.addClass(expanded);
			}
			else { 
				li.removeClass(expanded);
				li.addClass(expandable);
			}
		});
	},

	_isValidField: function(field) {
		return field instanceof jQuery 
			&& field.size() == 1 
			&& typeof(field[0].tagName) !== "undefined" 
			&& field[0].tagName === "INPUT";
	},

	_initEventHandlers: function() {
		var that = this;
		var fld = this._field;
		var vw = this._view;
		var eventName = ($.browser.msie)? 
			"propertychange"
			: "input";

		var that = this;
		var clickCaught = false;
		$(fld).on(eventName, function() {
			that._onFieldChange();
		}); 
		$(fld).on("focus", function() { 
			that._onFieldFocus();
		});
		$(fld).on("blur", function() {
			clickCaught = false;
			setTimeout(function() {
				if (clickCaught === false) {
					that._onFieldBlur();
				}
			}, 500);
		});
		$(vw).on("click", function() {
			clickCaught = true;
			$(fld).focus();
		});
	},

	_onFieldChange: function() {
		var fld = this._field;
		var text = fld.text();

		var selectionState = this._getSelectionState(text);
		this._applySelectionState(selectionState);
	},

	_onFieldFocus: function() {
		var fld = this._field;
		var vw = this._view;

		vw.show().position({
			my: "left top",
			at: "left bottom",
			of: fld
		});
	},

	_onFieldBlur: function() {
		var fld = this._field;
		var vw = this._view;
		vw.hide();
	},

	_search: function(str) {
		var match = this._re.exec(str);
		var results = [];
		if (match != null && match.length == 2) {
			var cleanStr = match[1];
			for (var i=0; i<this._data.length; i++) {
				var itm = this._data[i];
				var txt = itm.getText();
				if (txt.indexOf(cleanStr) >= 0) {
					results.push(itm);
				}
			}
		}
		return results;
	},

	_getData: function() {
		return this._dataSource.getData();
	},

	_getState: function() {

		if (this._state === null) {
			this._state = [
				{
					active: false,
					onActivate: function() {

					},
					onDeactivate: function() {

					},
					fieldName: "make",
					selector: "",
					className: ""
				},
				{
					active: false,
					onActivate: function() {

					},
					onDeactivate: function() {

					},
					fieldName: "model",
					selector: "",
					className: ""
				}
			];
		}

		return this._state;
	},

	_getSelectionState: function(text) {
		
		var state = this._getState();
		
		if (typeof(text) !== "string") {
			return state;
		}

		debugger;

		var data = this._getData();
		var parts = text.split(",");
		var carMake = this._re.exec(parts[0])[1];
		var carModel = (parts.length > 1)? 
			this._re.exec(parts[1])[1]
			: null;
		
		if (data["make"].find(carMake) > 0) {
			state[0].active = true;	
		}
		if (carModel !== null && data["model"].find(carModel)) {
			state[1].active = true;
		}

		return state;
	},

	_applySelectionState: function(selectionState) {
		for (var i=0; i<selectionState.length; i++) {
			var stateItem = selectionState[i];
			var selector = stateItem.selector;
			var className = stateItem.className;
			if (stateItem.valid) {
				$(selector).addClass(className);
			}
			else {
				$(selector).removeClass(className);
			}
		}
	}
}

function MockDataSource() {
	this.data = [];
	var that = this; 
	$(".groupedAutoComplete .searchItem").each(function(idx, item) {
		that.data.push(new MockDataItem($(item).text(), item));
	});
}

MockDataSource.prototype = {
	getData: function() {
		return this.data.slice(0);
	}
}

function MockDataItem(text, elem) {
	this.text = text;
	this.elem = elem;
}

MockDataItem.prototype = {
	getText: function () {return this.text; },
	getElement: function() {return this.elem; }
}

/*
	Entry Point
*/
$(function() {
	var view = $(".groupedAutoComplete");
	var field = $("input[name='autocompleteField']");
	var dataSource = new MockDataSource();
	var acCtrl = new GroupedAutoCompleteController({
		view: view,
		field: field,
		dataSource: dataSource
	});
});
