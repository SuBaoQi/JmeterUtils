package com.cccc.coco.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
        if (!flag) {
            for (Entry<String, Object> entry : entrySet) {
                if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONArray")) {
                    try {
                        String value1 = (String) entry.getValue();
                        getFiledName(value1, dbv, rdbv, table_field);
                    }
                    catch (Exception e) {
                        System.out.println("JSONArray  转化失败");
                    }
                }
                else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    try {
                        String value1 = (String) entry.getValue();
                        getFiledName(value1, dbv, rdbv, table_field);
                    }
                    catch (Exception e) {
                        System.out.println("转化失败");
                    }

                }
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
    public static void getJAByField(String fieldName, JSONObject JS, JSONArray resultJA)
    {
        //JsonObject实际上是一个map，可以通用map的方法
        Set<Entry<String, Object>> entrySet = JS.entrySet();
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
                        resultJA.add(temJA.getJSONObject(i));
                    }
                }
                else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    resultJA.add((JSONObject) entry.getValue());
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
                            getJAByField(fieldName, temJA.getJSONObject(i), resultJA);
                        }
                    }
                    catch (Exception e) {
                        System.out.println("JSONArray中的数据转化JsonObject失败");
                    }
                }
                else if (entry.getValue() != null
                        && entry.getValue().getClass().toString().equals("class com.alibaba.fastjson.JSONObject")) {
                    try {
                        getJAByField(fieldName, (JSONObject) entry.getValue(), resultJA);
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
     * @param resps        返回或者请求报文的节点数据
     * @param db           数据库的数据
     * @param errorMessage 错误信息
     * @param fieldName    节点名
     * @param ignoreList   存放不需要校验的字段
     */
    public static void listCompare(JSONArray resps, JSONArray db, StringBuffer errorMessage, String fieldName,
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
        if (resps != null && resps.size() > 0) {
            try {
                temRespIdJs = resps.getJSONObject(0).getJSONObject("id");
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
        if (resps.size() == db.size()) {
            if (ids == null || ids.size() == 0) {
                for (int i = 0; i < resps.size(); i++) {
                    JSONObjectCompare(resps.getJSONObject(i), db.getJSONObject(i), errorMessage, ignoreList);
                }
            }
            else {
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
        String value = "{\n" +
                "    \"status\": 0,\n" +
                "    \"statusText\": \"Success\",\n" +
                "    \"data\": {\n" +
                "        \"status\": \"0\",\n" +
                "        \"message\": null,\n" +
                "        \"data\": [\n" +
                "            {\n" +
                "                \"endorseNoBJS\": \"ETF0100220000000157\",\n" +
                "                \"endorseNo\": \"EJCO202114001000J00023\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        JSONObject jsonObject = JSONObject.parseObject(value);
        String data = jsonObject.getJSONObject("data").getJSONArray("data").getJSONObject(0).get("endorseNo").toString();
        System.out.println(data);
    }
}

