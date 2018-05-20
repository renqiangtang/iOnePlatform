var debug={};
debug.arr=[];
debug.push=function(time1,str){
	var time=(new Date()).toString();
	var obj={};
	obj.no=debug.arr.length+1;
	if(time1)
		obj.time=time1;
	else
		obj.time=time;
	obj.info=str;
	debug.arr.push(obj);
}

//for(var i=0;i<100;i++)
//	debug.push(i);

$(document).ready(function() {
	 $(document).keydown(function(e) {
	 	try{
	 		var key=Utils.event.getKeyNum(e);
	 		if (e.shiftKey && key==27){
	 			var url=approot+"/sysman/debug.html";
		 		//window.open(url,"","height=500;width=800;resizable=yes;status=no;location=no;");
		 		window.showModelessDialog(url ,debug.arr ,'dialogHeight=500px; dialogWidth=800px; center=yes; resizable=yes; status=no');
			 	e.cancelBubble = true;
		        e.returnValue = false;	
	 		}
	 	}catch(ex){}
	 });
});