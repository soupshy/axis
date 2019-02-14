package test;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import net.sf.json.JSONObject;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import util.HttpHelper;

import java.io.*;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {
        server1();
    }
    //杭州社保同步新建单位接口
    public static void server1() throws Exception {
        String url = "http://59.202.31.21:8091/nws/services/NwsServerNew";    //正式环境内网地址

        /** step1: 创建一个Service实例，注意是必须的！ */
        Service service = new Service();
        /** step2:创建Call实例，也是必须的！ */
        Call call = (Call) service.createCall();
        /** step3: 为Call设置服务的位置 操作方法名 */
        call.setTargetEndpointAddress(new java.net.URL(url));
        call.setOperationName("nwsReceive");// 操作的方法

        /** step4映射要传递自定义类型 设置返回类型等(可选) 如果返回的是自定义类型也要映射 */
        call.setReturnType(XMLType.XSD_STRING);

        /** step5: 为方法增加参数,传几个参数写几个 */
        call.addParameter(new QName("http://www.openuri.org/", "systemCode"), XMLType.XSD_STRING, ParameterMode.IN);
        call.addParameter(new QName("http://www.openuri.org/", "tradeCode"), XMLType.XSD_STRING, ParameterMode.IN);
        call.addParameter(new QName("http://www.openuri.org/", "interactXml"), XMLType.XSD_STRING, ParameterMode.IN);
        /** step6: 用参数数组调用Web Service */
        String interactCode = "9GEM3G";
        File file = new File("D:\\lastUpdated1.txt");
        String lineTxt = "";
        InputStreamReader read = new InputStreamReader(new FileInputStream(file));
        BufferedReader bufferedReader = new BufferedReader(read);
        lineTxt = bufferedReader.readLine();
        System.out.println(lineTxt);
        read.close();
        String interactXml = "{\"lastUpdated\":\"" + lineTxt + "\"}";
        //String result = (String) call.invoke(new Object[] {"HZCA",interactCode,interactXml});
        //System.out.println(result);
        String result = "{\"isOk\":true,\"message\":\"\",\"now\":\"2016-12-26 12:58:00\",\"orgInfoList\":[{\"orgName\":\"杭州有家装饰设计工程司\",\"socialSecurityNum\":\"6155886\",\"businessNum\":\"91330105MA280LPW68\",\"taxRegistrationCode\":\"\",\"socialCreditCode\":\"91330105MA280LPW68\",\"orgCode\":\"\"}]}";
        JSONObject jasonObject = JSONObject.fromObject(result);
        if (jasonObject.getBoolean("isOk")) {
            String now = jasonObject.optString("now");
            /* 写入Txt文件 */
            File writename = new File("D:\\lastUpdated1.txt");
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(now);
            out.flush();

        }
        Map map = jasonObject;
        String requestUri = "http://localhost:8080/warriors/api/v1/serviceForApp/synchroOrgService";
        //String requestUri = "http://cert.hzca.org.cn/warriors/api/v1/serviceForApp/synchroOrgService";
        String appId = "HZSocialSecurity";
        String appSecret = "99c19cad65793eaf67d201f52670ad9c";
        long timestamp = System.currentTimeMillis();
        map.put("timestamp", timestamp);
        map.put("appId", appId);
        String strJsonRespData = HttpHelper.postSignedJson(requestUri, new org.json.JSONObject(map), appSecret);
        org.json.JSONObject jsonRespData = new org.json.JSONObject(strJsonRespData);
        if (jsonRespData.getBoolean("isOk")) {
            System.out.println(strJsonRespData);
        }
    }
}
