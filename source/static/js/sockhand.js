
//function that listens to Socket and do something when notification comes
function listen() {
    var source = new WebSocket('ws://' + window.location.host + '/server/cd69ad9f-e08a-41e0-ab9f-cbe4b953c7dc');
    var parent = document.getElementById("mycol")
    source.onmessage = function(msg) {
      //var message = JSON.parse(msg.data);
      console.log(msg.data);

      var child = document.createElement("DIV");
      child.className = 'ui red message';

      //var text = message['new_val']['name'].toUpperCase() + ' joined the league on  '+ Date();
      var content = document.createTextNode(msg.data);
      child.appendChild(content);
      parent.appendChild(child);
      return false;
    }
}

$(document).ready(function(){
console.log('I am ready');
listen();
});
