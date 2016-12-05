var gulp = require('gulp');
var babel = require('gulp-babel');
var babelify = require('babelify');
var browserify = require('browserify');
var source = require('vinyl-source-stream');

gulp.task('build:server', function(done) {
  return gulp.src('./server.js')
    .pipe(babel())
    .on('error', function handleError() {
      this.emit('end');
    })
    .pipe(gulp.dest('./lib'));
});

gulp.task('build:app', function(done) {
  return browserify({entries: './app/js/main.js', debug: true})
    .transform(babelify, {presets: ['es2015']})
    .bundle()
    .pipe(source('editor.js'))
    .pipe(gulp.dest('./public/js'));
});

gulp.task('build:style', function(done) {
  return gulp.src('./app/css/editor.css')
    .pipe(gulp.dest('./public/css'));
});

gulp.task('build', ['build:server', 'build:app', 'build:style']);
