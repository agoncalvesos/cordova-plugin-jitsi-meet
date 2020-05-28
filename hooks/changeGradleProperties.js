var fs = require('fs');

module.exports = function (context) {
    return new Promise(function (resolve, reject) {

        var projectRoot = context.opts.projectRoot;

        const gradlePropertiesFile = projectRoot + '/platforms/android/gradle.properties'

        let fileContents = fs.readFileSync(gradlePropertiesFile, 'utf-8');
        console.log("Handling gradle.properties file")
        if (fileContents) {
            var containsProperty = fileContents.match("android.enableDexingArtifactTransform.desugaring");
            if (containsProperty) {
                console.log("Property android.enableDexingArtifactTransform.desugaring exists, setting value to false")
                fileContents=fileContents.replace("android.enableDexingArtifactTransform.desugaring=true", "android.enableDexingArtifactTransform.desugaring=false")
            } else {
                fileContents += "\nandroid.enableDexingArtifactTransform.desugaring=false"
            }
            fs.writeFileSync(gradlePropertiesFile, fileContents, 'utf-8');
            return resolve();
        } else {
            console.log("gradle.properties file not found")
            return reject
        }
    });
};