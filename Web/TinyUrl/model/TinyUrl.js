var getUrl = require('./getUrl');
var dbHandler = require('./dbHandler');


global.globalLongUrl = "";

function TinyUrl(longUrl) {
    this.longUrl = longUrl
};

module.exports = TinyUrl;


TinyUrl.storeLongUrl = function storeLongUrl(myLongUrl, callback) { 
    dbHandler.getShortUrl(myLongUrl, function(err, docs){
        if(err != null){
            console.log('TinyUrl.storeLongUrl Error:'+ err);
            return callback(err, null)
        }
        
        if(docs.length > 0){
            dbHandler.getShortUrl(myLongUrl, function(err, docs){
                if(err != null){
                    console.log('Error:'+ err);
                    return callback(err, null)
                }
                
                return callback(err, docs[0].shortUrl)
            });
        } else {
            getUrl.getShortUrl(function(shortUrl){
                
                console.log("shortUrl = " + shortUrl);
                
                if(shortUrl.length > 0){
                    dbHandler.save(shortUrl, myLongUrl);
                }
                return callback(null, shortUrl)
            })
        }
    })    
}


TinyUrl.getLongUrl = function getLongUrl(myShortUrl, callback){  
    dbHandler.getLongUrl(myShortUrl, function(err, docs){
        if(err){
            console.log('TinyUrl.getLongUrl error: ' + err.message);
            return callback(err, null)
        }
        
        if(docs.length > 0){
            var longUrl = docs[0].longUrl
            return callback(null, longUrl);    
        } else {
            return callback(null, null);
        }      
    })
}


TinyUrl.getShortUrl = function getShortUrl(myLongUrl, callback){
    dbHandler.getShortUrl(myLongUrl, function(err, docs){
        if(err){
            console.log('TinyUrl.getShortUrl error: ' + err.message);
            return callback(err, null)
        }
        
        var shortUrl = docs[0].shortUrl
        return callback(shortUrl);
    })
}