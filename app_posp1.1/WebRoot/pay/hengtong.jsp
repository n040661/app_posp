<%@page import="xdt.util.UtilDate"%>
<%@ page contentType="text/html;charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" language="java"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>代付</title>

<link rel="shortcut icon" href="favicon.ico">
<link href="http://www.zi-han.net/theme/hplus/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="http://www.zi-han.net/theme/hplus/css/font-awesome.min.css?v=4.4.0"
	rel="stylesheet">
<link
	href="http://www.zi-han.net/theme/hplus/css/plugins/iCheck/custom.css"
	rel="stylesheet">
<link href="http://www.zi-han.net/theme/hplus/css/animate.min.css"
	rel="stylesheet">
<link href="http://www.zi-han.net/theme/hplus/css/style.min.css?v=4.1.0"
	rel="stylesheet">

</head>

<body class="gray-bg">
	<div class="wrapper wrapper-content animated fadeInRight">
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5>恒通安泰</h5>
					</div>
					<div class="ibox-content">
						<form class="form-horizontal" name="form-common">
							<div class="form-group">
								<label class="col-sm-3 control-label">服务类型：</label>

								<div class="col-sm-8">
									<input type="text" name="service" value="cj006"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">商户号：</label>

								<div class="col-sm-8">
									<input type="text" name="merchantCode" value="10061274410"
										placeholder="商户号" class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">订单号(<font
									color="red">查询时候添加自己的订单号，下面都不要填</font>)：
								</label>

								<div class="col-sm-8">
									<input type="text" name="orderNum"
										value="<%=System.currentTimeMillis()%>" placeholder="商户号"
										class="form-control">
								</div>
							</div>
							<hr>
							<div class="form-group">
								<label class="col-sm-3 control-label">收款人账户号：</label>

								<div class="col-sm-8">
									<input type="text" name="bankCard" value="6217000010074056345"
										class="form-control">
								</div>
							</div>
							<hr>
							<div class="form-group">
								<label class="col-sm-3 control-label">收款人账户名：</label>

								<div class="col-sm-8">
									<input type="text" name="accountName" value="薛迎昭 "
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">收款人账户开户行名称：</label>

								<div class="col-sm-8">
									<input type="text" name="bankName" value="北京百子湾支行"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">收款人账户开户行联行号：</label>

								<div class="col-sm-8">
									<input type="text" name="bankLinked" value="105100023057"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">交易类型：</label>

								<div class="col-sm-8">
									<input type="text" name="type" value="1" class="form-control">
								</div>
							</div>
							<hr>
							<div class="form-group">
								<label class="col-sm-3 control-label">金额：</label>

								<div class="col-sm-8">
									<input type="number" name="transMoney" value="100"
										class="form-control"> <input type="hidden" name="sign"
										class="form-control">
								</div>
							</div>

							<div class="form-group">
								<div class="col-sm-offset-3 col-sm-8">
									<button class="btn btn-sm btn-success"
										onclick="QrCodeAction.submit()" type="button">确定</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>

		</div>


		<!-- 模态框（Modal） -->
		<div class="modal fade" style="display: none;" id="myModal"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">结果</h4>
					</div>
					<div class="modal-body"></div>
					<!-- <img alt="二维码" src="" id="qrcode" style="width: 100;height: 100px;"> -->
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭
						</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->

		</div>

		<script
			src="http://www.zi-han.net/theme/hplus/js/jquery.min.js?v=2.1.4"></script>
		<script
			src="http://www.zi-han.net/theme/hplus/js/bootstrap.min.js?v=3.3.6"></script>
		<script
			src="http://www.zi-han.net/theme/hplus/js/content.min.js?v=1.0.0"></script>
		<script
			src="http://www.zi-han.net/theme/hplus/js/plugins/iCheck/icheck.min.js"></script>
		<script>
			$(document).ready(function() {
				$(".i-checks").iCheck({
					checkboxClass : "icheckbox_square-green",
					radioClass : "iradio_square-green",
				})
			});
		</script>
		<script type="text/javascript"
			src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
		<script type="text/javascript">
			var baseurl = 'http://' + window.location.host;
			baseurl += "/app_posp/live/qrcode";

			var QrCodeAction = {
				submit : function() {
					this.sign(function(data) {
						$('[name="sign"]').val(data);
						var html = '';
						console.info('生成二维码');
						$.ajax({
							url : baseurl + '/interface.action',
							type : 'post',
							dataType : 'json',
							data : $('[name="form-common"]').serialize(),
							success : function(data) {
								html += JSON.stringify(data);
								QrCodeAction.show(html);
							}
						})

					})
				},
				show : function(html) {
					$('#myModal .modal-body').html(html);
					$('#myModal').modal('show');
					/* 					var url='http://s.jiathis.com/qrcode.php?url='+html.url;
					 $('#qrcode').attr('src',url); */
				},
				sign : function(callback) {
					$.post(baseurl + '/paySign.action', $(
							'[name="form-common"]').serialize(),
							function(data) {
								callback(data);
							})
				}
			}
		</script>
</body>

</html>
