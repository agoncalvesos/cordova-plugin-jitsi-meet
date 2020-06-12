var exec = require('cordova/exec');

exports.loadURL = function(jitsiOptions, success, error) {
  exec(success, error, "JitsiPlugin", "loadURL", [jitsiOptions]);
};