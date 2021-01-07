package com.cccc.coco.utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.scripts.JS;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class CompareUtil
{
    /**
     * 根据返回报文的节点名进行数据库表的组装，以及节点名与表名的映射关系组装
     *
     * @param value       请求或者返回报文
     * @param dbv         组装的数据库表名集合
     * @param rdbv        组装要进行对比的映射关系
     * @param table_field 组装好的映射关系
     */
    public static void getFiledName(String value, ArrayList<String> dbv, Map<Object, String> rdbv, Map<String, String> table_field)
    {
        JSONObject jsonObject = JSONObject.parseObject(value);
        JSONArray objects = new JSONArray();
        jsonObject.put("data", objects);
        Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
        boolean flag = false;
        for (Entry<String, Object> entry : entrySet) {
            for (String key : table_field.keySet()) {
                if ((entry.getKey()).equals(key)) {
                    flag = true;
                    if (entry.getValue() != null
                            && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                        dbv.add(table_field.get(key));
                        rdbv.put(table_field.get(key), key);
                    }
                    else if (entry.getValue() != null
                            && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                        dbv.add(table_field.get(key));
                        rdbv.put(table_field.get(key), key);
                    }
                    break;
                }
            }
        }
        if (flag) {
            for (Entry<String, Object> entry : entrySet) {
                if (entry.getValue() != null && entry.toString().equals("")
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                    try {
                        Object value1 = entry.getValue();
                        String s = value1.toString();
                        getFiledName(s, dbv, rdbv, table_field);
                    }
                    catch (Exception e) {
                        System.out.println("JSONArray  转化失败");
                    }
                }
                else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    try {
                        Object value1 = entry.getValue();
                        String s = value1.toString();
                        getFiledName(s, dbv, rdbv, table_field);
                    }
                    catch (Exception e) {
                        System.out.println("转化失败");
                    }

                }
            }
        }
    }

    /**
     * 从JSON数据中取出指定节点的数据
     *
     * @param fieldName 节点名
     * @param jsObject        JSON数据
     * @param resultJa  返回或者请求报文的节点数据（作为listCompare的方法参数）
     */
    public static void getJAByField(String fieldName, JSONObject jsObject, JSONArray resultJa)
    {
        //JsonObject实际上是一个map，可以通用map的方法
        Set<Entry<String, Object>> entrySet = jsObject.entrySet();
        boolean flag = false;
        for (Entry<String, Object> entry : entrySet) {
           /* 1.如果响应或者返回报文的节点名与需要比较的节点名称一致，
              2.将标志位置为true
              3.如果报文的节点值不为空，而且类型为JsonArray，得到整个数组值，循环将这些jsonObject数据放入resultJA
              4.或者报文的节点值不为空，而且类型为JsonObject，直接jsonObject放入resultJA*/
            if ((entry.getKey()).equals(fieldName)) {
                flag = true;
                if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                    JSONArray temJA = (JSONArray) entry.getValue();
                    for (int i = 0; i < temJA.size(); i++) {
                        resultJa.add(temJA.getJSONObject(i));
                    }
                }
                else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    resultJa.add((JSONObject) entry.getValue());
                }
                break;
            }
        }
        /*如果响应或者返回报文的节点名与需要比较的节点名称不一致
         * 1.如果报文的节点值不为空，而且类型为JsonArray，循环得到整个数组值，递归的思想调用自己
         * 2.或者报文的节点值不为空，而且类型为JsonObject，递归的思想调用自己*/
        if (!flag) {
            for (Entry<String, Object> entry : entrySet) {
                if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                    try {
                        JSONArray temJA = (JSONArray) entry.getValue();
                        for (int i = 0; i < temJA.size(); i++) {
                            getJAByField(fieldName, temJA.getJSONObject(i), resultJa);
                        }
                    }
                    catch (Exception e) {
                        System.out.println("JSONArray中的数据转化JsonObject失败");
                    }
                }
                else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    try {
                        getJAByField(fieldName, (JSONObject) entry.getValue(), resultJa);
                    }
                    catch (Exception e) {
                        System.out.println("JSONObject转化失败");
                    }

                }
            }
        }

    }

    /**
     * 数据库表的数据和返回报文对应接口数据校验，思路是返回报文的节点对应一张表
     *
     * @param resultJa        返回或者请求报文的节点数据
     * @param db           数据库的数据
     * @param errorMessage 错误信息
     * @param fieldName    节点名
     * @param ignoreList   存放不需要校验的字段
     */
    public static void listCompare(JSONArray resultJa, JSONArray db, StringBuffer errorMessage, String fieldName,
                                   ArrayList<String> ignoreList)
    {
        //如果没有不需要比较的字段
        if (ignoreList == null) {
            ignoreList = new ArrayList<String>();
        }
        /**
         * 这里确定ID 根据ID进行数据校验
         */
        ArrayList<String> ids = new ArrayList<String>();
        JSONObject temRespIdJs = null;
        if (resultJa != null && resultJa.size() > 0) {
            try {
                temRespIdJs = resultJa.getJSONObject(0).getJSONObject("id");
            }
            catch (Exception e) {
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
        //如果报文的条目数和数据库的条目数一样
        if (resultJa.size() == db.size()) {
            if (ids == null || ids.size() == 0) {
                for (int i = 0; i < resultJa.size(); i++) {
                    JSONObjectCompare(resultJa.getJSONObject(i), db.getJSONObject(i), errorMessage, ignoreList);
                }
            }
            else {
                for (int i = 0; i < resultJa.size(); i++) {
                    JSONObject resp_tem = resultJa.getJSONObject(i);
                    for (int j = 0; j < db.size(); j++) {
                        JSONObject db_tem = db.getJSONObject(j);
                        if (isIdentical(resp_tem, db_tem, ids)) {
                            JSONObjectCompare(resp_tem, db_tem, errorMessage, ignoreList);
                        }
                    }
                }
            }
        }
        else {
            errorMessage.append(fieldName + "接口返回的数据和数据库的数据数量不同");
        }
    }

    /**
     * 去掉数据库字段的下划线（核保微服务）
     *
     * @param JA
     * @return
     */
    public static JSONArray dealDbData(JSONArray JA)
    {
        JSONArray resultJA = new JSONArray();
        if (JA != null) {
            for (int i = 0; i < JA.size(); i++) {
                JSONObject dbJS = JA.getJSONObject(i);
                JSONObject temJS = dealData(dbJS);
                resultJA.add(temJS);
            }
        }
        return resultJA;
    }

    /**
     * 当数据库中没有这个字段，报文中有的时候，此方法可以手动给数据库中添加此字段
     *
     * @param dbv    数据库中查询出来的数据
     * @param dkey   添加的字段名
     * @param dvalue 添加的值
     * @return
     */
    public static JSONArray addDb(JSONArray dbv, String dkey, Object dvalue)
    {
        JSONObject jsonObject2 = dbv.getJSONObject(0);
        jsonObject2.put(dkey, dvalue);
        String value = String.valueOf(jsonObject2);
        value = "[" + value + "]";
        JSONArray JA = JSONArray.parseArray(value);
        return JA;
    }

    private static boolean isIdentical(JSONObject resp_tem, JSONObject db_tem, ArrayList<String> ids)
    {
        boolean flag = true;
        String resp_value = "";
        String db_value = "";
        for (String key : ids) {
            if (resp_tem.get(key) == null) {
                resp_value = resp_tem.getJSONObject("id").getString(key);
            }
            else {
                resp_value = resp_tem.getString(key);
            }
            db_value = db_tem.getString(key.toLowerCase());
            if (!resp_value.equals(db_value)) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * NUll值的处理
     *
     * @param value1
     * @return
     */
    private static String isNull(Object value1)
    {
        if (value1 == null) {
            return "";
        }
        else {
            return value1.toString();
        }
    }

    /**
     * 判断一个值是不是可以数字格式 可以判断正整型，正浮点型
     */
    private static boolean isNumber(String str)
    {
        if (str.matches("[0-9]+[.]?[0-9]*")) {
            return true;
        }
        else {
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
                                         ArrayList<String> ignoreList)
    {
        if (ignoreList == null) {
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
            }
            else if (entry.getValue() != null
                    && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")
                    && !entry.getKey().equals("id")) {
                continue;
            }
            else if (entry.getKey().equals("id")) {
                JSONObject temId = null;
                try {
                    temId = resp.getJSONObject("id");
                }
                catch (Exception e) {
                }
                if (temId == null) {
                    continue;
                }
                Set<Entry<String, Object>> entrySet_id = temId.entrySet();
                for (Entry<String, Object> ide : entrySet_id) {
                    valueCompare(temId.get(ide.getKey()), db.get(ide.getKey().toLowerCase()), errorMessage,
                            ide.getKey());
                }
            }
            else {
                valueCompare(resp.get(entry.getKey()), db.get(entry.getKey().toLowerCase()), errorMessage,
                        entry.getKey());
            }
        }
    }

    /**
     * 判断一个字段是不是时间类型,如果时分秒没有值，直接去除时分秒，如果时分秒有值，保留时分秒。
     *
     * @param value
     * @return String
     */
    private static String isDate(String value)
    {
        String result = "";
        if (value.contains(".")) {
            value = value.substring(0, value.indexOf("."));
        }
        if (value.contains("T")) {
            value = value.replace("T", " ");
        }
        if (value.contains("00:00:00")) {
            value = value.substring(0, 10);
        }
        if (value.length() == 13 && value.charAt(0) != '0' && isNumber(value) && value.charAt(1) != '.') {
            Long l = Long.parseLong(value);
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
        if (flag) {
            return result;
        }
        else {
            return value;
        }
    }


    public static JSONObject dealData(JSONObject JS)
    {
        JSONObject temJS = new JSONObject();
        Set<Entry<String, Object>> entrySet = JS.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            temJS.put(entry.getKey().replaceAll("_", ""), entry.getValue());
        }
        return temJS;
    }

    /**
     * 判断两个值是否一致
     *
     * @param resp         第一个值（一般是接口返回的值）
     * @param db           第二个值（一般是数据库存的值）
     * @param errorMessage 存放错误信息
     * @param fieldName    字段名
     */
    private static void valueCompare(Object resp, Object db, StringBuffer errorMessage, String fieldName)
    {
        System.out.println(fieldName + "开始进行校验");
        //null的处理，如果是null返回空字符串，如果不是null转换为String类型
        String respStr = isNull(resp).trim();
        String dbStr = isNull(db).trim();
        //时间类型的处理
        respStr = isDate(respStr);
        dbStr = isDate(dbStr);
        System.out.println("接口返回======" + respStr);
        System.out.println("数据库======" + dbStr);
        // 数字类型的值的比较
        if (isNumber(respStr) && isNumber(dbStr)) {
            if ((new BigDecimal(respStr)).compareTo(new BigDecimal(dbStr)) != 0) {
                errorMessage.append(fieldName + "的值数据库和接口返回的不一致 \n");
                errorMessage.append("接口===" + respStr + "\n");
                errorMessage.append("数据库===" + dbStr + "\n");
            }
        }
        else {
            if (!respStr.equals(dbStr)) {
                errorMessage.append(fieldName + "的值数据库和接口返回的不一致 \n");
                errorMessage.append("接口===" + respStr + "\n");
                errorMessage.append("数据库===" + dbStr + "\n");
            }
        }
    }


    public static void main(String[] args)
    {
        String value = "{\"status\":0,\"statusText\":\"Success\",\"data\":{\"createEndorseVo\":null,\"confirmEndorseVo\":null,\"prpCmainVo\":{\"proposalNo\":\"TEAC202033001000000004\",\"policyNo\":\"PEAC202033001000000002\",\"classCode\":\"06\",\"riskCode\":\"EAC\",\"projectCode\":null,\"contractNo\":null,\"policySort\":\"1\",\"businessNature\":\"0\",\"language\":\"C\",\"policyType\":\"03\",\"operateDate\":\"2020-12-25T08:00:00.000+0800\",\"startDate\":\"2020-12-26T08:00:00.000+0800\",\"startHour\":0,\"endDate\":\"2021-12-25T08:00:00.000+0800\",\"endHour\":24,\"pureRate\":null,\"disRate\":null,\"discount\":null,\"currency\":\"CNY\",\"sumValue\":100.00,\"sumAmount\":168948.31,\"sumDiscount\":null,\"sumPremium\":1689.48,\"sumSubPrem\":0.00,\"sumQuantity\":3,\"insuredCount\":null,\"policyCount\":null,\"judicalScope\":\"中国境内（港、澳、台除外）\",\"autoTransRenewFlag\":null,\"argueSolution\":\"1\",\"arbitBoardName\":null,\"payTimes\":1,\"makeCom\":\"33000000\",\"operateSite\":null,\"comCode\":\"33000100\",\"handlerCode\":\"01081016\",\"handler1Code\":\"33335933\",\"approverCode\":null,\"checkFlag\":\"4\",\"checkUpCode\":null,\"checkOpinion\":null,\"underWriteCode\":\"A330000185\",\"underWriteName\":\"王心冰\",\"operatorCode\":\"A330000182\",\"inputTime\":\"2020-12-25T23:20:50.000+0800\",\"underWriteEndDate\":\"2020-12-25T08:00:00.000+0800\",\"statisticsYM\":\"2020-11-30T16:00:00.000+0800\",\"agentCode\":\"000001000001\",\"coinsFlag\":\"00\",\"reinsFlag\":\"0000000000\",\"allinsFlag\":\"0\",\"underWriteFlag\":\"1\",\"othFlag\":\"000000YY00\",\"remark\":null,\"flag\":\"   0     A\",\"insertTimeForHis\":null,\"operateTimeForHis\":\"2020-12-25T23:37:53.000+0800\",\"crossFlag\":\"0\",\"printNo\":null,\"prpCmainLoanVos\":[],\"prpCmainInvestVos\":[],\"prpcmainCargoVos\":[],\"prpcmainLiabVos\":[],\"prpcmainConstructVos\":[],\"prpcmainAgriVos\":[],\"prpCnameVos\":[],\"prpCitemShipVos\":[],\"prpCitemShipYBVos\":null,\"prpCitemPlaneVos\":[],\"prpCitemVos\":[],\"prpCcargoDetailVos\":[],\"prpCmainSubVos\":[],\"prpCitemDeviceVos\":[],\"prpCitemDeviceYBVos\":null,\"prpCaddressVos\":[],\"prpCitemCarVos\":[],\"prpCitemKindVos\":[{\"proposalNo\":\"TEAC202033001000000004\",\"itemKindNo\":1,\"prpCmain\":null,\"riskCode\":\"EAC\",\"familyNo\":null,\"familyName\":null,\"projectCode\":\"not null\",\"clauseCode\":\"060036\",\"clauseName\":\"交通工具乘客意外伤害保险条款\",\"kindCode\":\"060043\",\"kindName\":\"在汽车中因意外伤害造成的身故、残疾\",\"itemNo\":1,\"itemCode\":null,\"itemDetailName\":null,\"modeCode\":null,\"modeName\":null,\"startDate\":\"2020-12-26T08:00:00.000+0800\",\"startHour\":0,\"endDate\":\"2021-12-25T08:00:00.000+0800\",\"endHour\":24,\"model\":null,\"buyDate\":null,\"addressNo\":null,\"calculateFlag\":\"1\",\"currency\":\"CAD\",\"unitAmount\":11111.00,\"quantity\":3,\"unit\":\"1\",\"value\":null,\"amount\":33333.00,\"ratePeriod\":null,\"rate\":10.00000000000,\"shortRateFlag\":\"3\",\"shortRate\":100.0000,\"basePremium\":null,\"benchMarkPremium\":null,\"discount\":31.625553,\"adjustRate\":null,\"premium\":333.33,\"prePremium\":null,\"deductibleRate\":null,\"deductible\":null,\"flag\":\" \",\"insertTimeForHis\":null,\"operateTimeForHis\":\"2020-12-25T23:35:40.000+0800\",\"groupNo\":1,\"taxFlag\":\"2\"}],\"prpCengageVos\":[{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":1,\"lineNo\":1,\"clauseCode\":\"000017\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":10,\"lineNo\":1,\"clauseCode\":\"910065\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":11,\"lineNo\":1,\"clauseCode\":\"910066\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":12,\"lineNo\":1,\"clauseCode\":\"910067\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":13,\"lineNo\":1,\"clauseCode\":\"910068\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":14,\"lineNo\":1,\"clauseCode\":\"910069\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":15,\"lineNo\":1,\"clauseCode\":\"910070\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":16,\"lineNo\":1,\"clauseCode\":\"910071\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":17,\"lineNo\":1,\"clauseCode\":\"910072\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":18,\"lineNo\":1,\"clauseCode\":\"910073\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":19,\"lineNo\":1,\"clauseCode\":\"910074\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":20,\"lineNo\":1,\"clauseCode\":\"910075\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":21,\"lineNo\":1,\"clauseCode\":\"910076\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":22,\"lineNo\":1,\"clauseCode\":\"910077\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":23,\"lineNo\":1,\"clauseCode\":\"910449\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":24,\"lineNo\":1,\"clauseCode\":\"914530\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":25,\"lineNo\":1,\"clauseCode\":\"914169\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":26,\"lineNo\":1,\"clauseCode\":\"914417\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":27,\"lineNo\":1,\"clauseCode\":\"914418\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":28,\"lineNo\":1,\"clauseCode\":\"914457\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":29,\"lineNo\":1,\"clauseCode\":\"914283\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":30,\"lineNo\":1,\"clauseCode\":\"914284\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":31,\"lineNo\":1,\"clauseCode\":\"914208\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":32,\"lineNo\":1,\"clauseCode\":\"914563\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":33,\"lineNo\":1,\"clauseCode\":\"914209\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":34,\"lineNo\":1,\"clauseCode\":\"914170\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":35,\"lineNo\":1,\"clauseCode\":\"914705\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":36,\"lineNo\":1,\"clauseCode\":\"916148\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":37,\"lineNo\":1,\"clauseCode\":\"916149\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":38,\"lineNo\":1,\"clauseCode\":\"916150\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":39,\"lineNo\":1,\"clauseCode\":\"915076\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":2,\"lineNo\":1,\"clauseCode\":\"000018\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":3,\"lineNo\":1,\"clauseCode\":\"000019\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":4,\"lineNo\":1,\"clauseCode\":\"000020\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":5,\"lineNo\":1,\"clauseCode\":\"000021\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":6,\"lineNo\":1,\"clauseCode\":\"000022\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":7,\"lineNo\":1,\"clauseCode\":\"000023\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":8,\"lineNo\":1,\"clauseCode\":\"000024\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null},{\"policyNo\":null,\"riskCode\":\"EAC\",\"serialNo\":9,\"lineNo\":1,\"clauseCode\":\"910064\",\"clauses\":null,\"flag\":null,\"remark\":null,\"validFlag\":null}],\"prpcCoinsVos\":[],\"prpCinsuredVos\":[{\"proposalNo\":\"TEAC202033001000000004\",\"serialNo\":1,\"riskCode\":\"EAC\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"10000100421803\",\"insuredName\":\"周东\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京市市辖区朝阳区人保\",\"insuredNature\":null,\"insuredFlag\":\"1\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"0\",\"relateSerialNo\":null,\"identifyType\":\"01\",\"identifyNumber\":\"410703198307072018\",\"creditLevel\":\"1\",\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":\"010101\",\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":\"北京市市辖区朝阳区人保\",\"postCode\":null,\"phoneNumber\":null,\"faxNumber\":null,\"mobile\":\"18618232407\",\"netAddress\":null,\"email\":null,\"startDate\":null,\"endDate\":null,\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"1\",\"age\":37,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":null,\"flag\":\" \",\"professionCode\":\"\",\"professionName\":\"\"},{\"proposalNo\":\"TEAC202033001000000004\",\"serialNo\":1,\"riskCode\":\"EAC\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"10000100421803\",\"insuredName\":\"周东\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京市市辖区朝阳区人保\",\"insuredNature\":null,\"insuredFlag\":\"2\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"0\",\"relateSerialNo\":null,\"identifyType\":\"01\",\"identifyNumber\":\"410703198307072018\",\"creditLevel\":\"1\",\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":\"010101\",\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":\"北京市市辖区朝阳区人保\",\"postCode\":null,\"phoneNumber\":null,\"faxNumber\":null,\"mobile\":\"18618232407\",\"netAddress\":null,\"email\":null,\"startDate\":null,\"endDate\":null,\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"1\",\"age\":37,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":null,\"flag\":\" \",\"professionCode\":\"\",\"professionName\":\"\"},{\"proposalNo\":\"TEAC202033001000000004\",\"serialNo\":2,\"riskCode\":\"EAC\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"21000001590407\",\"insuredName\":\"芈月\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京市市辖区西城区西海国际中心\",\"insuredNature\":null,\"insuredFlag\":\"1\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"0\",\"relateSerialNo\":null,\"identifyType\":\"01\",\"identifyNumber\":\"511423199006160012\",\"creditLevel\":\"1\",\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":\"000101\",\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":\"北京市市辖区西城区西海国际中心\",\"postCode\":null,\"phoneNumber\":null,\"faxNumber\":null,\"mobile\":\"13322223333\",\"netAddress\":null,\"email\":null,\"startDate\":null,\"endDate\":null,\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"2\",\"age\":30,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":null,\"flag\":null,\"professionCode\":\"\",\"professionName\":\"\"},{\"proposalNo\":\"TEAC202033001000000004\",\"serialNo\":3,\"riskCode\":\"EAC\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"10000079272205\",\"insuredName\":\"茶派\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京市市辖区昌平区回龙观一区4号楼3单元1222\",\"insuredNature\":null,\"insuredFlag\":\"1\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"0\",\"relateSerialNo\":null,\"identifyType\":\"01\",\"identifyNumber\":\"411403198211289444\",\"creditLevel\":\"1\",\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":\"000101\",\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":\"北京市市辖区昌平区回龙观一区4号楼3单元1222\",\"postCode\":\"000001\",\"phoneNumber\":null,\"faxNumber\":null,\"mobile\":\"18310924552\",\"netAddress\":null,\"email\":\"changjinxin@pt.picc.com.cn\",\"startDate\":null,\"endDate\":null,\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"2\",\"age\":38,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":null,\"flag\":null,\"professionCode\":null,\"professionName\":null}],\"prpCspecialFacVos\":[],\"prpClimitVos\":[],\"prpRepolicyVos\":[],\"prpCitemCreditOths\":[],\"prpCinsuredCreditInvestVos\":[],\"prpCmainCommonVos\":[{\"proposalNo\":\"TEAC202033001000000004\",\"prpCmain\":null,\"specialFlag\":\" 0 0  0        \",\"ext1\":\"\",\"ext2\":\"\",\"ext3\":\"\",\"resourceCode\":\"\",\"resourceName\":\"\",\"insertTimeForHis\":\"2020-12-25T23:33:01.000+0800\",\"operateTimeForHis\":\"2020-12-25T23:35:40.000+0800\",\"newBusinessNature\":\"010\",\"scmsAuditNotion\":\"\",\"pay_method\":\"\",\"platformProjectCode\":\"\",\"handler1Code_uni\":\"1294010948\",\"handlerCode_uni\":\"1294010948\",\"commonFlag\":\"0 0 0     0    \"}],\"prpCmainPropVos\":[],\"schoolName\":null,\"prpCrationVos\":null,\"prpCextendInfoVos\":null,\"appliName\":\"周东\",\"insuredName\":\"周东\",\"insuredAddress\":\"北京市市辖区朝阳区人保\",\"appliCode\":\"10000100421803\",\"insuredCode\":\"10000100421803\",\"endorseTimes\":1,\"appliAddress\":\"北京市市辖区朝阳区人保\",\"newBusinessNature\":\"010\",\"claimTimes\":null},\"prpReRiskVos\":null,\"prpCnameVos\":null,\"reduceEndorVo\":null,\"policySummaryVos\":[],\"endorseInfo\":null,\"endorsePtextCondition\":null,\"endorsePTextVo\":null,\"prpCinsuredVos\":null,\"prpCextendInfoVos\":null,\"endorsePuwEndorVo\":null,\"payPlanVos\":[],\"prpCfeeVos\":[],\"prpCitemKindVos\":null,\"contractInfoVo\":null,\"policyNo\":null,\"bjHepPolicyVo\":null,\"prpCitemCarVos\":null,\"prpCitemShipVos\":null,\"insuredIdvListCondition\":null,\"prpCinsuredIdvListVos\":null,\"status\":\"\",\"message\":\"\",\"mainPolicyNo\":null}}";
        JSONObject jsonObject1 = JSONObject.parseObject(value).getJSONObject("data");
        String s = JSONObject.toJSONString(jsonObject1);
        //System.out.println(s);
        ArrayList strings = new ArrayList();
        HashMap stringObjectHashMap = new HashMap();
        stringObjectHashMap.put("prpCmainVo","prpcopymain");
        stringObjectHashMap.put("prpCitemKindVos","prpcopyitemkind");
        HashMap table_field = new HashMap();
        getFiledName(s,strings,table_field,stringObjectHashMap);
        System.out.println(strings);
        System.out.println(table_field);

    }
}

