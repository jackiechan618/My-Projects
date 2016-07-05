var passport = require('passport');
var GoogleStrategy = require('passport-google-oauth').OAuth2Strategy;
var os = require('os');

var GOOGLE_CLIENT_ID = "XXX";
var GOOGLE_CLIENT_SECRET = "XXX";


global.serverIpaddr = "";
global.googleUser = "";


function SSO_google(studyPlanForm) {
    this.type = '';
    this.userid = '';
    this.name = '';
    this.email = '';
    this.avatar = '';
};

module.exports = SSO_google;

SSO_google.passportConfig = function passportConfig(){
    var os_flag = 2;
    
    var EC2CallbackURL = "http://studyplancreater.tk:3001/auth/google/callback";
    var localhostCallbackURL = "http://localhost:3001/auth/google/callback";
    var serverCallbackURL = localhostCallbackURL;
    
    if(os_flag == 3){
        serverCallbackURL = EC2CallbackURL;
    }
    
    passport.use(new GoogleStrategy({
        clientID: GOOGLE_CLIENT_ID,
        clientSecret: GOOGLE_CLIENT_SECRET,
        callbackURL: serverCallbackURL
        }, function(accessToken, refreshToken, profile, done) {            
            this.type = 'google';
            this.userid = profile.id;
            this.name = profile.displayName;
            this.email = profile.emails[0].value;
            this.avatar = profile._json.picture;
        
            global.googleUser = this.email;
   
            return done(null, profile);
        }
    ));

    passport.serializeUser(function (user, done) {
        done(null, user);
    });

    passport.deserializeUser(function (user, done) {
        done(null, user);
    });    
}

//exports.passportConfig = passportConfig;