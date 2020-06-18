var exec = require('cordova/exec');

exports.loadURL = function(jitsiOptions, success, error) {
  
  if(cordova.platformId === "ios"){
    var splitedUrl = jitsiOptions.url.split("/");
    jitsiOptions.room = splitedUrl[splitedUrl.length - 1];
	jitsiOptions.server = splitedUrl.slice(0,-1).join("/")
  }
  exec(success, error, "JitsiPlugin", "loadURL", [jitsiOptions]);
};