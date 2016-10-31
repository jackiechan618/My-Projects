var dbHandler = require('./dbHandler');


function getUrl(longUrl) {
    this.longUrl = longUrl;
};

module.exports = getUrl;

getUrl.getShortUrl = function getShortUrl(callback) {    
    dbHandler.updateParameter(function(err, globalId){
        if(err){
            console.log('error: ' + err.message);
            return callback(err, null)
        }
        
        var chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var shortUrl=""
        var localId = globalId
    
        while(localId > 1){
            shortUrl = chars.charAt(localId % 62) + shortUrl;
            localId = localId / 62;
        }
    
        while(shortUrl.length < 6){
            shortUrl = "0" + shortUrl;
        }
        
        return callback(shortUrl);
    })
}