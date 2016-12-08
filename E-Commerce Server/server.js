var express = require('express');
var app = express();

app.get('/', function(req, res) {
	var name = "Jackie";
	res.json("My name is " + name);
});

app.get('/catname', function(req, res){
	res.json("batman");
});

app.listen(3000, function(err) {
	if(err) {
		throw err;
	}
	console.log("Server is running on port 3000");
});



var morgan = require('morgan');
app.use(morgan('dev'));


app.get('/', function(req, res) {
	var name = "Jackie";
	res.json("My name is " + name);
});

app.get('/catname', function(req, res){
	res.json("batman");
});








