var mongodb = require('./../db');

function StudyPlanForm(studyPlanForm) {
    this.username = studyPlanForm.username;
    this.firstName = studyPlanForm.firstName;
    this.lastName = studyPlanForm.lastName;
    this.sId = studyPlanForm.sId;
    this.Email = studyPlanForm.Email;
    this.Dept = studyPlanForm.Dept;
    this.Major = studyPlanForm.Major;
    this.Concentration = studyPlanForm.Concentration;
    this.Degree = studyPlanForm.Degree;
    this.gYear = studyPlanForm.gYear;
    this.gTerm = studyPlanForm.gTerm;
    this.Opt = studyPlanForm.Opt;
    this.courseList = [];
};

module.exports = StudyPlanForm;

StudyPlanForm.prototype.save = function save(callback) {
    var form = {
        username: this.username,
        firstName: this.firstName,
        lastName: this.lastName,
        sId: this.sId,
        Email: this.Email,
        Dept: this.Dept,
        Major: this.Major,
        Concentration: this.Concentration,
        Degree: this.Degree,
        gYear: this.gYear,
        gTerm: this.gTerm,
        Opt: this.Opt,
        courseList: this.courseList
    };
    
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }
        db.collection('forms', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }

            collection.find({"username": form.username}).sort({time: -1}).toArray(function(err, docs) {
                if(docs.length > 0){
                    var updateData = {$set: { "firstName" : form.firstName, "lastName" : form.lastName, "sId" : form.sId, "Email" : form.Email, "Dept" : form.Dept, "Major" : form.Major, "Concentration" : form.Concentration, "Degree" : form.Degree, "gYear" : form.gYear, "gTerm" : form.gTerm, "Opt" : form.Opt}};
                    
                    collection.update({"username": form.username}, updateData, function(err, result){
                        if(err) {
                            console.log('Error:'+ err);
                            err = 'Error';
                        }
                        mongodb.close();
                        callback(err, form);
                    });
                } else {
                    collection.ensureIndex('username',{w:1});
                    collection.insert(form, {safe: true}, function(err, form) {
                        mongodb.close();
                        callback(err, form);
                    });
                }
            });
        });
    });
};

StudyPlanForm.get = function get(username, callback) {
    var myUserName = username;
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }

        db.collection('forms', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }

            collection.find({"username" : myUserName}).sort({time: -1}).toArray(function(err, docs) {
                mongodb.close();
                if (err) {
                    callback(err, null);
                }
                
                callback(null, docs);
            });
        });
    });
};

StudyPlanForm.addCourseList = function addCourseList(username, courseList, callback) {
    var myCourseInfo = {
        username: username,
        courseList: courseList
    };
    
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }
        db.collection('forms', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }
            
            collection.find({"username": myCourseInfo.username}).sort({time: -1}).toArray(function(err, docs) {
                if(docs.length > 0){
                    var updateData = {$set: {"courseList" : myCourseInfo.courseList}};
                    
                    collection.update({"username": myCourseInfo.username}, updateData, function(err, result){
                        if(err) {
                            console.log('Error:'+ err);
                            err = 'Error';
                        }
                        mongodb.close();
                        callback(err);
                    });
                }
            });
        });
    });
};

