var mongodb = require('./../db');

function SystemLog(SystemLogInfo) {
    this.SystemLogInfo = SystemLogInfo;
};

module.exports = SystemLog;

SystemLog.addSystemLog = function addSystemLog(username, action) { 
    var currentTime = new Date();
    var month = (currentTime.getMonth() - '0' + 1);
    var time = '<' + month + '/' + currentTime.getDate() + '/' + currentTime.getFullYear() + ', ' + currentTime.getHours() + ':' + currentTime.getMinutes() + ':' + currentTime.getSeconds() + '>' 
    
    var SystemLogInfo = time + ' user: ' + username + ', action: ' + action;
    console.log("system log: " + SystemLogInfo);       
    
    var sysLog = {
        userSystemLog: SystemLogInfo
    };
    
    mongodb.open(function(err, db) {
        if (err) {
            console.log('error: ' + err);
            return;
        }
        
        db.collection('systemLogs', function(err, collection) {
            if (err) {
                mongodb.close();
                return;
            }

            collection.ensureIndex('userSystemLog',{w:1});
            collection.insert(sysLog, {safe: true}, function(err, SystemLogInfo) {
                mongodb.close();
            });
        });
    });
};

SystemLog.get = function get(callback) {
    mongodb.open(function(err, db) {
        if (err) {
            console.log('error: ' + err);
            return callback(err);
        }

        db.collection('systemLogs', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }

            collection.find().toArray(function(err, systemLog) {
                mongodb.close();
                if (err) {
                    callback(err, null);
                }
                callback(null, systemLog);
            });
        });
    });
};