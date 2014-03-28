module.exports = function (grunt) {

  require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

  // Project configuration.
  grunt.initConfig({
    uglify: {
      prod: {
        src: 'tmp/bundle-before-min.js',
        dest: '../server/public/bundle.js'
      }
    },
    stylus: {
      app: {
        src: 'src/index.styl',
        dest: '../server/public/bundle.css'
      }
    },
    browserify: {
      app: {
        src: 'src/index.js',
        dest: '../server/public/bundle.js',
        options: {
          transform: ["hbsfy"],
          debug: true
        }
      },
      prod: {
        src: 'src/index.js',
        dest: 'tmp/bundle-before-min.js',
        options: {
          transform: ["hbsfy"]
        }
      }
    },
    watch: {
      js: {
        files: ['src/**/*.js', 'src/**/*.hbs'],
        tasks: ['browserify:app']
      },
      css: {
        files: ['src/**/*.styl'],
        tasks: ['stylus']
      }
    }
  });


  grunt.registerTask('default', ['build', 'watch']);
  grunt.registerTask('build', ['browserify:app', 'stylus:app']);
  grunt.registerTask('build-prod', ['browserify:prod', 'uglify', 'stylus:app']);
};
