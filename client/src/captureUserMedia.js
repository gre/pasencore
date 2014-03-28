
var Q = require("q");

navigator.getUserMedia  = navigator.getUserMedia ||
navigator.webkitGetUserMedia ||
navigator.mozGetUserMedia ||
navigator.msGetUserMedia;

window.URL = window.URL || window.webkitURL;

function lazify (f) {
  var res;
  return function () {
    if (res) return res;
    else return (res = f.apply(this, arguments));
  };
}

module.exports = lazify(function captureUserMedia() {
  var d = Q.defer();
  navigator.getUserMedia({ audio: true, video: true }, d.resolve, d.reject);
  return d.promise;
});
