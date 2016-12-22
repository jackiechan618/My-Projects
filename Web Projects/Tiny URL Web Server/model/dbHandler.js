var mongodb = require('./../db');


function dbHandler() {
    this.longUrl = '';
    this.shortUrl = '';
};

module.exports = dbHandler;


dbHandler.save = function save(shortUrl, longUrl){
    var urlInfo = {
        shortUrl: shortUrl,
        longUrl: longUrl
    };
    
    mongodb.open(function(err, db) {
        if (err) {
            console.log('error: ' + err);
            return;
        }
        
        db.collection('myTinyUrl', function(err, collection) {
            if (err) {
                mongodb.close();
                return;
            }

            collection.ensureIndex('shortUrl',{w:1});
            collection.insert(urlInfo, {safe: true}, function(err, urlInfo) {
                mongodb.close();
            });
        });
    });
}

dbHandler.getLongUrl = function getLongUrl(myShortUrl, callback){
    mongodb.open(function(err, db) {
        if (err) {           
            console.log('dbHandler.getLongUrl error: ' + err.message);
            return callback(err);
        }
        
        db.collection('myTinyUrl', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }
            
            collection.find({'shortUrl': myShortUrl}).sort({time: -1}).toArray(function(err, docs) {
                mongodb.close();
                if (err) {
                    callback(err, null);
                }
                
                callback(null, docs);
            });
        });
    });
}

dbHandler.getShortUrl = function getShortUrl(myLongUrl, callback){
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }
        
        db.collection('myTinyUrl', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }

            collection.find({"longUrl": myLongUrl}).sort({time: -1}).toArray(function(err, docs) {
                mongodb.close();
                if (err) {
                    callback(err, null);
                }
                
                callback(null, docs);
            });
        });
    });
}

dbHandler.updateParameter = function updateParameter(callback){
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }
        
        db.collection('Parameter', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }

            collection.find({"name": "globalId"}).toArray(function(err, result) {
                if(result.length > 0){
                    var globalId = result[0].value;     
                    globalId++;
                    
                    var updateData = {$set: { "value" : globalId}};
                    
                    collection.update({"name": "globalId"}, updateData, function(err, result){
                        if(err) {
                            console.log('Error:'+ err);
                            err = 'Error';
                        }
                        mongodb.close();
                        callback(err, globalId);
                    });
                } else {
                    var globalIdInfo = {
                        name: "globalId",
                        value: 1
                    }
                    
                    collection.ensureIndex('name',{w:1});
                    collection.insert(globalIdInfo, {safe: true}, function(err, globalIdInfo) {
                        mongodb.close();
                        callback(err, globalIdInfo);
                    });
                }
            });
        });
    });
}
