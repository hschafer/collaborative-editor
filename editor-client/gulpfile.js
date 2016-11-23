var gulp = require('gulp');
var nodemon = require('gulp-nodemon');
var babel = require('gulp-babel');

gulp.task('serve:build', function(done) {
  return gulp.src('./server.js')
    .pipe(babel())
    .on('error', function handleError() {
      this.emit('end');
    })
    .pipe(gulp.dest('./lib'));
});

gulp.task('serve:run', function(done) {
  nodemon({
    exec: 'node ./lib/server.js',
    watch: ['lib/server.js'],
    ext: 'js html'
  });
});

gulp.task('serve', ['serve:build', 'serve:run']);
