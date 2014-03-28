
var Q = require("q");
var Qajax = require("qajax");
var $ = window.jQuery;
var RecordRTC = window.RecordRTC;

var FIX_VIDEO_DURATION = 2000;

var captureUserMedia = require("./captureUserMedia");

var currentQuestionId;
var $questions = $(".user-question");
var $video = $("#webcamFeedback");
var $record = $("#record");
var $stop = $("#stop");
var $submit = $("#submit");
var $cancel = $("#cancel");
var $video = $("#webcamFeedback");
var $recording = $(".recording");
var $submitting = $(".submitting");

var audioVideoRecorder;

function hideModal () {
  $(".basic.modal").modal("hide");
  $recording.show();
  $submitting.hide();
}

function submitVideo (url) {
  return function (blob) {
    var d = Q.defer();
    var oReq = new XMLHttpRequest();
    oReq.open("POST", url, true);
    oReq.onload = d.resolve;
    oReq.onerror = d.reject;
    oReq.send(blob);
    return d.promise;
  };
}

var blobPromise;

$record.click(function () {
  $record.hide();
  captureUserMedia().then(function(stream) {
    $stop.show();
    $video.removeAttr("controls");
    $video.attr("autoplay", "autoplay");
    $video[0].volume = 0;
    $video[0].src = window.URL.createObjectURL(stream);
    audioVideoRecorder = RecordRTC(stream);
    audioVideoRecorder.startRecording();
  });
});

function stopRecording () {
  var d = Q.defer();
  audioVideoRecorder.stopRecording(d.resolve);
  return d.promise;
}

$stop.click(function() {
  $stop.hide();
  $recording.hide();
  $submitting.show();
  $video[0].src = "";
  blobPromise =
    Q.delay(FIX_VIDEO_DURATION)
    .then(stopRecording)
    .then(function (url) {
      $video.removeAttr("autoplay");
      $video.attr("controls", "controls");
      $video[0].volume = 1;
      $video[0].src = url;
      return audioVideoRecorder.getBlob();
    });

  /*
  setTimeout(function () {
    audioVideoRecorder.stopRecording(function(url) {
      $video.removeAttr("autoplay");
      $video.attr("controls", "controls");
      $video[0].volume = 1;
      $video[0].src = url;
      $
    });
    $recording.hide();
    $submitting.show();
  }, FIX_VIDEO_DURATION);
  */
});

$submit.click(function () {
  blobPromise
    .then(submitVideo("/api/video/"+currentQuestionId))//, audioVideoRecorder.getBlob())
    .fin(hideModal);
});
$cancel.click(hideModal);


// Init

$(".basic.modal").modal("setting", "onHide", function () {
  if (audioVideoRecorder) {
    audioVideoRecorder.stopRecording();
    $video[0].src = "";
    audioVideoRecorder = null;
  }
  $record.show();
  $stop.hide();
  $recording.show();
  $submitting.hide();
});

$questions.each(function() {
  $(this).on("click", function() {
    currentQuestionId = $(this).attr("data-id");
    $(".basic.modal").modal("show");
  });
});

