var makeSelector = document.getElementById("makeSelector")
var makeOptions = makeSelector.children
var modelSelector = document.getElementById("modelSelector")
var yearSelector = document.getElementById("yearSelector")
var delay = 2000

var selectedMakeIdx = 0
var selectedModelIdx = 0
var updateModel = true
var updateMake = true

var searchData = {}
var currentMakeData = null
var currentModelData = null

function cleanName(str) {
    return str.toLowerCase().replace(" ", "-")
}

function doNext() {
    if (updateMake) {        
        selectedMakeIdx++
        var curMakeOption = makeOptions[selectedMakeIdx]
        var curMakeName = cleanName(curMakeOption.value)
        currentMakeData = {}
        searchData[curMakeName] = currentMakeData
        updateMake = false
        selectedModelIdx = 0
        updateModel = true
        makeSelector.selectedIndex = selectedMakeIdx
    }
    else if (updateModel) {
        var modelOptions = modelSelector.options
        for (var i=0; i<modelOptions.length; i++) {
            var o = modelOptions[i]
            var curModelName = 
        }
    
        selectedModelIdx++
        modelSelector.selectedIndex = selectedModelIdx
    }
    //read all years
    else {    
        var numYears = yearSelector.children.length
        for (var i=0; i<numYears.length; i++) {
        
        }
        if (selectedModelIdx >= (numModels-1)) {
            selectedModelIdx = 0
            updateModel = true
        } else {
            updateMake = true
        }
    }
}

setInterval(doNext, delay)
