console.log("TODO");

var questions = $(".user-question");

questions.each(function() {
  $( this ).on( "click", function() {
    $(".basic.modal").modal("show");
  });
});    
