<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>订单查询</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/404/cmstop-error.css"
	media="all">
</head>
<body class="body-bg">

	<div align="center">
		<a href="${pageContext.request.contextPath}/index.jsp" style="margin: 50px 0 0 0;" class="btn">返回网站首页</a>
		<h1 style="margin-top: 10px;">微信订单查询返回结果:</h1>
		<br />
		<table>
			<tr>
				<td>return_code</td>
				<td><b>${orderQuery.return_code }</b></td>
			</tr>
			<tr>
				<td>return_msg</td>
				<td><b>${orderQuery.return_msg }</b></td>
			</tr>
			<tr>
				<td>result_code</td>
				<td><b>${orderQuery.result_code }</b></td>
			</tr>
			<tr>
				<td>appid</td>
				<td>${orderQuery.appid }</td>
			</tr>
			<tr>
				<td>mch_id</td>
				<td>${orderQuery.mch_id }</td>
			</tr>
			<tr>
				<td>device_info</td>
				<td>${orderQuery.device_info }</td>
			</tr>
			<tr>
				<td>nonce_str</td>
				<td>${orderQuery.nonce_str }</td>
			</tr>
			<tr>
				<td>err_code</td>
				<td>${orderQuery.err_code }</td>
			</tr>
			<tr>
				<td>err_code_des</td>
				<td>${orderQuery.err_code_des }</td>
			</tr>
			<tr>
				<td>is_subscribe</td>
				<td>${orderQuery.is_subscribe }</td>
			</tr>
			<tr>
				<td>nonce_str</td>
				<td>${orderQuery.nonce_str }</td>
			</tr>
			<tr>
				<td>trade_type</td>
				<td>${orderQuery.trade_type }</td>
			</tr>
			<tr>
				<td>bank_type</td>
				<td>${orderQuery.bank_type }</td>
			</tr>
			<tr>
				<td>fee_type</td>
				<td>${orderQuery.fee_type }</td>
			</tr>
			<tr>
				<td>transaction_id</td>
				<td>${orderQuery.transaction_id }</td>
			</tr>
			<tr>
				<td>out_trade_no</td>
				<td>${orderQuery.out_trade_no }</td>
			</tr>
			<tr>
				<td>sign</td>
				<td>${orderQuery.sign }</td>
			</tr>
			</div>
</body>