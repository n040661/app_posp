<%@ page contentType="text/html;charset=UTF-8" import="xdt.quickpay.hengfeng.util.*"  language="java" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>测试页面</title>

    <link rel="shortcut icon" href="favicon.ico"> <link href="http://www.zi-han.net/theme/hplus/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href="http://www.zi-han.net/theme/hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="http://www.zi-han.net/theme/hplus/css/plugins/iCheck/custom.css" rel="stylesheet">
    <link href="http://www.zi-han.net/theme/hplus/css/animate.min.css" rel="stylesheet">
    <link href="http://www.zi-han.net/theme/hplus/css/style.min.css?v=4.1.0" rel="stylesheet">

</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <div class="row">
        	<div class="col-sm-6">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>一、注册</h5>
                    </div>
                    <div class="ibox-content">
                        <form class="form-horizontal" name="form-register">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">商户号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="merchId" value="100510112345708" placeholder="商户号" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">账号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="account" value="13706743781" placeholder="用户名" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">密码：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="password" value="kefu123" placeholder="密码" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-8">
                                    <button class="btn btn-sm btn-success" onclick="QrCodeAction.reg()" type="button">注册</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        	
        	<div class="col-sm-6">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>二、下载私钥</h5>
                    </div>
                    <div class="ibox-content">
                        <form class="form-horizontal" name="form-down">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">商户号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="merchId" value="100510112345708" placeholder="商户号" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">账号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="account" value="13706743781" placeholder="用户名" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">密码：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="password" value="kefu123" placeholder="密码" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-8">
                                    <button class="btn btn-sm btn-success" onclick="QrCodeAction.down()" type="button">下载</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        	
         </div>
         
         <div class="row">
        	<div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>三、验证小商户信息</h5>
                    </div>
                    <div class="ibox-content">
                        <form class="form-horizontal" name="form-verify">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">商户号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="merchId" value="100510112345708"  placeholder="商户号" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">账号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="account" value="13706743781" placeholder="用户名" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">密码：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="password" value="kefu123" placeholder="密码" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">密钥：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="privateKey" placeholder="密钥" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">真实姓名：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="realName" placeholder="真实姓名" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">商户名称：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="merchartName" placeholder="商户名称" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">商户简称：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="merchartNameSort" placeholder="商户简称" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">联系人手机号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="phone" placeholder="联系人手机号" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">结算卡类型：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="cardType" placeholder="结算卡类型" readonly="readonly" value="1"  class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">结算卡号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="cardNo" placeholder="结算卡号" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">证件类型：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="certType" placeholder="证件类型" readonly="readonly" value="00"  class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">证件号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="certNo" placeholder="证件号" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">开户手机号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="mobile" placeholder="开户手机号" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">开户城市：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="location" placeholder="开户城市" class="form-control">
                                </div>
                            </div>
                            
                            
                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-8">
                                    <button class="btn btn-sm btn-success" onclick="QrCodeAction.verify()" type="button">确认</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        	
         </div>
         
          <div class="row">
        	<div class="col-sm-6">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>四、同步费率</h5>
                    </div>
                    <div class="ibox-content">
                        <form class="form-horizontal" name="form-rate">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">商户号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="merchId" value="100510112345708" placeholder="商户号" class="form-control"> 
                                </div>
                            </div>
                           <div class="form-group">
                                <label class="col-sm-3 control-label">账号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="account" value="15800818231" placeholder="用户名" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">密码：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="password" value="kefu123" placeholder="密码" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">费率：</label>

                                <div class="col-sm-8">
                                    <input type="number" name="rate" value="0.004" placeholder="费率" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-8">
                                    <button class="btn btn-sm btn-success" onclick="QrCodeAction.rate()" type="button">确认</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        	
        	<div class="col-sm-6">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>五、生成二维码</h5>
                    </div>
                    <div class="ibox-content">
                        <form class="form-horizontal" name="form-pay">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">商户号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="merchId" value="100510112345708" placeholder="商户号" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">账号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="account" value="15800818231" placeholder="用户名" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">订单号：</label>

                                <div class="col-sm-8">
                                    <input type="text" name="orderNo" value="<%=HFUtil.randomOrder()%>" placeholder="订单号" class="form-control"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">支付方式：</label>

                                <div class="col-sm-8">
                                	<select class="form-control m-b" name="orderCode">
                                        <option value="tb_WeixinPay">微信</option>
                                        <option value="tb_alipay">支付宝</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">金额：</label>

                                <div class="col-sm-8">
                                    <input type="number" name="totalFee" placeholder="金额" value="200" class="form-control">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-8">
                                    <button class="btn btn-sm btn-success" onclick="QrCodeAction.prepay()" type="button">支付</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        	
         </div>
         <div class="row">
        	<div class="col-sm-6">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>六、查询支付结果</h5>
                    </div>
                    <div class="ibox-content">
                        <form class="form-horizontal" name="form-query">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">订单号：</label>

                                <div class="col-sm-8">
                                    <input type="text" placeholder="订单号" class="form-control" name="orderNo"> 
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-8">
                                    <button class="btn btn-sm btn-success" type="button" onclick="QrCodeAction.query()" >查询</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        	
         </div>
         
