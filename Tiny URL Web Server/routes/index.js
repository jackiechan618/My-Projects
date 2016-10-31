var url = require('url');
var TinyUrl = require('./../model/TinyUrl');



module.exports = function(app) {
    app.get('/', function(req, res) {
        res.render('index', {
            title: 'index',
            layout:'layout'
        });
    });
    
    app.post('/', function(req, res) {
        var longUrl = req.body['longUrl'];
            
        TinyUrl.storeLongUrl(longUrl, function(err, shortUrl){
            if(err != null){
                console.log('Error:'+ err)
                return res.redirect('/');
            } else {
                globalLongUrl = longUrl;
                req.flash('success', 'The tiny url has been generated successfully');
                return res.redirect('/index2');
            }
        });
    });
    
    app.get('/index2', function(req, res) { 
        res.render('index2', {
            title: 'index2',
            layout:'layout2'
        });
    });
    
    app.get('/index2/shortUrl', function(req, res) {         
        TinyUrl.getShortUrl(globalLongUrl, function(shortUrl){
            if(shortUrl.length > 0){
                res.writeHead(200, {'Content-Type': 'text/plain'})
                res.write(shortUrl)
                res.end()
            }  
        })
    });
    
    app.post('/index2', function(req, res) {
        return res.redirect('/')
    });
    
    app.get('/:who', function(req, res) {
        var pathname = url.parse(req.url).pathname;
        var shortUrl = pathname.split("/")
        
        TinyUrl.getLongUrl(shortUrl[1], function(err, longUrl){
            if(err != null){
                console.log('app.get who: error: ' + err.message);
                return res.redirect('/');
            }
            
            if(longUrl != null){
                longUrl = "http://" + longUrl;
                return res.redirect(longUrl)
            } else {
                req.flash('error', 'The tiny url is not in database, please add it first');
                return res.redirect('/')
            }
        })
    });
}
