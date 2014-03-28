
var Q = require("q");

navigator.getUserMedia  = navigator.getUserMedia ||
navigator.webkitGetUserMedia ||
navigator.mozGetUserMedia ||
navigator.msGetUserMedia;

window.URL = window.URL || window.webkitURL;

module.exports = (function captureUserMedia() {
  var d = Q.defer();
  navigator.getUserMedia({ audio: true, video: true }, d.resolve, d.reject);
  return d.promise;
}());
