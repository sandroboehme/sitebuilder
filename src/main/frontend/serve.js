var express = require("express");
var jspm = require('jspm');

jspm.setPackagePath('.');

var running = 'Sitebuilder server for installing packages running.';

var app = express();

var bodyParser = require('body-parser');
var multer = require('multer'); // for parsing the body
var upload = multer(); // for parsing multipart/form-data of the body

app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

app.get('/', function(req, res){ 
	res.send(running); 
});
 
 /*
 e.g.:
 POST /install 
 	name=lightbox2 
 	target=github:lokesh/lightbox2@^2.8.1
 	options={"override":{  "format": "global", "registry": "jspm","main": "dist/js/lightbox.js",  "dependencies": {    "jquery": "*","css":"*"  }, "shim": {    "dist/js/lightbox": {      "deps": [        "jquery",        "../css/lightbox.css!"],      "exports": "$"    }}}}
 */
app.post('/install', function(req, res){  
 	console.log("Installing a package with the following parameters: "+JSON.stringify(req.body));
 
 	var installSuccessful = function(value){
 		var successMsg = "Package installed.";
		res.send(successMsg); 
 		console.log(successMsg);
 	};
 	var installUnsuccessful = function(reason){
 		var error = 'Installation unsuccessful: '+reason;
		res.status(500).send({ error: error });
 		console.log(error); 
 	};
 	
    var options = JSON.parse(req.body["options"]);

        // see http://jspm.io/docs/api.html#installname-target--options--promise    
 	// details to the available options:
 	// https://github.com/jspm/jspm-cli/blob/master/lib/install.js#L71
 	jspm.install(req.body["name"], req.body["target"], options).then(installSuccessful, installUnsuccessful);
 
}); 
 
app.post('/reinstallAll', function(req, res){  
 	console.log("Reinstalling all packages from the package.json: ");
 
 	var installSuccessful = function(value){
 		var successMsg = "All packages succuessfully installed.";
		res.send(successMsg); 
 		console.log(successMsg);
 	};
 	var installUnsuccessful = function(reason){
 		var error = 'Installation unsuccessful: '+reason;
		res.status(500).send({ error: error });
 		console.log(error); 
 	};
 
 	// details to the available options:
 	// https://github.com/jspm/jspm-cli/blob/master/lib/install.js#L71
 	jspm.install(true).then(installSuccessful, installUnsuccessful);
 
}); 

app.listen(7777);

console.log(running);
