var memory = new Array();
var _debug = false;

function debug(o) {
       if (_debug) {
               alert(o);
       }
}

function getChildIgnoreText(obj, index) {
       var childs = obj.childNodes;
       var c=0;
       for(var i = 0; i<childs.length; i++){
               o = childs[i];
               if (o.nodeType != 3) {
                       debug("fils "+c+" (reel: "+i+": "+o+" ; "+o.type);
                       if (c == index) {
                               debug("return "+o);
                               return o;
                       }
                       c++;
               }
       }
}

function show(id) {
   var p = document.getElementById(id).parentNode;
   obj = getChildIgnoreText(p, 1);

   p = p.parentNode;
   l = getChildIgnoreText(p, 3);

   if (memory[id] == true) {
       obj.src = obj.src.replace(/.gif$/, ".png");
       l.innerHTML = "Show animation";
       memory[id] = false;
   } else {
       obj.src = obj.src.replace(/.png$/, ".gif");
       l.innerHTML = "Hide animation";
       memory[id] = true;
   }
}