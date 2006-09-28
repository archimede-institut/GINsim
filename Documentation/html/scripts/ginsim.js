var memory = new Array();

function show(id) {
    l = document.getElementById("l_"+id);
    obj = document.getElementById(id);
    obj = obj.parentNode;
    obj = obj.childNodes[5];
    obj = obj.childNodes[1];
    
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
