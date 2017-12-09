$(document).ready(function(){ 
	var iaasInfo = getIaas();
	var iaasIp = (iaasInfo.ip ? iaasInfo.ip:"");
	var iaasId = (iaasInfo.id ? iaasInfo.id:"");
	$("#iaasIp").val(iaasIp);
	$("#iaasId").val(iaasId);
	if(iaasIp == ""){
		$("#iaas-submit").show();
		$(".iaas-modify-delete").hide(); 
	}
	else{
		$("#iaas-submit").hide();
		$(".iaas-modify-delete").show(); 
	}
	$("#iaas-submit").click(function(){
		iaasSubmit();
	});
	$("#iaas-modify").click(function(){
		iaasModify();
	});
	$("#iaas-delete").click(function(){
		iaasDelete();
	});
})