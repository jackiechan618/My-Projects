var User = require('./../model/User');
var Post = require('./../model/Post');
var StudyPlanForm = require('./../model/StudyPlanForm');
var EmailSender = require('./../model/EmailSender');
var PdfCreater = require('./../model/PdfCreater');
var SystemLogRecorder = require('./../model/SystemLog');
var Administrator = require('./../model/Administrator');
var Feedback = require('./../model/Feedback');
var fs = require('fs');

var passport = require('passport');

var SSO_google = require('../model/SSO_google');



module.exports = function(app) {
    app.get('/', function(req, res) {
        Post.get(null, function(err, posts) {
            if (err) {
                posts = [];
            }
            res.render('index', {
                title: 'Home',
                posts: posts,
                layout:'layout'
            });
        });
    });
    
    app.get('/about', function(req, res) { 
        res.render('about', {
            title: 'About Us',
            layout:'layout'
        });
    });
    
    app.get('/contact', function(req, res) {
        res.render('contact', {
            title: 'Contact Us',
            layout:'layout'
        });
    });
    
    app.post('/contact', function(req, res) {
        var firstName = req.body['firstName'];
        var lastName = req.body['lastName'];
        var areaCode = req.body['areaCode'];
        var telnum = req.body['telnum'];
        var Email = req.body['Email'];
        var contactMethod = req.body['contactMethod'];
        var feedback = req.body['feedback'];
        
        if(firstName.length == 0 || lastName.length == 0){
            req.flash('error', 'Your name should not be empty');
            return res.redirect('/contact');
        }
        
        if(areaCode.length == 0){
            req.flash('error', 'Your areaCode should not be empty');
            return res.redirect('/contact');
        }
        
        if(telnum.length == 0){
            req.flash('error', 'Your telnum should not be empty');
            return res.redirect('/contact');
        }
        
        if(Email.length == 0){
            req.flash('error', 'Your Email should not be empty');
            return res.redirect('/contact');
        }
        
        if(feedback.length == 0){
            req.flash('error', 'Your feedback should not be empty');
            return res.redirect('/contact');
        }
        
        Feedback.addFeedback(firstName, lastName, areaCode, telnum, Email, contactMethod, feedback);
        EmailSender.sendFeedback(firstName, lastName, areaCode, telnum, Email, contactMethod, feedback);
        req.flash('success', 'Thanks for your feedback');
        return res.redirect('/contact');
    });
    
    app.get('/formPage1', function(req, res) {
        res.render('formPage1', {
            title: 'Study Plan',
            layout:'layout2'
        });
    });
    
    app.get('/formPage1/userInfo', function(req, res) {
        StudyPlanForm.get(req.session.user.name, function(err, studyPlanForm) {
            if(err){
                req.flash('error', err);
                console.log('error: ' + err.message);
                return res.redirect('/formPage1');
            }

            res.writeHead(200, {'Content-Type': 'text/plain'})
            
            if(studyPlanForm.length == 1){
                var firstName = studyPlanForm[0].firstName;
                var lastName = studyPlanForm[0].lastName;
                var sId = studyPlanForm[0].sId;
                var Email = studyPlanForm[0].Email;
                var Dept = studyPlanForm[0].Dept;
                var Major = studyPlanForm[0].Major;
                var Concentration = studyPlanForm[0].Concentration;
                var Degree = studyPlanForm[0].Degree;
                var gYear = studyPlanForm[0].gYear;
                var gTerm = studyPlanForm[0].gTerm;
                var Opt = studyPlanForm[0].Opt;
                
                res.write(firstName 
                  + ':' + lastName 
                  + ':' + sId
                  + ':' + Email
                  + ':' + Dept
                  + ':' + Major
                  + ':' + Concentration
                  + ':' + Degree
                  + ':' + gYear
                  + ':' + gTerm
                  + ':' + Opt
                 ); 
            } else {
                res.write(':');
            }
            
            res.end()
        });
    });
    
      
    app.post('/formPage1', checkLogin);
    app.post('/formPage1', function(req, res) {
        var myStudyPlanForm = {
            username: req.session.user.name,
            firstName: req.body['firstName'],
            lastName: req.body['lastName'],
            sId: req.body['sId'],
            Email: req.body['Email'],
            Dept: req.body['Dept'],
            Major: req.body['Major'],
            Concentration: req.body['Concentration'],
            Degree: req.body['Degree'],
            gYear: req.body['gYear'],
            gTerm: req.body['gTerm'],
            Opt: req.body['Opt'],
            courseList: []
        };
        
        var studyPlanForm1 = new StudyPlanForm(myStudyPlanForm);
        
        studyPlanForm1.save(function(err){
            if (err) {
                req.flash('error', err);
                return res.redirect('/formPage1');
            }
            req.flash('success', 'save personal information');
            res.redirect('/formPage2');
        });
    });
    
    app.get('/formPage2', function(req, res) {
        res.render('formPage2', {
            title: 'Study Plan',
            layout:'layout2'
        });
    });
    
    app.get('/formPage2/userInfo', function(req, res) {
        StudyPlanForm.get(req.session.user.name, function(err, studyPlanForm) {
            if(err){
                req.flash('error', err);
                console.log('error: ' + err.message);
                return res.redirect('/formPage2');
            }

            res.writeHead(200, {'Content-Type': 'text/plain'})
            
            if(studyPlanForm.length == 1){
                var firstName = studyPlanForm[0].firstName;
                var lastName = studyPlanForm[0].lastName;
                var sId = studyPlanForm[0].sId;
                var Email = studyPlanForm[0].Email;
                var Dept = studyPlanForm[0].Dept;
                var Major = studyPlanForm[0].Major;
                var Concentration = studyPlanForm[0].Concentration;
                var Degree = studyPlanForm[0].Degree;
                var gYear = studyPlanForm[0].gYear;
                var gTerm = studyPlanForm[0].gTerm;
                var Opt = studyPlanForm[0].Opt;
                var courseList = studyPlanForm[0].courseList;
                
                var courseListStr = courseList.join(";");
                
                res.write(firstName 
                  + ':' + lastName 
                  + ':' + sId
                  + ':' + Email
                  + ':' + Dept
                  + ':' + Major
                  + ':' + Concentration
                  + ':' + Degree
                  + ':' + gYear
                  + ':' + gTerm
                  + ':' + Opt
                  + ':' + courseListStr
                 ); 
            } else {
                res.write(':');
            }
            
            res.end()
        });
    });
    
    app.post('/formPage2', checkLogin);
    app.post('/formPage2', function(req, res) {        
        var username = req.session.user.name;
        var courseList = [];
        
        var os_flag = 2;
    
        var filePath_mac = '/Users/Jackie/Documents/workspace\(full\ stack\)/Study\ Plan\ Creater\(new\)/public/userDocument/' + req.session.user.name + '_study_plan.pdf';
        
        var filePath_ubuntu = '/home/jackie/projects/study-plan-creater/public/userDocument/' + req.session.user.name + '_study_plan.pdf';
        
        var filePath_ubuntu_ec2 = '/home/ubuntu/project/study-plan-creater/public/userDocument/' + req.session.user.name + '_study_plan.pdf';
        
        var filePath = filePath_mac;
    
        if(os_flag == 2){
            filePath = filePath_ubuntu;
        }
        if(os_flag == 3){
            filePath = filePath_ubuntu_ec2;
        }
        
        for(var i = 0; i < req.body.length; i++){
            courseList.push(req.body[i]);
        }

        StudyPlanForm.addCourseList(username, courseList, function(err, returnCourseInfo){
            if(err){
                req.flash('error', err);
                console.log('error: ' + err.message);
                return res.redirect('/formPage2');
            }
            
            req.flash('success', 'save course list');
        });
        
        
        var oneSecond = 200 * 1; 
        
        setTimeout(function() {
            StudyPlanForm.get(req.session.user.name, function(err, studyPlanForm) {
                
            if(err){
                req.flash('error', err);
                console.log('error: ' + err.message);
                return res.redirect('/formPage2');
            }
            
            if(studyPlanForm.length > 0) {
                var username = req.session.user.name;
                var firstName = studyPlanForm[0].firstName;
                var lastName = studyPlanForm[0].lastName;
                var sId = studyPlanForm[0].sId;
                var Email = studyPlanForm[0].Email;
                var Dept = studyPlanForm[0].Dept;
                var Major = studyPlanForm[0].Major;
                var Concentration = studyPlanForm[0].Concentration;
                var Degree = studyPlanForm[0].Degree;
                var gYear = studyPlanForm[0].gYear;
                var gTerm = studyPlanForm[0].gTerm;
                var Opt = studyPlanForm[0].Opt;
            
                PdfCreater.createPdf(filePath, username, firstName, lastName, sId, Email, Dept, Major, Concentration, Degree, gYear, gTerm, Opt, courseList);
            } else {
                req.flash('error', 'Please input your information to create the study plan firstly!');
                return res.redirect('/formPage2');
            }
        });
        }, oneSecond);  
    });
    
    
    app.get('/formPage3', function(req, res) {
        res.render('formPage3', {
            title: 'Study Plan',
            layout:'layout2'
        });
    });
    
    app.post('/formPage3', checkLogin);
    app.post('/formPage3', function(req, res) {
        var os_flag = 2;
    
        var filePath_mac = '/Users/Jackie/Documents/workspace\(full\ stack\)/Study\ Plan\ Creater\(new\)/public/userDocument/' + req.session.user.name + '_study_plan.pdf';
        
        var filePath_ubuntu = '/home/jackie/projects/study-plan-creater/public/userDocument/' + req.session.user.name + '_study_plan.pdf';
        
        var filePath_ubuntu_ec2 = '/home/ubuntu/project/study-plan-creater/public/userDocument/' + req.session.user.name + '_study_plan.pdf';
        
        var filePath = filePath_mac;
    
        if(os_flag == 2){
            filePath = filePath_ubuntu;
        }
        if(os_flag == 3){
            filePath = filePath_ubuntu_ec2;
        }
        
        var DestinationEmail = req.body["DestinationEmail"];
        if(DestinationEmail.length == 0){
            req.flash('error', 'Please input your email address before sending your study plan!');
            return res.redirect('/formPage3');
        }
        
        EmailSender.autoSendEmail(filePath, req.session.user.name, DestinationEmail);
        var successMessage = 'Your study plan has been sent to ' + DestinationEmail + ', thanks for using Study Plan Creater!';              
        req.flash('success', successMessage);     
        return res.redirect('/formPage3');
    });
    
    app.post('/reg', checkNotLogin);
    app.get("/reg",function(req, res) {
        res.render('reg', {
            title: 'Register',
            layout:'layout'
        });
    });

    app.post('/reg', checkNotLogin);
    app.post("/reg",function(req, res) {
        if(req.body.username.length == 0){
            req.flash('error', 'Username should not be empty');
            return res.redirect('/reg');
        }
        
        if(req.body.password.length == 0){
            req.flash('error', 'Password should not be empty');
            return res.redirect('/reg');
        }
        
        var username = req.body['username'];
        var password = req.body['password'];
        var passwordRepeat = req.body['password_repeat'];
        
        if(username.length < 5 || username.length > 12){
            req.flash('error', 'The length of username should between 5 and 12');
            return res.redirect('/reg');
        }
        if(password.length < 5 || password.length > 12){
            req.flash('error', 'The length of password should between 5 and 12');
            return res.redirect('/reg');
        }
        if(password !== passwordRepeat){
            req.flash('error', 'Password is not the same as Password-Repeat');
            return res.redirect('/reg');
        }
        
        var newUser = new User({
            name:username,
            password:password
        });
        User.get(newUser.name,function(err,user){
            if (user){
                console.log('register error: username <' + user.name + "> is already exist");
                err = 'Username: ' + user.name + ' is already exist, please change a new one, thanks';
            }
            if (err) {
                req.flash('error', err);
                console.log('error1: ' + err.message);
                return res.redirect('/reg');
            }
            newUser.save(function(err){
                if(err){
                    req.flash('error', err);
                    console.log('error: ' + err.message);
                    return res.redirect('/reg');
                }
                
                req.session.user = newUser;
                
                var action = 'register';
                SystemLogRecorder.addSystemLog(req.session.user.name, action);
                
                req.flash('success','Register successfully');
                res.redirect('/formPage1');
            });
        });
    });
    
    app.get('/userPasswordModify', checkLogin);
    app.get('/userPasswordModify', function(req, res) {
        res.render('userPasswordModify', {
            title: 'User Password Modification',
            layout:'layout2'
        });
    });
    
    app.post('/userPasswordModify', checkLogin);
    app.post("/userPasswordModify",function(req, res) {
        var oldPassword = req.body['oldPassword'];
        var newPassword = req.body['newPassword'];
        var newPassword_repeat = req.body['newPassword_repeat'];
        
        if(newPassword.length < 5 || newPassword.length > 12){
            req.flash('error', 'The length of new password should between 5 and 12');
            return res.redirect('/userPasswordModify');
        }
            
        if(newPassword_repeat.length < 5 || newPassword_repeat.length > 12){
            req.flash('error', 'The length of new password_repeat should between 5 and 12');
            return res.redirect('/userPasswordModify');
        }
            
        if(newPassword !== newPassword_repeat){
            req.flash('error', 'New Password is not the same as new password-repeat');
            return res.redirect('/userPasswordModify');
        }
            
        if(oldPassword == newPassword){
            req.flash('error', 'New Password is the same as old password');
            return res.redirect('/userPasswordModify');
        }
        
        User.get(req.session.user.name, function(err, user) {
            if (user.password != oldPassword) {
                req.flash('error', 'Wrong Old Password');
                return res.redirect('/userPasswordModify');
            } 
          
            User.modifyPassword(req.session.user.name, newPassword, function(err) {
                if(err){
                    req.flash('error', 'Password modification error');
                    console.log('error: ' + err.message);
                    return res.redirect('/userPasswordModify');
                } else {
                    var action = 'password modification';
                    SystemLogRecorder.addSystemLog(req.session.user.name, action);    
        
                    req.flash('success','Password modifiy successfully');
                    res.redirect('/formPage1');
                }
            });           
        });        
    });
    
    app.get('/pdfPreview', checkLogin);
    app.get('/pdfPreview', function(req, res) {
        res.render('pdfPreview', {
            title: 'pdfPreview',
            layout:'layout2'
        });
    });
    
    app.get('/pdfPreview/pdfInfo', checkLogin);
    app.get('/pdfPreview/pdfInfo', function(req, res) { 
        var fileName = req.session.user.name + "_study_plan.pdf";
        var file_exists = fs.existsSync('./public/userDocument/' + fileName);
        
        res.writeHead(200, {'Content-Type': 'text/plain'})
        
        if(file_exists == true){    
            res.write(fileName); 
        } else {
            res.write('');
        }
            
        res.end();
    });
        
    app.get('/login', checkNotLogin);
    app.get('/login', function(req, res) {
        res.render('login', {
            title: 'User Login',
            layout:'layout'
        });
    }); 
    
    app.post('/login', checkNotLogin);
    app.post('/login', function(req, res) {
        //var md5 = crypto.createHash('md5');
        //var password = md5.update(req.body.password).digest('base64');
        if(req.body.username.length == 0){
            req.flash('error', 'Username should not be empty');
            return res.redirect('/login');
        }
        
        if(req.body.password.length == 0){
            req.flash('error', 'Password should not be empty');
            return res.redirect('/login');
        }
        
        var password = req.body.password;
            User.get(req.body.username, function(err, user) {
            if (!user) {
                req.flash('error', 'Username does not exist');
                return res.redirect('/login');
            }
            if (user.password != password) {
                req.flash('error', 'Wrong Password');
                return res.redirect('/login');
            }
            
            var action = 'log in';
            SystemLogRecorder.addSystemLog(user.name, action);
            
            req.session.user = user;  
            req.flash('success', 'Login Successfully!');
            res.redirect('/formPage1');
        });
    });
    
    app.get('/logout', checkLogin);
    app.get('/logout', function(req, res) {   
        var action = 'log out';
        SystemLogRecorder.addSystemLog(req.session.user.name, action);
        
        req.session.user = null;
        req.flash('success', 'Log Out Successfully!');
        res.redirect('/');
    });
    
    app.get('/loginAdmin', checkNotLogin);
    app.get('/loginAdmin', function(req, res) {
        res.render('loginAdmin', {
            title: 'Administor Login',
            layout:'layout'
        });
    }); 
    
    app.post('/loginAdmin', function(req, res) {
        //var md5 = crypto.createHash('md5');
        //var password = md5.update(req.body.password).digest('base64');
        var password = req.body.password;
            Administrator.get(req.body.administratorname, function(err, administrator) {
            if (!administrator) {
                req.flash('error', 'administrator does not exist');
                return res.redirect('/loginAdmin');
            }
            if (administrator.password != password) {
                req.flash('error', 'Wrong Password');
                return res.redirect('/loginAdmin');
            }
            
            var action = 'as administrator to log in';
            SystemLogRecorder.addSystemLog(administrator.name, action);
            
            req.session.user = administrator;  
            req.flash('success', 'Administrator Login Successfully!');
            res.redirect('/SystemLogPage');
        });
    });
    
    app.get('/SystemLogPage', function(req, res) {
        res.render('SystemLogPage', {
            title: 'SystemLog',
            layout:'layoutAdmin'
        });
    });
    
    app.get('/SystemLogPage/systemLogInfo', function(req, res) {
        SystemLogRecorder.get(function(err, systemLog){
            if(err){
                req.flash('error', err);
                console.log('error: ' + err.message);
            }
            
            if(systemLog.length > 0){
                var array = [];            
                for(var i = 0; i < systemLog.length; i++){
                    array[i] = systemLog[i].userSystemLog;
                }   
                var resString = array.join('$');
                res.writeHead(200, {'Content-Type': 'text/plain'});
                res.write(resString);               
            } else {
                res.write('$');
            }
            
            res.end();
        }); 
    });
    
    app.get('/userInfoAdmin', function(req, res) {
        res.render('userInfoAdmin', {
            title: 'userInfoAdmin',
            layout:'layoutAdmin'
        });
    });
    
    app.get('/userInfoAdmin/userInfo', function(req, res) {
        User.getAll(function(err, userInfo){
            if(err){
                req.flash('error', err);
                console.log('error: ' + err.message);
            }
            
            res.writeHead(200, {'Content-Type': 'text/plain'});
            
            if(userInfo.length > 0){
                var array = [];            
                for(var i = 0; i < userInfo.length; i++){
                    array[i] = userInfo[i].name + ':' + userInfo[i].password;
                }   
                var resString = array.join('$');
                res.write(resString);               
            } else {
                res.write('$');
            }
            
            res.end();
        }); 
    });
    
    app.get('/Feedback', function(req, res) {
        res.render('Feedback', {
            title: 'Feedback',
            layout:'layoutAdmin'
        });
    });
    
    app.get('/Feedback/feedbackInfo', function(req, res) {
        Feedback.get(function(err, feedbackList){
            if(err){
                req.flash('error', err);
                console.log('error: ' + err.message);
            }
            
            if(feedbackList.length > 0){
                var array = [];            
                for(var i = 0; i < feedbackList.length; i++){
                    array[i] = feedbackList[i].userFeedback;
                }   
                var resString = array.join('$');
                res.writeHead(200, {'Content-Type': 'text/plain'});
                res.write(resString);               
            } else {
                res.write('$');
            }
            
            res.end();
        }); 
    });
    
    app.get('/auth/google', passport.authenticate('google', { scope: ['https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile'] }),
    function(req, res){
        
    });

    app.get('/auth/google/callback', passport.authenticate('google', { failureRedirect: '/login' }),
    function(req, res) {        
        var newUser = new User({
            name: global.googleUser + "-google+",
            password: " "
        });
    
        req.session.user = newUser;
                
        var action = 'google+ account sign in';
        SystemLogRecorder.addSystemLog(req.session.user.name, action);                
        req.flash('success','google+ sign in successfully');
        res.redirect('/formPage1');
    });
    
    app.get('/＊{1,}', function(req, res) {
        res.redirect('/');
    });
};


function checkLogin(req, res, next){
    if(!req.session.user){
        req.flash("error","Please login first");
        return res.redirect("/");
    }
    next();
}
function checkNotLogin(req, res, next){
    if(req.session.user){
        req.flash("error","Please log out first");
        return res.redirect("/index");
    }
    next();
}

function ensureAuthenticated(req, res, next) {
  if (req.isAuthenticated()) { return next(); }
  res.redirect('/login');
}


