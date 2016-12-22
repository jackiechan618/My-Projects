var nodemailer = require('nodemailer');
var fs = require('fs');
var SystemLogRecorder = require('./../model/SystemLog');

function autoSendEmail(filePath, username, DestinationEmail){  
    var mailOptions = {
        from: 'Study Plan Creater',
        to: DestinationEmail,
        subject: 'SSL Email',
        html: 'Hello, this is your study plan, thanks for using Study Plan Creater',
        attachments: [{
            path: filePath
        }]
    }
    
    var stransporter = nodemailer.createTransport({
        service: 'Gmail',
        secureConnection: true, // use SSL
        port: 465, // port
        auth: {
            user: 'XXX',
            pass: 'XXX'
        }
    }); 
    
    stransporter.sendMail(mailOptions, function(error, info){
        if(error){
            console.log(error);
        }else{
            var timeOut = 5000 * 1; 
            setTimeout(function() {
                fs.unlinkSync(filePath);
            }, timeOut);
            
            console.log('Study Plan sent to ' + DestinationEmail + ': ' + info.response);
            
            var action = 'send study plan to ' + DestinationEmail;
            SystemLogRecorder.addSystemLog(username, action);
        }
    });
}

function sendFeedback(firstName, lastName, areaCode, telnum, Email, contactMethod, feedback){  
    var currentTime = new Date();
    var time = '<' + currentTime.getMonth() + '/' + currentTime.getDate() + '/' + currentTime.getFullYear() + ', ' + currentTime.getHours() + ':' + currentTime.getMinutes() + ':' + currentTime.getSeconds() + '>' 
    
    var userFeedBackString = time + " Customer feedback (firstName: " + firstName + ", lastName: " + lastName + ", Tel: " + areaCode + "-" + telnum + ", Email: " + Email + ", contactMethod: " + contactMethod + ", feedback: " + feedback + ")."
            
    var mailOptions = {
        from: 'Study Plan Creater',
        to: 'XXX',
        subject: 'SSL Email',
        html: userFeedBackString
    }
    
    var stransporter = nodemailer.createTransport({
        service: 'Gmail',
        secureConnection: true, // use SSL
        port: 465, // port
        auth: {
            user: 'XXX',
            pass: 'XXX'
        }
    }); 
    
    stransporter.sendMail(mailOptions, function(error, info){
        if(error){
            console.log(error);
        }else{
            console.log('Feedback sent: ' + info.response);
            
            var action = 'send feedback';
            var customer = "anonymous user"
            SystemLogRecorder.addSystemLog(customer, action);
        }
    });
}

exports.autoSendEmail = autoSendEmail;
exports.sendFeedback = sendFeedback;






