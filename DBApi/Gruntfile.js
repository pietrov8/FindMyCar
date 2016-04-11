module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        serve: {
            path: './index.php',
            options: {
                port: 1337
            }
        },

        php: {
            dist: {
                options: {
                    keepalive: true,
                    port: 1337
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-serve');
    grunt.loadNpmTasks('grunt-php');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.registerTask('default', ['php']);
};