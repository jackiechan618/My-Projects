var mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs');
var Schema = mongoose.Schema;

/* The user Schema attributes,  characteristics/ fields */
var UserSchema = new Schema({
	email: {type: String, unique: true, lowcast: true},
	password: String,

	profile: {
		name: {type: String, default: ''},
		picture: {type: String, default: ''}
	},

	address: String,

	history: [{
		date: Date,
		paid: {type: Number, default: '0'}
	}]
});