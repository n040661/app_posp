<%@page import="xdt.util.UtilDate"%>
<%@ page contentType="text/html;charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" language="java"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>测试页面</title>

<link rel="shortcut icon" href="favicon.ico">
<link href="../css/hplus/bootstrap.min.css"
	rel="stylesheet">
<link
	href="../css/hplus/font-awesome.css?v=4.4.0"
	rel="stylesheet">
<link
	href="../css/hplus/custom.css"
	rel="stylesheet">
<link href="../css/hplus/animate.min.css"
	rel="stylesheet">
<link href="../css/hplus/style.min.css?v=4.1.0"
	rel="stylesheet">

</head>

<body class="gray-bg">
	<div class="wrapper wrapper-content animated fadeInRight">
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5>江苏电商</h5>
					</div>
					<div class="ibox-content">
						<form class="form-horizontal" name="form-common">
							<div class="form-group">
								<label class="col-sm-3 control-label">服务类型：</label>

								<div class="col-sm-8">
									<input type="text" name="service" value=""
										placeholder="cj001:支付宝   ,cj002:微信   ,cj003:公众号 ,cj004:查询订单,cj005:QQ钱包  ,cj006:额度代付 ,cj007:网关,cj008:京东扫码"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">商户号：</label>

								<div class="col-sm-8">
									<input type="text" name="merchantCode" value="100510112345708"
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
							<font color="red">网关需要填,其他都不需要填</font>
							<div class="form-group">
								<label class="col-sm-3 control-label">前台通知页面地址：</label>

								<div class="col-sm-8">
									<input type="text" name="returnUrl"
										value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action" class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">银行编码：</label>

								<div class="col-sm-8">
									<input type="text" name="bankCode" value="ICBC"
										class="form-control">
								</div>
							</div>
							<hr>
							<font color="red">代付不需要填,其他都需要填</font>
							<div class="form-group">
								<label class="col-sm-3 control-label">异步通知url：</label>

								<div class="col-sm-8">
									<input type="text" name="notifyUrl"
										value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">商户名称：</label>

								<div class="col-sm-8">
									<input type="text" name="merchantName" value="重庆小面"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">商户门店编号：</label>

								<div class="col-sm-8">
									<input type="text" name="merchantNum"
										value="<%=UtilDate.getOrderNum()%>" class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">商品名称：</label>

								<div class="col-sm-8">
									<input type="text" name="commodityName" value="担担面"
										class="form-control">
								</div>
							</div>
							<hr>
							<font color="red">代付和网关填</font>
							<div class="form-group">
								<label class="col-sm-3 control-label">收款人账户号：</label>

								<div class="col-sm-8">
									<input type="text" name="bankCard" value="6226220126775369"
										class="form-control">
								</div>
							</div>
							<hr>
							<font color="red">只有代付需要填</font>
							<div class="form-group">
								<label class="col-sm-3 control-label">收款人账户名：</label>

								<div class="col-sm-8">
									<input type="text" name="accountName" value="李鑫"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">收款人账户开户行名称：</label>

								<div class="col-sm-8">
									<input type="text" name="bankName" value="中国民生银行北京上地支行"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">收款人账户开户行联行号：</label>

								<div class="col-sm-8">
									<input type="text" name="bankLinked" value="305100001104"
										class="form-control">
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label">交易类型D0或T1：</label>

								<div class="col-sm-8">
									<input type="text" name="type" value="D0" class="form-control">
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
			src="../js/hplus/jquery.min.js?v=2.1.4"></script>
		<script
			src="../js/hplus/bootstrap.min.js?v=3.3.6"></script>
		<script
			src="../js/hplus/content.min.js?v=1.0.0"></script>
		<script
			src="../js/hplus/icheck.min.js"></script>
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
						console.info(data);
								callback(data);
							})
				}
			}
		</script>
</body>

</html>
