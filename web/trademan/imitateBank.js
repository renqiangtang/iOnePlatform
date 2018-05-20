$(document).ready(function(event) {
			Utils.autoIframeSize();
			$("#transTime10002").click(function() {
						WdatePicker({
									dateFmt : 'yyyy-MM-dd HH:mm:ss'
								})
					});

			$('#btnSend10001').click(function(event) {
						var cmd = new Command();
						cmd.module = "bank";
						cmd.service = "ImitateBank";
						cmd.method = "send10001";
						cmd.centerNo = $("#centerNo10001").val().trim();
						cmd.accountName = $("#accountName10001").val().trim();
						cmd.bankid = $("#bankid10001").val().trim();
						cmd.currency = $('#currency10001').val().trim();
						cmd.success = function(data) {
							alert(data.msg);
						}
						cmd.execute();
					});

			$('#btnSend10004').click(function(event) {
						var cmd = new Command();
						cmd.module = "bank";
						cmd.service = "ImitateBank";
						cmd.method = "send10004";
						cmd.centerNo = $("#centerNo10004").val();
						cmd.name = $("#name10004").val();
						cmd.no = $("#no10004").val();
						cmd.amoutStr = $("#amoutStr10004").val();
						cmd.bankId = $("#bankId10004").val();
						cmd.success = function(data) {
							alert(data.msg);
						}
						cmd.execute();
					});

			$('#btnSend10003').click(function(event) {
						var cmd = new Command();
						cmd.module = "bank";
						cmd.service = "ImitateBank";
						cmd.method = "send10003";
						cmd.benjin = $("#benjin10003").val();
						cmd.no = $("#no10003").val();
						cmd.beginDate = $("#beginDate10003").val();
						cmd.endDate = $("#endDate10003").val();
						cmd.bankid = $("#bankid10003").val();
						cmd.success = function(data) {
							alert(data.msg);
						}
						cmd.execute();
					});

			$('#btnSend10005').click(function(event) {
						var cmd = new Command();
						cmd.module = "bank";
						cmd.service = "ImitateBank";
						cmd.method = "send10005";
						cmd.accountNo = $("#accountNo10005").val();
						cmd.bankid = $("#bankid10005").val();
						cmd.success = function(data) {
							alert(data.msg);
						}
						cmd.execute();
					});

			$('#btnSend10002').click(function(event) {
						var cmd = new Command();
						cmd.module = "bank";
						cmd.service = "ImitateBank";
						cmd.method = "send10002";
						cmd.childNo = $("#childNo10002").val();
						cmd.amount = $("#amount10002").val();
						cmd.currency = $("#currency10002").val();
						cmd.transTime = $("#transTime10002").val();
						cmd.bankid = $("#bankid10002").val();
						cmd.rate = $("#rate10002").val();
						cmd.success = function(data) {
							alert(data.msg);
						}
						cmd.execute();
					});

		});