<!-- 模态框（Modal） -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title" id="myModalLabel">
					结果
				</h4>
			</div>
			<div class="modal-body">
				在这里添加一些文本
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭
				</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
         
    </div>
    
    <script src="http://www.zi-han.net/theme/hplus/js/jquery.min.js?v=2.1.4"></script>
    <script src="http://www.zi-han.net/theme/hplus/js/bootstrap.min.js?v=3.3.6"></script>
    <script src="http://www.zi-han.net/theme/hplus/js/content.min.js?v=1.0.0"></script>
    <script src="http://www.zi-han.net/theme/hplus/js/plugins/iCheck/icheck.min.js"></script>
    <script>
        $(document).ready(function(){$(".i-checks").iCheck({checkboxClass:"icheckbox_square-green",radioClass:"iradio_square-green",})});
    </script>
    <script type="text/javascript" src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
	<script type="text/javascript">
	
	var baseurl='http://'+window.location.host;
	 //注册url
	 $REGISTERURL = baseurl+"/app_posp/cj/qrcode/register.action";
	 //下载密钥url
    var $DownLoadKeyURL = baseurl+"/app_posp/cj/qrcode/downkey.action";
    //验证商户信息url
    var $verifyInfoURL = baseurl+"/app_posp/cj/qrcode/valida.action";
    //同步费率url
    var $ChangeRate = baseurl+"/app_posp/cj/qrcode/updateRate.action";
    //生成二维码url
    var $WeixinPayURL = baseurl+"/app_posp/cj/qrcode/pay.action";
    //查询订单状态url
    var $OrderConfirmURL = baseurl+"/app_posp/cj/qrcode/query.action";
		
		var QrCodeAction={
				reg:function(){
					var html='注册<br>';
					console.info('注册');
					$.ajax({
						url:$REGISTERURL,
						type:'post',
						dataType :'text',
						data:$('[name="form-register"]').serialize(),
						success:function(data){
							html+=data;
							QrCodeAction.show(html);
						}
					})
				},
				down:function(){
					var html='下载密钥<br>';
					console.info('下载密钥');
					$.ajax({
						url:$DownLoadKeyURL,
						type:'post',
						dataType :'json',
						data:$('[name="form-down"]').serialize(),
						success:function(data){
							html+='<p>respCode:'+data.respCode+'</p>';
							html+='<p>userid:'+data.userid+'</p>';
							html+='<p>privatekey:</p>';
							html+='<textarea style="margin: 0px; width: 492px; height: 351px;">'+data.privatekey+'</textarea>';
							QrCodeAction.show(html);
						}
					})
				},
				verify:function(){
					var html='验证商户信息<br>';
					console.info('验证商户信息');
					$.ajax({
						url:$verifyInfoURL,
						type:'post',
						dataType :'text',
						data:$('[name="form-verify"]').serialize(),
						success:function(data){
							html+=data;
							QrCodeAction.show(html);
						}
					})
				},
				rate:function(){
					var html='同步费率<br>';
					console.info('同步费率');
					$.ajax({
						url:$ChangeRate,
						type:'post',
						dataType :'text',
						data:$('[name="form-rate"]').serialize(),
						success:function(data){
							html+=data;
							QrCodeAction.show(html);
						}
					})
				},
				prepay:function(){
					var html='生成二维码<br>';
					console.info('生成二维码');
					$.ajax({
						url:$WeixinPayURL,
						type:'post',
						dataType :'json',
						data:$('[name="form-pay"]').serialize(),
						success:function(data){
							html+=JSON.stringify(data);
							if(data.respCode=='0000'){
								data.url="http://www.baidu.com";
								$payurl=data.QRcodeURL;
								$url="http://s.jiathis.com/qrcode.php?url="+$payurl;
								html+="<p><img src='"+$url+"' width='100px' width='100px' alt='图片二维码'/></p>";
							}else{
								data.url="http://www.baidu.com";
								$weixinUrl=data.url;
								$url="http://s.jiathis.com/qrcode.php?url="+$weixinUrl;
								html+="<p><img src='"+$url+"' width='100px' width='100px' alt='图片二维码'/></p>";
							}
							QrCodeAction.show(html);
						}
					})
					
				},
				query:function(){
					var html='查询结果<br>';
					console.info('查询结果');
					$.ajax({
						url:$OrderConfirmURL,
						type:'post',
						dataType :'text',
						data:$('[name="form-query"]').serialize(),
						success:function(data){
							html+=data;
							QrCodeAction.show(html);
						}
					})
					
				},
				show:function(html){
					
					$('#myModal .modal-body').html(html);
					$('#myModal').modal('show');
				}
		}
	
	
	
	
	</script>
</body>

</html>
