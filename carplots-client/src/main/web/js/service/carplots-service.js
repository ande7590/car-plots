var ServiceDefinitions = {
	carplots: {
		name: "carplots", 
		url:"/db/carplots",
		methods: [
		{
			name: "getPlots",
			methodType: "GET",
			dataType: "json",
			location: "plots/",
			args: [
				":MakeModelID/:Year",
				":MakeModelID/:Year/:StartYear/:EndYear/:CarEngineID"]
		}]
	},
	metadata: {
		name: "metadata", 		
		url:"/db/metadata",
		methods : [
		{
			name: "getMakeModels",
			methodType: "GET",
			dataType: "json",
			location: "makeModels/",
			args: [
				"", 
				":make"
			]	
		},
		{
			name: "getMakes",
			methodType: "GET",
			dataType: "json",
			location: "makes/",
			args: [
				""
			]	
		},
		{
			name: "getEngines",
			methodType: "GET",
			dataType: "json",
			location: "engines/",
			args: [
				":mmid",
				":mmid/:yr"
			]	
		},
		{
			name: "getYears",
			methodType: "GET",
			dataType: "json",
			location: "years/",
			args: [
				":mmid"
			]	
		}]
	}
};
