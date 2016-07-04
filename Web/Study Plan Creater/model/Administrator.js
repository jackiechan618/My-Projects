var mongodb = require('./../db');

function Administrator(administrator) {
    this.name = administrator.name;
    this.password = administrator.password;
};

module.exports = Administrator;

Administrator.get = function get(administratorname, callback) {
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }

        db.collection('administrators', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }
            collection.findOne({name: administratorname}, function(err, doc) {
                mongodb.close();
                if (doc) {
                    var administrator = new Administrator(doc);
                    callback(err, administrator);
                } else {
                    callback(err, null);
                }
            });
        });
    });
};