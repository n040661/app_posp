package xdt.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

public class JsonTest {

	public static void main(String[] args) {

		// String aa =
		// "{\"AcceptStatus\":\"F\",\"InputCharset\":\"UTF-8\",\"PartnerId\":\"200001160096\",\"RetCode\":\"REQUIRED_FIELD_NOT_EXIST\",\"RetMsg\":\"['BankCommonName','BankCommonName']必填字段未填写\",\"Sign\":\"POAxfV1h3neNquEld8YXP7vvxFy8Bo2vt9khrMpTRc/r8SpmEagBgUOC8bJv4FAhDcCkGVj5iVHAesmWAInaCGaBpyiGOCUmTWhRZSU91VqgyDWImMIJPiXzdsnZLkDmbSBS2mHTP/71anlxNOPmCbN/SsFZhQOwe5oIPNmNezk=\",\"SignType\":\"RSA\",\"TradeDate\":\"20171117\",\"TradeTime\":\"161149\"}";

//		String bb = "{\"ext\":{},\"key\":\"0000\",\"msg\":\"认证通过\",\"requestId\":\"2017113010573754197363\"}";
//		JSONObject ob1 = JSONObject.fromObject(bb);
//		Iterator it1 = ob1.keys();
//		Map<String, String> map = new HashMap<>();
//		while (it1.hasNext()) {
//			String key1 = (String) it1.next();
//			if (key1.equals("key")) {
//
//				String value = ob1.getString(key1);
//
//				System.out.println(value);
//
//			}
//			if (key1.equals("msg")) {
//
//				String value = ob1.getString(key1);
//
//				System.out.println(value);
//
//			}
//		}
		String bankName = "中国农业银行天津广厦支行";
		String name=bankName.substring(0, 6);
		
		System.out.println(name);
	   //猴子算法	
//		int[10] source,flag,res;
//		int sort(){
//		    memset(flag,1,sizeof(flag));
//		    int num = 10,count=0;
//		    while(num){
//		        int t =rand()%10;   //生成0-9之间的数
//		        if(flag[t]){
//		            res[count++] = source[t];
//		            num--;
//		        }
//		    }
//		    for(int i=0;i<9;i++){
//		        if(res[i]>res[i+1]){      //只有是从小到大的排列才行
//		            return 0;
//		        } 
//		    }
//		    return 1;
//		}
//		int main(){
//		    int count = 0;
//		    for(int i=0;i<10;i++){
//		        cin>>source[i];
//		    }
//		    while(sort()!=1){
//		        count++;
//		    }
//		    cout<<"共运行了"<<count<<"次"<<endl;
//		    return 0;
//		}

	}

}
