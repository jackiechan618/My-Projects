var mongodb = require('./../db');

function User(user) {
    this.name = user.name;
    this.password = user.password;
};

module.exports = User;

User.prototype.save = function save(callback) {
    console.log("in save");

    var user = {
        name: this.name,
        password: this.password
    };
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }

        db.collection('users', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }

            collection.ensureIndex('name', {unique: true},{w:1});
            collection.insert(user, {safe: true}, function(err, user) {
                mongodb.close();
                callback(err, user);
            });
        });
    });
};

User.get = function get(username, callback) {
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }

        db.collection('users', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }
            collection.findOne({name: username}, function(err, doc) {
                mongodb.close();
                if (doc) {
                    var user = new User(doc);
                    callback(err, user);
                } else {
                    callback(err, null);
                }
            });
        });
    });
};

User.getAll = function getAll(callback) {
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }

        db.collection('users', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }
            
            collection.find().toArray(function(err, userInfo) {
                mongodb.close();
                if (err) {
                    callback(err, null);
                }
                callback(null, userInfo);
            });
        });
    });
};

User.modifyPassword = function modifyPassword(username, newPassword, callback_1) {
    var myUserInfo = {
        username: username,
        password: newPassword
    };
    
    mongodb.open(function(err, db) {
        if (err) {
            console.log('error: ' + err);
            return callback_1(err);
        }
        db.collection('users', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }
            
            collection.find({"name": myUserInfo.username}).sort({time: -1}).toArray(function(err, docs) {
                if(docs.length > 0){
                    var updateData = {$set: {"password" : myUserInfo.password}};
                    
                    collection.update({"name": myUserInfo.username}, updateData, function(err, result){
                        if(err) {
                            console.log('Error:'+ err);
                            err = 'Error';
                        }
                        mongodb.close();
                        return callback_1(err);
                    });
                }
            });
        });
    });
};
