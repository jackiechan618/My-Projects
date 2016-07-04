var mongodb = require('./../db');

function Feedback(firstName, lastName, areaCode, Tel, Email, contactMethod, userFeedback) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.areaCode = areaCode;
    this.Tel = Tel;
    this.Email = Email;
    this.contactMethod = contactMethod;
    this.userFeedback = userFeedback;
};

module.exports = Feedback;

Feedback.addFeedback = function addFeedback(firstName, lastName, areaCode, Tel, Email, contactMethod, userFeedback) { 
    var currentTime = new Date();
    var time = '<' + currentTime.getMonth() + '/' + currentTime.getDate() + '/' + currentTime.getFullYear() + ', ' + currentTime.getHours() + '.' + currentTime.getMinutes() + '.' + currentTime.getSeconds() + '>' 
    
    var feedbackString = time + ':' + firstName + ':' + lastName + ':' + areaCode + ':' + Tel + ':' + Email + ':' + contactMethod + ':' + userFeedback;
    console.log("user feedback: " + feedbackString);       
    
    var feedbackInfo = {
        userFeedback: feedbackString
    };
    
    mongodb.open(function(err, db) {
        if (err) {
            console.log('error: ' + err);
            return callback(err);
        }
        
        db.collection('feedbacks', function(err, collection) {
            if (err) {
                mongodb.close();
                return;
            }

            collection.ensureIndex('userFeedback',{w:1});
            collection.insert(feedbackInfo, {safe: true}, function(err, feedbackInfo) {
                mongodb.close();
            });
        });
    });
};

Feedback.get = function get(callback) {
    mongodb.open(function(err, db) {
        if (err) {
            console.log('error: ' + err);
            return callback(err);
        }

        db.collection('feedbacks', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }

            collection.find().toArray(function(err, feedbackInfo) {
                mongodb.close();
                if (err) {
                    callback(err, null);
                }
                callback(null, feedbackInfo);
            });
        });
    });
};