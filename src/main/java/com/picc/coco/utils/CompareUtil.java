package com.picc.coco.utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.scripts.JS;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CompareUtil {

    /**
     * 判断两个值是否一致
     *
     * @param resp         第一个值（一般是接口返回的值）
     * @param db           第二个值（一般是数据库存的值）
     * @param errorMessage 存放错误信息
     * @param fieldName    字段名
     */
    private static void valueCompare(Object resp, Object db, StringBuffer errorMessage, String fieldName) {
        System.out.println(fieldName + "开始进行校验");
        //null的处理，如果是null返回空字符串，如果不是null转换为String类型
        String respStr = isNull(resp).trim();
        String dbStr = isNull(db).trim();
        //时间类型的处理
        respStr = isDate(respStr);
        dbStr = isDate(dbStr);
        System.out.println("接口返回======"+respStr);
        System.out.println("数据库======"+dbStr);
        // 数字类型的值的比较
        if (isNumber(respStr) && isNumber(dbStr)) {
            if ((new BigDecimal(respStr)).compareTo(new BigDecimal(dbStr)) != 0) {
                errorMessage.append(fieldName + "的值数据库和接口返回的不一致 \n");
                errorMessage.append("接口===" + respStr + "\n");
                errorMessage.append("数据库===" + dbStr + "\n");
            }
        } else {
            if (!respStr.equals(dbStr)) {
                errorMessage.append(fieldName + "的值数据库和接口返回的不一致 \n");
                errorMessage.append("接口===" + respStr + "\n");
                errorMessage.append("数据库===" + dbStr + "\n");
            }
        }
    }

    /**
     * 从JSON数据里取出对应的节点数据
     *
     * @param fieldName 节点名
     * @param JS        JSON数据
     * @param resultJA  返回或者请求报文的节点数据（作为listCompare的方法参数）
     */
    public static void getJAByField(String fieldName, JSONObject JS, JSONArray resultJA) {
        Set<Entry<String, Object>> entrySet = JS.entrySet();
        boolean flag = false;
        for (Entry<String, Object> entry : entrySet) {
            if ((entry.getKey()).equals(fieldName)) {
                flag = true;
                if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                    JSONArray temJA = (JSONArray) entry.getValue();
                    for (int i = 0; i < temJA.size(); i++) {
                        resultJA.add(temJA.getJSONObject(i));
                    }
                } else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    resultJA.add((JSONObject) entry.getValue());
                }
                break;
            }
        }
        if (!flag) {
            for (Entry<String, Object> entry : entrySet) {
                if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                    try {
                        JSONArray temJA = (JSONArray) entry.getValue();
                        for (int i = 0; i < temJA.size(); i++) {
                            getJAByField(fieldName, temJA.getJSONObject(i), resultJA);
                        }
                    } catch (Exception e) {
                        System.out.println("JSONArray  转化失败");
                    }
                } else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    try {
                        getJAByField(fieldName, (JSONObject) entry.getValue(), resultJA);
                    } catch (Exception e) {
                        System.out.println("转化失败");
                    }

                }
            }
        }

    }

    /**
     * 数据库表的数据和返回报文对应接口数据校验
     *
     * @param resps        返回或者请求报文的节点数据
     * @param db           数据库的数据
     * @param errorMessage 错误信息
     * @param fieldName    节点名
     * @param ignoreList   存放不需要校验的字段
     */
    public static void listCompare(JSONArray resps, JSONArray db, StringBuffer errorMessage, String fieldName,
                                       ArrayList<String> ignoreList) {
        if (ignoreList == null) {
            ignoreList = new ArrayList<String>();
        }
        /**
         * 这里更加确定ID 根据ID进行数据校验
         */
        ArrayList<String> ids = new ArrayList<String>();
        JSONObject temRespIdJs = null;
        if (resps != null && resps.size() > 0) {
            try {
                temRespIdJs = resps.getJSONObject(0).getJSONObject("id");
            } catch (Exception e) {
                // TODO: handle exception
            }

            if (temRespIdJs != null) {
                Set<Entry<String, Object>> entrySet_id = temRespIdJs.entrySet();
                for (Entry<String, Object> ide : entrySet_id) {
                    ids.add(ide.getKey());
                }
            }
        }

        /**
         * ID 里的字段放入到IgnoreList
         */
        ignoreList.addAll(ids);

        if (resps.size() == db.size()) {
            if (ids == null || ids.size() == 0) {
                for (int i = 0; i < resps.size(); i++) {
                    JSONObjectCompare(resps.getJSONObject(i), db.getJSONObject(i), errorMessage, ignoreList);
                }
            } else {
                for (int i = 0; i < resps.size(); i++) {
                    JSONObject resp_tem = resps.getJSONObject(i);
                    for (int j = 0; j < db.size(); j++) {
                        JSONObject db_tem = db.getJSONObject(j);
                        if (isIdentical(resp_tem, db_tem, ids)) {
                            JSONObjectCompare(resp_tem, db_tem, errorMessage, ignoreList);
                        }
                    }
                }
            }
        } else {
            errorMessage.append(fieldName + "接口返回的数据和数据库的数据数量不同");
        }
    }

    private static boolean isIdentical(JSONObject resp_tem, JSONObject db_tem, ArrayList<String> ids) {
        boolean flag = true;
        String resp_value = "";
        String db_value = "";
        for (String key : ids) {
            if(resp_tem.get(key) == null) {
                resp_value = resp_tem.getJSONObject("id").getString(key);
            }else {
                resp_value = resp_tem.getString(key);
            }
            db_value = db_tem.getString(key.toLowerCase());
            if(!resp_value.equals(db_value)) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * NUll值的处理
     * @param value1
     * @return
     */
    private static String isNull(Object value1) {
        if (value1 == null) {
            return "";
        } else {
            return value1.toString();
        }
    }

    /**
     * 判断一个值是不是可以数字格式 可以判断正整型，正浮点型
     */
    private static boolean isNumber(String str) {
        if (str.matches("[0-9]+[.]?[0-9]*")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验两个jsonObject的值是不是相等
     *
     * @param resp         接口返回的数据
     * @param db           数据库的一条数据
     * @param errorMessage 存放错误信息
     * @param ignoreList   存放不需要校验的字段
     */
    public static void JSONObjectCompare(JSONObject resp, JSONObject db, StringBuffer errorMessage,
                                         ArrayList<String> ignoreList) {
        if(ignoreList == null) {
            ignoreList = new ArrayList<String>();
        }
        Set<Entry<String, Object>> entrySet = resp.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            if (ignoreList.contains(entry.getKey())) {
                continue;
            }
            if (entry.getValue() != null
                    && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                continue;
            } else if (entry.getValue() != null
                    && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")
                    && !entry.getKey().equals("id")) {
                continue;
            } else if (entry.getKey().equals("id")) {
                JSONObject temId = null;
                try {
                    temId = resp.getJSONObject("id");
                } catch (Exception e) {
                }
                if(temId==null) {
                    continue;
                }
                Set<Entry<String, Object>> entrySet_id = temId.entrySet();
                for (Entry<String, Object> ide : entrySet_id) {
                    valueCompare(temId.get(ide.getKey()), db.get(ide.getKey().toLowerCase()), errorMessage,
                            ide.getKey());
                }
            } else {
                valueCompare(resp.get(entry.getKey()), db.get(entry.getKey().toLowerCase()), errorMessage,
                        entry.getKey());
            }
        }
    }

    /**
     * 判断一个字段是不是时间类型,如果时分秒没有值，直接去除时分秒，如果时分秒有值，保留时分秒。
     * @param  value
     * @return String
     * */
    private static String isDate(String value) {
        String result = "";
        if(value.contains(".")) {
            result = value.substring(0,value.indexOf("."));
        }
        if(value.contains("T")) {
            result = value.replace("T", " ");
        }
        if(value.contains("00:00:00")) {
            result = value.substring(0,10);
        }
        if(value.length()==13 && value.charAt(0)!='0'&& isNumber(value)){
            Long l=Long.parseLong(value);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = simpleDateFormat.format(l);
        }
        boolean flag = false;
        String pattern = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

        String pattern1 = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|"
                + "((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|"
                + "((0[48]|[2468][048]|[3579][26])00))-02-29)$";

        if (result.matches(pattern) || result.matches(pattern1)) {
            flag = true;
        }
        if(flag) {
            return result;
        }else {
            return value;
        }
    }

    /**
     * 去掉数据库字段的下划线（核保微服务）
     * @param JA
     * @return
     */
    public static JSONArray dealDbData(JSONArray JA) {
        JSONArray resultJA = new JSONArray();
        if(JA != null) {
            for (int i = 0; i < JA.size(); i++) {
                JSONObject dbJS = JA.getJSONObject(i);
                JSONObject temJS = dealData(dbJS);
                resultJA.add(temJS);
            }
        }
        return resultJA;
    }

    public static JSONObject dealData(JSONObject JS) {
        JSONObject temJS = new JSONObject();
        Set<Entry<String, Object>> entrySet = JS.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            temJS.put(entry.getKey().replaceAll("_", ""), entry.getValue());
        }
        return temJS;
    }

    public static void getFiledName(String value,ArrayList<String> dbv)
    {
        JSONObject jsonObject = JSONObject.parseObject(value).getJSONObject("data");
        Set<Entry<String, Object>> entrySet = jsonObject.entrySet();

        HashMap<String,String> table_field = new HashMap<String,String>();
        table_field.put("prpCmainAirLines","prpcmain_airline");
        table_field.put("Insureds","prpcinsured");
        /*table_field.put("prpPmainAccs","prpPmain_Accs");
        table_field.put("Addresses","Add_resses");*/

        boolean flag = false;
        for (Entry<String, Object> entry : entrySet) {
            for (String key : table_field.keySet()) {
                if ((entry.getKey()).equals(key)) {
                    flag = true;
                    if (entry.getValue() != null
                            && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                        dbv.add(table_field.get(key));
                    }
                    else if (entry.getValue() != null
                            && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                        dbv.add(table_field.get(key));
                    }
                    break;
                }
            }
        }
        if (!flag) {
            for (Entry<String, Object> entry : entrySet) {
                if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                    try {
                        String value1 = (String) entry.getValue();
                        getFiledName(value1,dbv);
                    } catch (Exception e) {
                        System.out.println("JSONArray  转化失败");
                    }
                } else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    try {
                        String value1 = (String) entry.getValue();
                        getFiledName(value1,dbv);
                    } catch (Exception e) {
                        System.out.println("转化失败");
                    }

                }
            }
        }

    }

    public static void main(String[] args) {
  /*     String value = "{\"data\":{\"archivesNo\":\"RC00300000000202000050\",\"checkUpCode\":\"A000019300\",\"checkUpCodeCName\":\"\",\"checkUpFlag\":\"0\",\"comCode\":\"00000000\",\"companyName\":\"ggg\",\"exploreAddress\":\"北京市海淀区北三环西路辅路99号西海国际中心\",\"exploreComcode\":\"00000000\",\"exploreComcodeCName\":\"\",\"exploreDate\":\"2019-08-21\",\"explorer\":\"\",\"explorerCName\":\"\",\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"message\":\"根据照片档案号查询照片档案信息成功\",\"mobileFlag\":\"1\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"riskReportSaleImaTypeList\":[{\"id\":{\"archivesNo\":\"RC00300000000202000050\",\"imageType\":\"1.1\"},\"imageRepulseSum\":0,\"imageSum\":1,\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"remark\":\"\",\"riskReportSaleImageList\":[{\"id\":{\"archivesNo\":\"RC00300000000202000050\",\"imageName\":\"1.1.1\",\"imageType\":\"1.1\"},\"imageUrl\":\"/RC00300000000202000050/1.1/1.1.1.jpg\",\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"modifyFlag\":\"\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"pageId\":\"EDC1D4B3-FCD7-BB54-468D-477A59DF3A13\",\"remark\":\"\",\"repulseReason\":\"\",\"riskReportSaleCorrectionList\":[],\"riskSuggest\":\"\",\"riskType\":\"\",\"stateFlag\":\"0\",\"thumUrl\":\"\",\"title\":\"\",\"urlAfter\":\"\",\"urlName\":\"\"}],\"typeCName\":\"大门照\",\"url\":\"\"},{\"id\":{\"archivesNo\":\"RC00300000000202000050\",\"imageType\":\"3.2\"},\"imageRepulseSum\":0,\"imageSum\":1,\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"remark\":\"\",\"riskReportSaleImageList\":[{\"id\":{\"archivesNo\":\"RC00300000000202000050\",\"imageName\":\"3.2.1\",\"imageType\":\"3.2\"},\"imageUrl\":\"/RC00300000000202000050/3.2/3.2.1.jpg\",\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"modifyFlag\":\"\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"pageId\":\"54DC3F4E-8530-9139-DAC2-198E91514842\",\"remark\":\"\",\"repulseReason\":\"\",\"riskReportSaleCorrectionList\":[],\"riskSuggest\":\"\",\"riskType\":\"\",\"stateFlag\":\"0\",\"thumUrl\":\"\",\"title\":\"\",\"urlAfter\":\"\",\"urlName\":\"\"}],\"typeCName\":\"材料燃烧性质\",\"url\":\"\"},{\"id\":{\"archivesNo\":\"RC00300000000202000050\",\"imageType\":\"4.2.5\"},\"imageRepulseSum\":0,\"imageSum\":1,\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"remark\":\"\",\"riskReportSaleImageList\":[{\"id\":{\"archivesNo\":\"RC00300000000202000050\",\"imageName\":\"4.2.5.1\",\"imageType\":\"4.2.5\"},\"imageUrl\":\"/RC00300000000202000050/4.2.5/4.2.5.1.jpg\",\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"modifyFlag\":\"\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"pageId\":\"F80B6955-4330-37E4-DE66-EC21B3BEE465\",\"remark\":\"\",\"repulseReason\":\"\",\"riskReportSaleCorrectionList\":[],\"riskSuggest\":\"\",\"riskType\":\"\",\"stateFlag\":\"0\",\"thumUrl\":\"\",\"title\":\"\",\"urlAfter\":\"\",\"urlName\":\"\"}],\"typeCName\":\"消防设施维护检查\",\"url\":\"\"},{\"id\":{\"archivesNo\":\"RC00300000000202000050\",\"imageType\":\"5.1.4\"},\"imageRepulseSum\":0,\"imageSum\":1,\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"remark\":\"\",\"riskReportSaleImageList\":[{\"id\":{\"archivesNo\":\"RC00300000000202000050\",\"imageName\":\"5.1.4.1\",\"imageType\":\"5.1.4\"},\"imageUrl\":\"/RC00300000000202000050/5.1.4/5.1.4.1.jpg\",\"insertTimeForHis\":\"2020-07-27 17:20:49\",\"modifyFlag\":\"\",\"operateTimeForHis\":\"2020-07-27 17:20:49\",\"pageId\":\"F84D64B4-8AD0-2555-A6F0-EBEF7F6166C7\",\"remark\":\"\",\"repulseReason\":\"\",\"riskReportSaleCorrectionList\":[],\"riskSuggest\":\"\",\"riskType\":\"\",\"stateFlag\":\"0\",\"thumUrl\":\"\",\"title\":\"\",\"urlAfter\":\"\",\"urlName\":\"\"}],\"typeCName\":\"方位地势照\",\"url\":\"\"}],\"status\":0},\"status\":0,\"statusText\":\"Success\"}";
       String dbvalue = "[{\"operatetimeforhis\":1595841649000,\"imagetype\":\"3.2\",\"imagesum\":1,\"inserttimeforhis\":1595841649000,\"archivesno\":\"RC00300000000202000050\",\"imagerepulsesum\":0,\"remark\":\"\",\"typecname\":\"材料燃烧性质\"},{\"operatetimeforhis\":1595841649000,\"imagetype\":\"1.1\",\"imagesum\":1,\"inserttimeforhis\":1595841649000,\"archivesno\":\"RC00300000000202000050\",\"imagerepulsesum\":0,\"remark\":\"\",\"typecname\":\"大门照\"},{\"operatetimeforhis\":1595841649000,\"imagetype\":\"4.2.5\",\"imagesum\":1,\"inserttimeforhis\":1595841649000,\"archivesno\":\"RC00300000000202000050\",\"imagerepulsesum\":0,\"remark\":\"\",\"typecname\":\"消防设施维护检查\"},{\"operatetimeforhis\":1595841649000,\"imagetype\":\"5.1.4\",\"imagesum\":1,\"inserttimeforhis\":1595841649000,\"archivesno\":\"RC00300000000202000050\",\"imagerepulsesum\":0,\"remark\":\"\",\"typecname\":\"方位地势照\"}] ";
        JSONArray objects1 = JSONArray.parseArray(dbvalue);
        JSONObject jsonObject = JSONObject.parseObject(value);
        JSONArray objects = new JSONArray();
        StringBuffer errorMessage = new StringBuffer();
        getJAByField("riskReportSaleImaTypeList",jsonObject,objects);
        System.out.println(objects);
        listCompare(objects,objects1,errorMessage,"riskReportSaleImaTypeList",null);
        System.out.println(errorMessage);*/

        String str ="{\n" +
                "    \"status\":0,\n" +
                "    \"statusText\":\"Success\",\n" +
                "    \"data\":{\n" +
                "        \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "        \"policyNo\":\"PJBU202032046000005452\",\n" +
                "        \"classCode\":\"02\",\n" +
                "        \"riskCode\":\"JBU\",\n" +
                "        \"riskName\":null,\n" +
                "        \"projectCode\":\"\",\n" +
                "        \"contractNo\":\"\",\n" +
                "        \"policySort\":\"1\",\n" +
                "        \"businessNature\":\"0\",\n" +
                "        \"language\":\"C\",\n" +
                "        \"policyType\":\"01\",\n" +
                "        \"agriFlag\":\"0\",\n" +
                "        \"operateDate\":\"2020-01-01 13:11:17\",\n" +
                "        \"startDate\":\"2020-01-08 00:00:00\",\n" +
                "        \"endDate\":\"2021-01-07 00:00:00\",\n" +
                "        \"startHour\":null,\n" +
                "        \"endHour\":null,\n" +
                "        \"disRate\":null,\n" +
                "        \"sumValue\":0,\n" +
                "        \"sumAmount\":200,\n" +
                "        \"sumDiscount\":null,\n" +
                "        \"sumPremiumB4Discount\":null,\n" +
                "        \"couponAmount\":null,\n" +
                "        \"couponPremium\":null,\n" +
                "        \"minPremium\":null,\n" +
                "        \"sumPremium\":20,\n" +
                "        \"sumSubPrem\":0,\n" +
                "        \"sumQuantity\":1,\n" +
                "        \"policyCount\":1,\n" +
                "        \"judicalScope\":\"01\",\n" +
                "        \"argueSolution\":\"1\",\n" +
                "        \"arbitBoardName\":\"\",\n" +
                "        \"payTimes\":1,\n" +
                "        \"makeCom\":\"32048200\",\n" +
                "        \"operateSite\":null,\n" +
                "        \"comCode\":\"32048200\",\n" +
                "        \"handlerCode\":\"16207959\",\n" +
                "        \"handler1Code\":\"16207986\",\n" +
                "        \"checkFlag\":\"4\",\n" +
                "        \"checkUpCode\":null,\n" +
                "        \"checkOpinion\":null,\n" +
                "        \"underWriteCode\":\"UnderWrite\",\n" +
                "        \"underWriteName\":\"自动核保\",\n" +
                "        \"operatorCode\":\"83298873\",\n" +
                "        \"inputTime\":\"2020-01-11 17:11:04\",\n" +
                "        \"underWriteEndDate\":\"2020-01-15\",\n" +
                "        \"statisticsYM\":\"202001\",\n" +
                "        \"agentCode\":\"000002000001\",\n" +
                "        \"coinsFlag\":\"00\",\n" +
                "        \"reinsFlag\":\"0000000000\",\n" +
                "        \"allinsFlag\":\"0\",\n" +
                "        \"underWriteFlag\":\"3\",\n" +
                "        \"jfeeFlag\":\"0\",\n" +
                "        \"inputFlag\":\"0\",\n" +
                "        \"undwrtSubmitDate\":\"2020-01-15\",\n" +
                "        \"othFlag\":\"000000YY00\",\n" +
                "        \"remark\":null,\n" +
                "        \"checkCode\":null,\n" +
                "        \"flag\":null,\n" +
                "        \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "        \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "        \"payMode\":\"9\",\n" +
                "        \"payCode\":null,\n" +
                "        \"crossFlag\":\"0\",\n" +
                "        \"batchGroupNo\":null,\n" +
                "        \"sumTaxFee\":1.13,\n" +
                "        \"sumNetPremium\":18.87,\n" +
                "        \"prePremium\":null,\n" +
                "        \"pkey\":\"TJBU202032046000005683\",\n" +
                "        \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "        \"currency\":\"CNY\",\n" +
                "        \"dmFlag\":null,\n" +
                "        \"handler1Code_uni\":\"1232061117\",\n" +
                "        \"handlerCode_uni\":\"\",\n" +
                "        \"isAutoePolicy\":null,\n" +
                "        \"salesCode\":null,\n" +
                "        \"personOri\":\"4\",\n" +
                "        \"productCode\":null,\n" +
                "        \"productName\":null,\n" +
                "        \"approverCode\":null,\n" +
                "        \"pureRate\":null,\n" +
                "        \"discount\":null,\n" +
                "        \"insuredCount\":null,\n" +
                "        \"auditNo\":null,\n" +
                "        \"auditNoToE\":null,\n" +
                "        \"crossSellType\":null,\n" +
                "        \"inputSumPremium\":null,\n" +
                "        \"dutySumNetPremium\":null,\n" +
                "        \"freeSumNetPremium\":null,\n" +
                "        \"orderNo\":null,\n" +
                "        \"orderFlag\":null,\n" +
                "        \"policyPageVo\":{\n" +
                "            \"tableMap\":{\n" +
                "                \"PrpCcoins\":0,\n" +
                "                \"PrpCname\":0,\n" +
                "                \"PrpCitem\":0,\n" +
                "                \"PrpCaddress\":0,\n" +
                "                \"PrpCinsured\":2,\n" +
                "                \"PrpCration\":1\n" +
                "            },\n" +
                "            \"pageGroupVos\":[\n" +
                "                {\n" +
                "                    \"groupNo\":1,\n" +
                "                    \"groupMap\":{\n" +
                "                        \"PrpCitemKind\":1\n" +
                "                    },\n" +
                "                    \"totalCountsNew\":null,\n" +
                "                    \"totalCountsOld\":null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"pageItemVos\":null\n" +
                "        },\n" +
                "        \"prpCmainAccs\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCmainExts\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCmainBonds\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCmainCredits\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCextendInfos\":[\n" +
                "            {\n" +
                "                \"id\":{\n" +
                "                    \"pkey\":\"TJBU202032046000005683\",\n" +
                "                    \"extendType\":\"04\",\n" +
                "                    \"serialNo\":1,\n" +
                "                    \"columnCode\":\"5173用户游戏帐号\"\n" +
                "                },\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"riskCode\":\"JBU\",\n" +
                "                \"columnName\":\"王者荣耀\",\n" +
                "                \"content\":\"1\",\n" +
                "                \"displayOrder\":0,\n" +
                "                \"flag\":null,\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"prpCmainAirLines\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCcommissions\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCcoeffs\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCmainAgris\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCprojects\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCprofitFactors\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCitemCreditOths\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCclauseplans\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCpayeeAccounts\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"prpCinsuredCreditInvests\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"actualProduct\":null,\n" +
                "        \"prpCmainExtraVo\":null,\n" +
                "        \"Employees\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Props\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"AgentDetails\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"shipDrivers\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Drivers\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Addresses\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"InsuredIdvLists\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Crosses\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Subs\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Agents\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Constructs\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Itemkinds\":[\n" +
                "            {\n" +
                "                \"id\":{\n" +
                "                    \"pkey\":\"TJBU202032046000005683\",\n" +
                "                    \"itemKindNo\":1\n" +
                "                },\n" +
                "                \"riskCode\":\"JBU\",\n" +
                "                \"familyNo\":null,\n" +
                "                \"familyName\":null,\n" +
                "                \"projectCode\":null,\n" +
                "                \"clauseCode\":\"020223\",\n" +
                "                \"clauseName\":\"网络游戏虚拟财产损失保险条款\",\n" +
                "                \"kindCode\":\"020164\",\n" +
                "                \"kindName\":\"网络游戏虚拟财产损失\",\n" +
                "                \"itemNo\":1,\n" +
                "                \"itemCode\":\"002131\",\n" +
                "                \"itemDetailName\":\"游戏账号\",\n" +
                "                \"groupNo\":1,\n" +
                "                \"modeCode\":\"JBUYY00001\",\n" +
                "                \"modeName\":\"虚拟财产保险060天至060天\",\n" +
                "                \"startDate\":\"2020-01-08\",\n" +
                "                \"startHour\":null,\n" +
                "                \"endDate\":\"2021-01-07\",\n" +
                "                \"endHour\":null,\n" +
                "                \"model\":null,\n" +
                "                \"buyDate\":null,\n" +
                "                \"addressNo\":null,\n" +
                "                \"calculateFlag\":\"1\",\n" +
                "                \"currency\":\"CNY\",\n" +
                "                \"unitAmount\":200,\n" +
                "                \"quantity\":1,\n" +
                "                \"unit\":\"1\",\n" +
                "                \"value\":null,\n" +
                "                \"amount\":200,\n" +
                "                \"ratePeriod\":null,\n" +
                "                \"rate\":10,\n" +
                "                \"shortRateFlag\":\"3\",\n" +
                "                \"shortRate\":100,\n" +
                "                \"prePremium\":null,\n" +
                "                \"calPremium\":20,\n" +
                "                \"basePremium\":null,\n" +
                "                \"benchMarkPremium\":null,\n" +
                "                \"discount\":null,\n" +
                "                \"adjustRate\":null,\n" +
                "                \"unitPremium\":20,\n" +
                "                \"premiumB4Discount\":null,\n" +
                "                \"premium\":20,\n" +
                "                \"deductibleRate\":null,\n" +
                "                \"deductible\":null,\n" +
                "                \"taxFee\":1.13,\n" +
                "                \"taxFee_ys\":null,\n" +
                "                \"taxFee_gb\":0,\n" +
                "                \"taxFee_lb\":0,\n" +
                "                \"netPremium\":18.87,\n" +
                "                \"allTaxFee\":1.13,\n" +
                "                \"allNetPremium\":18.87,\n" +
                "                \"taxRate\":6,\n" +
                "                \"taxFlag\":\"2\",\n" +
                "                \"flag\":null,\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"prpCprofits\":[\n" +
                "\n" +
                "                ],\n" +
                "                \"iscalculateFlag\":null,\n" +
                "                \"userCount\":null,\n" +
                "                \"pack\":null,\n" +
                "                \"firstLevel\":null,\n" +
                "                \"methodType\":null,\n" +
                "                \"insuredQuantity\":null,\n" +
                "                \"clauseFlag\":null,\n" +
                "                \"itemKindFactorFlag\":null,\n" +
                "                \"prpCitemKindTaxFees\":[\n" +
                "\n" +
                "                ],\n" +
                "                \"ItemKindDetails\":[\n" +
                "\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"Limits\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Loans\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Engages\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Insureds\":[\n" +
                "            {\n" +
                "                \"id\":{\n" +
                "                    \"pkey\":\"TJBU202032046000005683\",\n" +
                "                    \"serialNo\":1\n" +
                "                },\n" +
                "                \"serialNo\":null,\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"riskCode\":\"JBU\",\n" +
                "                \"language\":\"C\",\n" +
                "                \"insuredType\":\"1\",\n" +
                "                \"insuredCode\":\"8888888888888888\",\n" +
                "                \"insuredName\":\"测试\",\n" +
                "                \"insuredEName\":null,\n" +
                "                \"aliasName\":null,\n" +
                "                \"insuredAddress\":null,\n" +
                "                \"insuredNature\":null,\n" +
                "                \"insuredFlag\":\"100000000000000000000000000000\",\n" +
                "                \"unitType\":null,\n" +
                "                \"appendPrintName\":null,\n" +
                "                \"insuredIdentity\":\"99\",\n" +
                "                \"relateSerialNo\":null,\n" +
                "                \"identifyType\":\"01\",\n" +
                "                \"identifyNumber\":\"110101198001010010\",\n" +
                "                \"unifiedSocialCreditCode\":null,\n" +
                "                \"creditLevel\":null,\n" +
                "                \"possessNature\":null,\n" +
                "                \"businessSource\":null,\n" +
                "                \"businessSort\":null,\n" +
                "                \"occupationCode\":null,\n" +
                "                \"educationCode\":null,\n" +
                "                \"bank\":null,\n" +
                "                \"accountName\":null,\n" +
                "                \"account\":null,\n" +
                "                \"linkerName\":null,\n" +
                "                \"postAddress\":\"\",\n" +
                "                \"postCode\":null,\n" +
                "                \"phoneNumber\":null,\n" +
                "                \"faxNumber\":null,\n" +
                "                \"mobile\":\"18300000000\",\n" +
                "                \"netAddress\":null,\n" +
                "                \"email\":\"\",\n" +
                "                \"dateValid\":null,\n" +
                "                \"startDate\":\"2020-01-08 00:00:00\",\n" +
                "                \"endDate\":\"2021-01-07 00:00:00\",\n" +
                "                \"benefitFlag\":null,\n" +
                "                \"benefitRate\":null,\n" +
                "                \"drivingLicenseNo\":null,\n" +
                "                \"changelessFlag\":null,\n" +
                "                \"sex\":\"1\",\n" +
                "                \"age\":40,\n" +
                "                \"marriage\":null,\n" +
                "                \"driverAddress\":null,\n" +
                "                \"peccancy\":null,\n" +
                "                \"acceptLicenseDate\":null,\n" +
                "                \"receiveLicenseYear\":null,\n" +
                "                \"drivingYears\":null,\n" +
                "                \"causeTroubleTimes\":null,\n" +
                "                \"awardLicenseOrgan\":null,\n" +
                "                \"drivingCarType\":null,\n" +
                "                \"countryCode\":null,\n" +
                "                \"versionNo\":null,\n" +
                "                \"auditstatus\":null,\n" +
                "                \"flag\":null,\n" +
                "                \"warningFlag\":null,\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                \"blackFlag\":null,\n" +
                "                \"importSerialNo\":null,\n" +
                "                \"groupCode\":null,\n" +
                "                \"groupName\":null,\n" +
                "                \"dweller\":null,\n" +
                "                \"customerLevel\":null,\n" +
                "                \"insuredPYName\":null,\n" +
                "                \"groupNo\":null,\n" +
                "                \"itemNo\":null,\n" +
                "                \"importFlag\":null,\n" +
                "                \"smsFlag\":null,\n" +
                "                \"emailFlag\":null,\n" +
                "                \"sendPhone\":null,\n" +
                "                \"sendEmail\":null,\n" +
                "                \"subPolicyNo\":null,\n" +
                "                \"socialSecurityNo\":null,\n" +
                "                \"electronicflag\":null,\n" +
                "                \"insuredSort\":null,\n" +
                "                \"isHealthSurvey\":null,\n" +
                "                \"InsuredNatures\":[\n" +
                "                    {\n" +
                "                        \"id\":{\n" +
                "                            \"pkey\":\"TJBU202032046000005683\",\n" +
                "                            \"serialNo\":1\n" +
                "                        },\n" +
                "                        \"serialNo\":null,\n" +
                "                        \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                        \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                        \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                        \"insuredFlag\":null,\n" +
                "                        \"sex\":\"1\",\n" +
                "                        \"age\":40,\n" +
                "                        \"birthday\":\"1980-01-01\",\n" +
                "                        \"health\":null,\n" +
                "                        \"jobTitle\":null,\n" +
                "                        \"localWorkYears\":null,\n" +
                "                        \"education\":null,\n" +
                "                        \"totalWorkYears\":null,\n" +
                "                        \"unit\":null,\n" +
                "                        \"unitPhoneNumber\":null,\n" +
                "                        \"unitAddress\":null,\n" +
                "                        \"unitPostCode\":null,\n" +
                "                        \"unitType\":null,\n" +
                "                        \"dutyLevel\":null,\n" +
                "                        \"dutyType\":null,\n" +
                "                        \"occupationCode\":null,\n" +
                "                        \"houseProperty\":null,\n" +
                "                        \"localPoliceStation\":null,\n" +
                "                        \"roomAddress\":null,\n" +
                "                        \"roomPostCode\":null,\n" +
                "                        \"selfMonthIncome\":null,\n" +
                "                        \"familyMonthIncome\":null,\n" +
                "                        \"incomeSource\":null,\n" +
                "                        \"roomPhone\":null,\n" +
                "                        \"mobile\":null,\n" +
                "                        \"familySumQuantity\":null,\n" +
                "                        \"marriage\":null,\n" +
                "                        \"spouseName\":null,\n" +
                "                        \"spouseBornDate\":null,\n" +
                "                        \"spouseId\":null,\n" +
                "                        \"spouseMobile\":null,\n" +
                "                        \"spouseUnit\":null,\n" +
                "                        \"spouseJobTitle\":null,\n" +
                "                        \"spouseUnitPhone\":null,\n" +
                "                        \"flag\":null,\n" +
                "                        \"carType\":null,\n" +
                "                        \"disablePartAndLevel\":null,\n" +
                "                        \"moreLoanHouseFlag\":null,\n" +
                "                        \"nation\":null,\n" +
                "                        \"poorFlag\":null,\n" +
                "                        \"licenseNo\":null,\n" +
                "                        \"getLicenseDate\":null,\n" +
                "                        \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                        \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                        \"educationCode\":null,\n" +
                "                        \"contactNo\":null,\n" +
                "                        \"contactName\":null,\n" +
                "                        \"certificationDate\":null,\n" +
                "                        \"certificationNo\":null,\n" +
                "                        \"addressCount\":null,\n" +
                "                        \"importFlag\":null,\n" +
                "                        \"socialFlag\":null,\n" +
                "                        \"cardAmount\":null,\n" +
                "                        \"usedAmount\":null,\n" +
                "                        \"payAmount\":null,\n" +
                "                        \"isPoverty\":null,\n" +
                "                        \"importSerialNo\":null\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"InsuredArtifs\":[\n" +
                "\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\":{\n" +
                "                    \"pkey\":\"TJBU202032046000005683\",\n" +
                "                    \"serialNo\":2\n" +
                "                },\n" +
                "                \"serialNo\":null,\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"riskCode\":\"JBU\",\n" +
                "                \"language\":\"C\",\n" +
                "                \"insuredType\":\"1\",\n" +
                "                \"insuredCode\":\"8888888888888888\",\n" +
                "                \"insuredName\":\"3092285570\",\n" +
                "                \"insuredEName\":\"\",\n" +
                "                \"aliasName\":null,\n" +
                "                \"insuredAddress\":null,\n" +
                "                \"insuredNature\":null,\n" +
                "                \"insuredFlag\":\"010000000000000000000000000000\",\n" +
                "                \"unitType\":null,\n" +
                "                \"appendPrintName\":null,\n" +
                "                \"insuredIdentity\":\"0\",\n" +
                "                \"relateSerialNo\":1,\n" +
                "                \"identifyType\":\"99\",\n" +
                "                \"identifyNumber\":\"123456\",\n" +
                "                \"unifiedSocialCreditCode\":null,\n" +
                "                \"creditLevel\":null,\n" +
                "                \"possessNature\":null,\n" +
                "                \"businessSource\":null,\n" +
                "                \"businessSort\":null,\n" +
                "                \"occupationCode\":null,\n" +
                "                \"educationCode\":null,\n" +
                "                \"bank\":null,\n" +
                "                \"accountName\":null,\n" +
                "                \"account\":null,\n" +
                "                \"linkerName\":null,\n" +
                "                \"postAddress\":\"\",\n" +
                "                \"postCode\":null,\n" +
                "                \"phoneNumber\":null,\n" +
                "                \"faxNumber\":null,\n" +
                "                \"mobile\":\"15130800000\",\n" +
                "                \"netAddress\":null,\n" +
                "                \"email\":\"\",\n" +
                "                \"dateValid\":null,\n" +
                "                \"startDate\":\"2020-01-08 00:00:00\",\n" +
                "                \"endDate\":\"2021-01-07 00:00:00\",\n" +
                "                \"benefitFlag\":null,\n" +
                "                \"benefitRate\":null,\n" +
                "                \"drivingLicenseNo\":null,\n" +
                "                \"changelessFlag\":null,\n" +
                "                \"sex\":\"9\",\n" +
                "                \"age\":null,\n" +
                "                \"marriage\":null,\n" +
                "                \"driverAddress\":null,\n" +
                "                \"peccancy\":null,\n" +
                "                \"acceptLicenseDate\":null,\n" +
                "                \"receiveLicenseYear\":null,\n" +
                "                \"drivingYears\":null,\n" +
                "                \"causeTroubleTimes\":null,\n" +
                "                \"awardLicenseOrgan\":null,\n" +
                "                \"drivingCarType\":null,\n" +
                "                \"countryCode\":null,\n" +
                "                \"versionNo\":null,\n" +
                "                \"auditstatus\":null,\n" +
                "                \"flag\":null,\n" +
                "                \"warningFlag\":null,\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                \"blackFlag\":null,\n" +
                "                \"importSerialNo\":null,\n" +
                "                \"groupCode\":null,\n" +
                "                \"groupName\":null,\n" +
                "                \"dweller\":null,\n" +
                "                \"customerLevel\":null,\n" +
                "                \"insuredPYName\":null,\n" +
                "                \"groupNo\":null,\n" +
                "                \"itemNo\":null,\n" +
                "                \"importFlag\":null,\n" +
                "                \"smsFlag\":null,\n" +
                "                \"emailFlag\":null,\n" +
                "                \"sendPhone\":null,\n" +
                "                \"sendEmail\":null,\n" +
                "                \"subPolicyNo\":null,\n" +
                "                \"socialSecurityNo\":null,\n" +
                "                \"electronicflag\":null,\n" +
                "                \"insuredSort\":null,\n" +
                "                \"isHealthSurvey\":null,\n" +
                "                \"InsuredNatures\":[\n" +
                "                    {\n" +
                "                        \"id\":{\n" +
                "                            \"pkey\":\"TJBU202032046000005683\",\n" +
                "                            \"serialNo\":2\n" +
                "                        },\n" +
                "                        \"serialNo\":null,\n" +
                "                        \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                        \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                        \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                        \"insuredFlag\":null,\n" +
                "                        \"sex\":\"9\",\n" +
                "                        \"age\":null,\n" +
                "                        \"birthday\":null,\n" +
                "                        \"health\":null,\n" +
                "                        \"jobTitle\":null,\n" +
                "                        \"localWorkYears\":null,\n" +
                "                        \"education\":null,\n" +
                "                        \"totalWorkYears\":null,\n" +
                "                        \"unit\":null,\n" +
                "                        \"unitPhoneNumber\":null,\n" +
                "                        \"unitAddress\":null,\n" +
                "                        \"unitPostCode\":null,\n" +
                "                        \"unitType\":null,\n" +
                "                        \"dutyLevel\":null,\n" +
                "                        \"dutyType\":null,\n" +
                "                        \"occupationCode\":null,\n" +
                "                        \"houseProperty\":null,\n" +
                "                        \"localPoliceStation\":null,\n" +
                "                        \"roomAddress\":null,\n" +
                "                        \"roomPostCode\":null,\n" +
                "                        \"selfMonthIncome\":null,\n" +
                "                        \"familyMonthIncome\":null,\n" +
                "                        \"incomeSource\":null,\n" +
                "                        \"roomPhone\":null,\n" +
                "                        \"mobile\":null,\n" +
                "                        \"familySumQuantity\":null,\n" +
                "                        \"marriage\":null,\n" +
                "                        \"spouseName\":null,\n" +
                "                        \"spouseBornDate\":null,\n" +
                "                        \"spouseId\":null,\n" +
                "                        \"spouseMobile\":null,\n" +
                "                        \"spouseUnit\":null,\n" +
                "                        \"spouseJobTitle\":null,\n" +
                "                        \"spouseUnitPhone\":null,\n" +
                "                        \"flag\":null,\n" +
                "                        \"carType\":null,\n" +
                "                        \"disablePartAndLevel\":null,\n" +
                "                        \"moreLoanHouseFlag\":null,\n" +
                "                        \"nation\":null,\n" +
                "                        \"poorFlag\":null,\n" +
                "                        \"licenseNo\":null,\n" +
                "                        \"getLicenseDate\":null,\n" +
                "                        \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                        \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                        \"educationCode\":null,\n" +
                "                        \"contactNo\":null,\n" +
                "                        \"contactName\":null,\n" +
                "                        \"certificationDate\":null,\n" +
                "                        \"certificationNo\":null,\n" +
                "                        \"addressCount\":null,\n" +
                "                        \"importFlag\":null,\n" +
                "                        \"socialFlag\":null,\n" +
                "                        \"cardAmount\":null,\n" +
                "                        \"usedAmount\":null,\n" +
                "                        \"payAmount\":null,\n" +
                "                        \"isPoverty\":null,\n" +
                "                        \"importSerialNo\":null\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"InsuredArtifs\":[\n" +
                "\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"Coins\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"SpecialFacs\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Batches\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Commissions\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Cargos\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Plans\":[\n" +
                "            {\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"id\":{\n" +
                "                    \"pkey\":\"TJBU202032046000005683\",\n" +
                "                    \"serialNo\":1\n" +
                "                },\n" +
                "                \"endorseNo\":null,\n" +
                "                \"payNo\":1,\n" +
                "                \"payReason\":\"R21\",\n" +
                "                \"planDate\":\"2020-01-08\",\n" +
                "                \"currency\":\"CNY\",\n" +
                "                \"subsidyrate\":null,\n" +
                "                \"planFee\":20,\n" +
                "                \"delinquentFee\":20,\n" +
                "                \"flag\":null,\n" +
                "                \"payDate\":null,\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                \"payType\":null,\n" +
                "                \"exchangeNo\":null,\n" +
                "                \"paymentcomplete\":null,\n" +
                "                \"taxFee\":1.13\n" +
                "            }\n" +
                "        ],\n" +
                "        \"CoinsDetails\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Liabs\":[\n" +
                "            {\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"riskCode\":\"JBU\",\n" +
                "                \"certificateNo\":null,\n" +
                "                \"certificateDate\":null,\n" +
                "                \"certificateDepart\":null,\n" +
                "                \"practiceDate\":null,\n" +
                "                \"businessDetail\":null,\n" +
                "                \"businessSite\":\"爱保科技\",\n" +
                "                \"insureAreaCode\":null,\n" +
                "                \"insureArea\":null,\n" +
                "                \"saleArea\":null,\n" +
                "                \"officeType\":null,\n" +
                "                \"bkWardStartDate\":null,\n" +
                "                \"bkWardEndDate\":null,\n" +
                "                \"staffCount\":null,\n" +
                "                \"preTurnOver\":null,\n" +
                "                \"nowTurnOver\":null,\n" +
                "                \"electricPower\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"claimBase\":\"1\",\n" +
                "                \"flag\":null,\n" +
                "                \"guaranteeArea\":null,\n" +
                "                \"familyMembers\":null,\n" +
                "                \"goodsName\":null,\n" +
                "                \"hazardLevel\":null,\n" +
                "                \"totleCount\":null,\n" +
                "                \"quantity\":null,\n" +
                "                \"itemInfo\":null,\n" +
                "                \"disputeType\":null,\n" +
                "                \"court\":null,\n" +
                "                \"lineType\":null,\n" +
                "                \"insuredType\":null,\n" +
                "                \"isSignInsured\":null,\n" +
                "                \"collectBsae\":null,\n" +
                "                \"businessClass\":null,\n" +
                "                \"companyLevel\":null,\n" +
                "                \"companyType\":null,\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                \"pkey\":\"TJBU202032046000005683\",\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"singlePayRate\":null,\n" +
                "                \"ensureCardNo\":null\n" +
                "            }\n" +
                "        ],\n" +
                "        \"Confines\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Rations\":[\n" +
                "            {\n" +
                "                \"id\":{\n" +
                "                    \"modeCode\":\"1\",\n" +
                "                    \"pkey\":\"TJBU202032046000005683\"\n" +
                "                },\n" +
                "                \"modeName\":\"虚拟财产保险060天至060天\",\n" +
                "                \"planCode\":\"JBUYY00001\",\n" +
                "                \"serialNo\":null,\n" +
                "                \"itinerary\":null,\n" +
                "                \"sex\":null,\n" +
                "                \"age\":null,\n" +
                "                \"occupationCode\":null,\n" +
                "                \"jobTitle\":null,\n" +
                "                \"quantity\":1,\n" +
                "                \"rationCount\":1,\n" +
                "                \"groupDiscount\":null,\n" +
                "                \"insuredFlag\":null,\n" +
                "                \"countryCode\":null,\n" +
                "                \"sickRoomLevel\":null,\n" +
                "                \"journeyBack\":null,\n" +
                "                \"journeyEnd\":null,\n" +
                "                \"journeyStart\":null,\n" +
                "                \"remark\":null,\n" +
                "                \"updateFlag\":null,\n" +
                "                \"flag\":null,\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"discountType\":null,\n" +
                "                \"discountMode\":null,\n" +
                "                \"discountValue\":null,\n" +
                "                \"unitPremium\":null,\n" +
                "                \"premiumB4Discount\":null,\n" +
                "                \"premium\":null,\n" +
                "                \"planTypeCode\":null\n" +
                "            }\n" +
                "        ],\n" +
                "        \"Items\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Fees\":[\n" +
                "            {\n" +
                "                \"id\":{\n" +
                "                    \"pkey\":\"TJBU202032046000005683\",\n" +
                "                    \"currency\":\"CNY\"\n" +
                "                },\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"riskCode\":\"JBU\",\n" +
                "                \"amount\":200,\n" +
                "                \"premiumB4Discount\":null,\n" +
                "                \"premium\":20,\n" +
                "                \"flag\":\"\",\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                \"sumTaxFee\":1.13,\n" +
                "                \"sumTaxFee_ys\":0,\n" +
                "                \"sumNetPremium\":18.87,\n" +
                "                \"sumTaxFee_gb\":0,\n" +
                "                \"sumTaxFee_lb\":0\n" +
                "            }\n" +
                "        ],\n" +
                "        \"Renewals\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"CargoDetails\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Cprotocols\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Clauses\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Contributions\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"Commons\":[\n" +
                "            {\n" +
                "                \"pkey\":\"TJBU202032046000005683\",\n" +
                "                \"tkey\":\"2020-01-15 09:01:45\",\n" +
                "                \"proposalNo\":\"TJBU202032046000005683\",\n" +
                "                \"specialFlag\":\"               \",\n" +
                "                \"ext1\":null,\n" +
                "                \"ext2\":null,\n" +
                "                \"ext3\":null,\n" +
                "                \"resourceCode\":null,\n" +
                "                \"resourceName\":null,\n" +
                "                \"qualityLevel\":null,\n" +
                "                \"insertTimeForHis\":\"2020-01-15 09:02:02\",\n" +
                "                \"operateTimeForHis\":\"2020-01-15 09:02:03\",\n" +
                "                \"newBusinessNature\":\"020\",\n" +
                "                \"scmsAuditNotion\":null,\n" +
                "                \"pay_method\":null,\n" +
                "                \"platformProjectCode\":\"CPI000493\",\n" +
                "                \"handler1Code_uni\":\"1232061117\",\n" +
                "                \"handlerCode_uni\":\" \",\n" +
                "                \"commonFlag\":\"               \",\n" +
                "                \"otherPolicyName\":null,\n" +
                "                \"groupName\":null,\n" +
                "                \"isHPDriveCus\":\"0\",\n" +
                "                \"startTime\":null,\n" +
                "                \"endTime\":null,\n" +
                "                \"salesCode\":null,\n" +
                "                \"electronic\":\"0\",\n" +
                "                \"electronicTitle\":null,\n" +
                "                \"electronicPhone\":null,\n" +
                "                \"socialinsPay\":null,\n" +
                "                \"socialinsNo\":null,\n" +
                "                \"projectCode\":\"\",\n" +
                "                \"projectName\":null,\n" +
                "                \"priorityFlag\":null,\n" +
                "                \"priorityMessage\":null,\n" +
                "                \"isAccredit\":null,\n" +
                "                \"accreditType\":null,\n" +
                "                \"accreditDate\":null,\n" +
                "                \"bankFlowNo\":null,\n" +
                "                \"sealNum\":null,\n" +
                "                \"policyNo\":\"PJBU202032046000005452\",\n" +
                "                \"classify\":null,\n" +
                "                \"overSeas\":\"0\",\n" +
                "                \"isClaim\":null,\n" +
                "                \"isCondition\":null,\n" +
                "                \"unifiedInsurance\":\" \",\n" +
                "                \"electronicEmail\":null,\n" +
                "                \"isRenewalTeam\":null,\n" +
                "                \"keyAccountCode\":null,\n" +
                "                \"isRenewal\":null,\n" +
                "                \"isGIvesff\":null,\n" +
                "                \"isStatistics\":null,\n" +
                "                \"isInsureRate\":null,\n" +
                "                \"busiAccountType\":\" \",\n" +
                "                \"isPStage\":\" \",\n" +
                "                \"visaCode\":null,\n" +
                "                \"visaPrintCode\":null,\n" +
                "                \"visaNo\":null,\n" +
                "                \"isVisaCancel\":null,\n" +
                "                \"internetCode\":null,\n" +
                "                \"isPoverty\":null,\n" +
                "                \"isTargetedPoverty\":null,\n" +
                "                \"coMakecom\":null,\n" +
                "                \"coOperatorcode\":null,\n" +
                "                \"inputType\":null,\n" +
                "                \"deliverFlag\":null,\n" +
                "                \"deliverType\":null,\n" +
                "                \"addressee\":null,\n" +
                "                \"deliverTel\":null,\n" +
                "                \"deliverAddr\":null,\n" +
                "                \"isVsCard\":null,\n" +
                "                \"subinformation\":null,\n" +
                "                \"isRapidCalPremium\":null,\n" +
                "                \"externalPayFlag\":null,\n" +
                "                \"ownerFlag\":null,\n" +
                "                \"signTag\":null,\n" +
                "                \"signState\":null,\n" +
                "                \"transFlag\":null,\n" +
                "                \"invokeFlag\":null,\n" +
                "                \"invoiceCode\":null,\n" +
                "                \"reviewerName\":null,\n" +
                "                \"receivableFlag\":null,\n" +
                "                \"internationalFlag\":null,\n" +
                "                \"policyFactorFlag\":null,\n" +
                "                \"recallFlag\":null\n" +
                "            }\n" +
                "        ],\n" +
                "        \"Coupon\":null,\n" +
                "        \"InsuredCataLists\":[\n" +
                "\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        ArrayList arrayList = new ArrayList();
        getFiledName(str,arrayList);
        System.out.println(arrayList);

    }



}
