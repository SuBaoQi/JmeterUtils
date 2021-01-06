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
        StringBuilder sb = new StringBuilder();
        sb.append("{\n" +
                "    \"status\": 0,\n" +
                "    \"statusText\": \"Success\",\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": {\n" +
                "                \"pkey\": \"PYDL201932045000000277\",\n" +
                "                \"applyNo\": \"TYDL201932045000000396\"\n" +
                "            },\n" +
                "            \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "            \"policyNo\": \"PYDL201932045000000277\",\n" +
                "            \"classCode\": \"09\",\n" +
                "            \"riskCode\": \"YDL\",\n" +
                "            \"projectCode\": \"\",\n" +
                "            \"projectName\": null,\n" +
                "            \"contractNo\": \"合同号\",\n" +
                "            \"policySort\": \"1\",\n" +
                "            \"businessNature\": \"1\",\n" +
                "            \"language\": \"C\",\n" +
                "            \"policyType\": \"19\",\n" +
                "            \"agriFlag\": \"0\",\n" +
                "            \"operateDate\": \"2019-09-11 16:09:46\",\n" +
                "            \"startDate\": \"2019-09-12 00:00:00\",\n" +
                "            \"endDate\": \"2020-09-07 00:00:00\",\n" +
                "            \"startHour\": 0,\n" +
                "            \"endHour\": 24,\n" +
                "            \"sumValue\": 0.00,\n" +
                "            \"sumAmount\": 1000.00,\n" +
                "            \"sumDiscount\": null,\n" +
                "            \"sumPremium\": 0.80,\n" +
                "            \"sumSubPrem\": 0.00,\n" +
                "            \"sumQuantity\": 0,\n" +
                "            \"policyCount\": null,\n" +
                "            \"judicalScope\": \"01\",\n" +
                "            \"argueSolution\": \"1\",\n" +
                "            \"arbitBoardName\": \"\",\n" +
                "            \"payTimes\": 1,\n" +
                "            \"makeCom\": \"32048200\",\n" +
                "            \"operateSite\": \"中国\",\n" +
                "            \"comCode\": \"32048200\",\n" +
                "            \"handlerCode\": \"83242824\",\n" +
                "            \"handler1Code\": \"83242824\",\n" +
                "            \"prePremium\": null,\n" +
                "            \"checkFlag\": \"4\",\n" +
                "            \"checkUpCode\": \"\",\n" +
                "            \"checkOpinion\": \"\",\n" +
                "            \"underWriteCode\": \"UnderWrite\",\n" +
                "            \"underWriteName\": \"自动核保\",\n" +
                "            \"operatorCode\": \"A320000058\",\n" +
                "            \"inputTime\": \"2019-09-11 16:09:07\",\n" +
                "            \"underWriteEndDate\": \"2019-09-11\",\n" +
                "            \"statisticsYM\": \"201909\",\n" +
                "            \"agentCode\": \"000011000001\",\n" +
                "            \"coinsFlag\": \"11\",\n" +
                "            \"crossFlag\": \"0\",\n" +
                "            \"reinsFlag\": \"0000099000\",\n" +
                "            \"isReins\": \"0\",\n" +
                "            \"allinsFlag\": \"0\",\n" +
                "            \"checkCode\": \"\",\n" +
                "            \"underWriteFlag\": \"3\",\n" +
                "            \"jfeeFlag\": \"0\",\n" +
                "            \"payMode\": \"2\",\n" +
                "            \"payCode\": null,\n" +
                "            \"othFlag\": \"000000YY00\",\n" +
                "            \"inputFlag\": \"\",\n" +
                "            \"remark\": \"\",\n" +
                "            \"flag\": \"0   7\",\n" +
                "            \"endorseNo\": \"0\",\n" +
                "            \"validDate\": \"2020-10-15\",\n" +
                "            \"validHour\": null,\n" +
                "            \"endorseTimes\": 0,\n" +
                "            \"pureRate\": null,\n" +
                "            \"discount\": null,\n" +
                "            \"disRate\": null,\n" +
                "            \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "            \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "            \"batchGroupNo\": null,\n" +
                "            \"projectCodeZ\": null,\n" +
                "            \"sumTaxFee\": 0.02,\n" +
                "            \"sumNetPremium\": 0.78,\n" +
                "            \"inputSumPremium\": null,\n" +
                "            \"dutySumNetPremium\": null,\n" +
                "            \"freeSumNetPremium\": null,\n" +
                "            \"orderNo\": null,\n" +
                "            \"orderFlag\": null,\n" +
                "            \"prpCopyMainAccs\": [],\n" +
                "            \"prpCopyMainExts\": [],\n" +
                "            \"prpCopyMainBonds\": [],\n" +
                "            \"prpCopyItems\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"PYDL201932045000000277\",\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"itemNo\": 1\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"itemCode\": \"852050\",\n" +
                "                    \"itemName\": \"旅行用品、手提包及类似容器\",\n" +
                "                    \"plusRate\": 0.000000,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"flag\": null,\n" +
                "                    \"itemInfo\": null,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"prpCopyItemPlanes\": [],\n" +
                "                    \"prpCopyItemDevices\": [],\n" +
                "                    \"prpCopyItemProps\": [],\n" +
                "                    \"prpCopyItemHouses\": [],\n" +
                "                    \"prpCopyItemCars\": [],\n" +
                "                    \"prpCopyItemCargos\": [],\n" +
                "                    \"prpCopyItemShips\": [\n" +
                "                        {\n" +
                "                            \"id\": {\n" +
                "                                \"pkey\": \"PYDL201932045000000277\",\n" +
                "                                \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                                \"itemNo\": 1\n" +
                "                            },\n" +
                "                            \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                            \"riskCode\": \"YDL\",\n" +
                "                            \"fleetNo\": \"\",\n" +
                "                            \"ditemno\": null,\n" +
                "                            \"shipCode\": \"\",\n" +
                "                            \"shipCName\": \"\",\n" +
                "                            \"shipEName\": \"\",\n" +
                "                            \"oldShipName\": null,\n" +
                "                            \"shipOwner\": null,\n" +
                "                            \"oldShipOwner\": null,\n" +
                "                            \"conveyManager\": null,\n" +
                "                            \"associate\": \"\",\n" +
                "                            \"makeYearMonth\": \"20190823\",\n" +
                "                            \"countryCode\": \"\",\n" +
                "                            \"makeFactory\": \"\",\n" +
                "                            \"makeDock\": \"2019-05-31\",\n" +
                "                            \"shipWayType\": null,\n" +
                "                            \"applyCriterion\": null,\n" +
                "                            \"makeContractNo\": null,\n" +
                "                            \"makeStartDate\": null,\n" +
                "                            \"makeEndDate\": null,\n" +
                "                            \"preBuildCyc\": null,\n" +
                "                            \"stepHull\": \"无船级\",\n" +
                "                            \"oldStepHull\": null,\n" +
                "                            \"shipFlag\": \"\",\n" +
                "                            \"shipTypeCode\": null,\n" +
                "                            \"useNatureCode\": null,\n" +
                "                            \"shipUsage\": null,\n" +
                "                            \"shipStruct\": null,\n" +
                "                            \"registrySite\": null,\n" +
                "                            \"tonCount\": null,\n" +
                "                            \"netTonCount\": null,\n" +
                "                            \"horsePower\": null,\n" +
                "                            \"powerUnit\": null,\n" +
                "                            \"seatCount\": null,\n" +
                "                            \"loadTon\": null,\n" +
                "                            \"shipLength\": null,\n" +
                "                            \"shipWidth\": null,\n" +
                "                            \"shipDepth\": null,\n" +
                "                            \"trySailPeriod\": null,\n" +
                "                            \"trySailArea\": null,\n" +
                "                            \"shipPort\": null,\n" +
                "                            \"launchDate\": null,\n" +
                "                            \"sailAreaCode\": null,\n" +
                "                            \"sailAreaName\": null,\n" +
                "                            \"sailScope\": null,\n" +
                "                            \"sailModeCode\": null,\n" +
                "                            \"voyage\": null,\n" +
                "                            \"shipValue\": null,\n" +
                "                            \"currency\": null,\n" +
                "                            \"suspendStartDate\": null,\n" +
                "                            \"suspendEndDate\": null,\n" +
                "                            \"mortgageName\": null,\n" +
                "                            \"insurerShipRelation\": null,\n" +
                "                            \"shipCallSign\": null,\n" +
                "                            \"imo\": null,\n" +
                "                            \"shipManagerAddress\": null,\n" +
                "                            \"reconstructionYear\": null,\n" +
                "                            \"shipAssociation\": null,\n" +
                "                            \"fleetTotalScale\": null,\n" +
                "                            \"fleetPICCScale\": null,\n" +
                "                            \"shipConstractionAdrdress\": null,\n" +
                "                            \"launchType\": null,\n" +
                "                            \"govAgencyForShip\": null,\n" +
                "                            \"licStartDateForShip\": null,\n" +
                "                            \"licEndDateForShip\": null,\n" +
                "                            \"govAgencyForHull\": null,\n" +
                "                            \"licStartDateForHull\": null,\n" +
                "                            \"licEndDateForHull\": null,\n" +
                "                            \"govAgencyForTempHull\": null,\n" +
                "                            \"licStartDateForTempHull\": null,\n" +
                "                            \"licenceEndDateForTempHull\": null,\n" +
                "                            \"govAgencyForMachinery\": null,\n" +
                "                            \"licStartDateForMachinery\": null,\n" +
                "                            \"licEndDateForMachinery\": null,\n" +
                "                            \"govAgencyForTempMachinery\": null,\n" +
                "                            \"licStartDateForTempMachinery\": null,\n" +
                "                            \"licEndDateForTempMachinery\": null,\n" +
                "                            \"isLicForIntTon\": null,\n" +
                "                            \"licStartDateForIntTon\": null,\n" +
                "                            \"licEndDateForIntTon\": null,\n" +
                "                            \"isLicForIntLoad\": null,\n" +
                "                            \"licStartDateForIntLoad\": null,\n" +
                "                            \"licEndDateForIntLoad\": null,\n" +
                "                            \"isLicForConstruction\": null,\n" +
                "                            \"licStartDateForConstruction\": null,\n" +
                "                            \"licEndDateForConstruction\": null,\n" +
                "                            \"isLicForEquipment\": null,\n" +
                "                            \"licStartDateForEquipment\": null,\n" +
                "                            \"licEndDateForEquipment\": null,\n" +
                "                            \"isLicForWireLess\": null,\n" +
                "                            \"licStartDateForWireLess\": null,\n" +
                "                            \"licEndDateForWireLess\": null,\n" +
                "                            \"isLicForLift\": null,\n" +
                "                            \"licStartDateForLift\": null,\n" +
                "                            \"licEndDateForLift\": null,\n" +
                "                            \"isLicForFitness\": null,\n" +
                "                            \"licStartDateForFitness\": null,\n" +
                "                            \"licEndDateForFitness\": null,\n" +
                "                            \"isLicForIOPP\": null,\n" +
                "                            \"licStartDateForIOPP\": null,\n" +
                "                            \"licEndDateForIOPP\": null,\n" +
                "                            \"isLicForNavigation\": null,\n" +
                "                            \"licStartDateForNavigation\": null,\n" +
                "                            \"licEndDateForNavigation\": null,\n" +
                "                            \"isLicForSMC\": null,\n" +
                "                            \"licStartDateForSMC\": null,\n" +
                "                            \"licEndDateForSMC\": null,\n" +
                "                            \"isLicForConform\": null,\n" +
                "                            \"licStartDateForConform\": null,\n" +
                "                            \"licEndDateForConform\": null,\n" +
                "                            \"isLicForMSM\": null,\n" +
                "                            \"licStartDateForMSM\": null,\n" +
                "                            \"licEndDateForMSM\": null,\n" +
                "                            \"makeDockAddress\": null,\n" +
                "                            \"projectForBuilder\": null,\n" +
                "                            \"constractNoForBuilder\": null,\n" +
                "                            \"amountForBuilder\": null,\n" +
                "                            \"addressForBuilder\": null,\n" +
                "                            \"projectForSub\": null,\n" +
                "                            \"constractNoForSub\": null,\n" +
                "                            \"addressForSub\": null,\n" +
                "                            \"historyForBuilder\": null,\n" +
                "                            \"lastPrice\": null,\n" +
                "                            \"deliveryPlace\": null,\n" +
                "                            \"beginDate\": null,\n" +
                "                            \"stageDate\": null,\n" +
                "                            \"waterDate\": null,\n" +
                "                            \"sailDate\": null,\n" +
                "                            \"deliveryDate\": null,\n" +
                "                            \"insuredStatus\": null,\n" +
                "                            \"actualShipOwner\": null,\n" +
                "                            \"loadType\": null,\n" +
                "                            \"loadStyle\": null,\n" +
                "                            \"shipRegisterOwner\": null,\n" +
                "                            \"shipRegisterPlace\": null,\n" +
                "                            \"newContractPrice\": null,\n" +
                "                            \"buyerName\": null,\n" +
                "                            \"waybillNumber\": null,\n" +
                "                            \"sellerName\": null,\n" +
                "                            \"onlinePurchaseOrderNumber\": null,\n" +
                "                            \"transportMachine\": null,\n" +
                "                            \"destination\": null,\n" +
                "                            \"insured\": null,\n" +
                "                            \"departurePlace\": null,\n" +
                "                            \"quantity\": null,\n" +
                "                            \"boxSize\": null,\n" +
                "                            \"usage\": null,\n" +
                "                            \"boxNumber\": null,\n" +
                "                            \"carTypre\": null,\n" +
                "                            \"boxType\": null,\n" +
                "                            \"ownFlag\": null,\n" +
                "                            \"deviceNo\": null,\n" +
                "                            \"remark\": null,\n" +
                "                            \"flag\": null,\n" +
                "                            \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                            \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                            \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                            \"voyageNo\": null,\n" +
                "                            \"registryPort\": null,\n" +
                "                            \"shipYears\": null,\n" +
                "                            \"landAgent\": null,\n" +
                "                            \"shipLeval\": null,\n" +
                "                            \"route\": null,\n" +
                "                            \"aquageGrade\": null,\n" +
                "                            \"importFlag\": null,\n" +
                "                            \"engineNo\": null,\n" +
                "                            \"vinNo\": null,\n" +
                "                            \"certificateNo\": null\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"prpCopyItemConstructs\": [],\n" +
                "                    \"prpCopyItemTravelAgencys\": []\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainSubs\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"mainPolicyNo\": \"PYAE201932040000000042\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"mainProposalNo\": \"TYAE201932040000000056\",\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": \"\",\n" +
                "                    \"cargoSerialNo\": \"\",\n" +
                "                    \"balanceTimes\": null,\n" +
                "                    \"balanceFlag\": \"0\",\n" +
                "                    \"payFlag\": \"1\",\n" +
                "                    \"flag\": \"NY\",\n" +
                "                    \"auditNo\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyCommissions\": [],\n" +
                "            \"prpCopyCommissionDetails\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"agreementNo\": \"RULE20173204000000001         \",\n" +
                "                    \"customerGroupCode\": \"1\",\n" +
                "                    \"costType\": \"01\",\n" +
                "                    \"payNo\": 1,\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"clauseCode\": \"PUB   \",\n" +
                "                    \"kindCode\": \"PUB   \",\n" +
                "                    \"sumPremium\": 0.78,\n" +
                "                    \"costRate\": 20.00,\n" +
                "                    \"costRateUpper\": 20.00,\n" +
                "                    \"adjustFlag\": \"1\",\n" +
                "                    \"upperFlag\": \"1\",\n" +
                "                    \"auditRate\": null,\n" +
                "                    \"auditFlag\": \"1\",\n" +
                "                    \"coinsRate\": 50.00000000,\n" +
                "                    \"coinsDeduct\": \"\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"costFee\": 780.00,\n" +
                "                    \"configCode\": \"1       \",\n" +
                "                    \"amortizeFlag\": \" \",\n" +
                "                    \"clauseKindFlag\": \"      \",\n" +
                "                    \"remark\": null,\n" +
                "                    \"flag\": \"        \",\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"levelRate\": 0.00,\n" +
                "                    \"levelMaxRate\": 0.00,\n" +
                "                    \"isNetFlag\": \"1\",\n" +
                "                    \"queryNo\": null,\n" +
                "                    \"xsfyRateUpper\": null,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"isAmortizeFlag\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyEngages\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"clauseCode\": \"LF002\",\n" +
                "                    \"clauseName\": \"被保险人名称\",\n" +
                "                    \"clauses\": \"浙江省货运险外部客户\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"relatedFlag\": null,\n" +
                "                    \"relatedContent\": null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"clauseCode\": \"T0001\",\n" +
                "                    \"clauseName\": \"特别约定\",\n" +
                "                    \"clauses\": \"特别约定\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"relatedFlag\": null,\n" +
                "                    \"relatedContent\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainAgris\": [],\n" +
                "            \"prpCopyBatchs\": [],\n" +
                "            \"prpCopyCoeffs\": [],\n" +
                "            \"prpCopyMainCargos\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"PYDL201932045000000277\",\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"priceCondition\": null,\n" +
                "                    \"ladingNo\": \"\",\n" +
                "                    \"invoiceNo\": \"\",\n" +
                "                    \"invoiceCurrency\": \"CNY\",\n" +
                "                    \"invoiceAmount\": 1000.00,\n" +
                "                    \"plusRate\": 0.000000,\n" +
                "                    \"creditNo\": \"000000\",\n" +
                "                    \"shipNoteNo\": null,\n" +
                "                    \"bargainNo\": null,\n" +
                "                    \"conveyance\": \"\",\n" +
                "                    \"conveyancePlace\": null,\n" +
                "                    \"blName\": \"\",\n" +
                "                    \"carryBillNo\": \"\",\n" +
                "                    \"transferConveyance\": null,\n" +
                "                    \"blNo\": \"dfgh\",\n" +
                "                    \"voyageNo\": \"dfgh\",\n" +
                "                    \"preserveInfo\": null,\n" +
                "                    \"tonCount\": null,\n" +
                "                    \"startSiteCode\": null,\n" +
                "                    \"startSiteName\": \"HGD\",\n" +
                "                    \"viaSiteCode\": null,\n" +
                "                    \"viaSiteName\": \"\",\n" +
                "                    \"reshipSiteName\": null,\n" +
                "                    \"endSiteCode\": null,\n" +
                "                    \"endSiteName\": \"FGH\",\n" +
                "                    \"endDetailName\": null,\n" +
                "                    \"checkAgentCode\": null,\n" +
                "                    \"claimSite\": \"\",\n" +
                "                    \"transferBank\": null,\n" +
                "                    \"originalCount\": 2,\n" +
                "                    \"goodsAmount\": null,\n" +
                "                    \"trailerPlate\": null,\n" +
                "                    \"temperatureControl\": null,\n" +
                "                    \"transportType\": \"Y06\",\n" +
                "                    \"carCount\": null,\n" +
                "                    \"machineType\": null,\n" +
                "                    \"registryPort\": null,\n" +
                "                    \"businessScope\": null,\n" +
                "                    \"shipYears\": null,\n" +
                "                    \"engineNumber\": null,\n" +
                "                    \"routeLengthUnit\": null,\n" +
                "                    \"goodsName\": \"23r23f\",\n" +
                "                    \"routeLength\": null,\n" +
                "                    \"startPost\": null,\n" +
                "                    \"endPost\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"recipientName\": null,\n" +
                "                    \"departureTime\": \"2019-09-11\",\n" +
                "                    \"transportScope\": null,\n" +
                "                    \"commitmentArrivalTime\": null,\n" +
                "                    \"distance\": null,\n" +
                "                    \"commitmentPeriod\": null,\n" +
                "                    \"preInsureFlag\": null,\n" +
                "                    \"businessDetail\": null,\n" +
                "                    \"landAgent\": \"\",\n" +
                "                    \"protectedClub\": null,\n" +
                "                    \"checkedTone\": null,\n" +
                "                    \"income\": null,\n" +
                "                    \"quantity\": null,\n" +
                "                    \"shipLeval\": \"无船级\",\n" +
                "                    \"balanceFlag\": \"0\",\n" +
                "                    \"cargoserialNo\": null,\n" +
                "                    \"mainPolicyNo\": null,\n" +
                "                    \"payFlag\": \"1\",\n" +
                "                    \"remark\": \"\",\n" +
                "                    \"flag\": null,\n" +
                "                    \"carKindCode\": null,\n" +
                "                    \"carQuantity\": null,\n" +
                "                    \"busCount\": null,\n" +
                "                    \"truckCount\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"shipNo\": null,\n" +
                "                    \"buildYear\": null,\n" +
                "                    \"preSigns\": null,\n" +
                "                    \"loadTranSport\": null,\n" +
                "                    \"vinNo\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainCredits\": [],\n" +
                "            \"prpCopyAddresses\": [],\n" +
                "            \"prpCopyMainConstructs\": [],\n" +
                "            \"prpCopyInsureds\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"language\": \"C\",\n" +
                "                    \"insuredType\": \"1\",\n" +
                "                    \"insuredCode\": \"3200100000025700\",\n" +
                "                    \"insuredName\": \"海贼\",\n" +
                "                    \"insuredEName\": \"\",\n" +
                "                    \"aliasName\": \"\",\n" +
                "                    \"insuredAddress\": \"黑龙江省哈尔滨市五常市黑龙江五常市\",\n" +
                "                    \"insuredNature\": \"\",\n" +
                "                    \"insuredFlag\": \"01000000000000000000000000000A\",\n" +
                "                    \"unitType\": \"\",\n" +
                "                    \"appendPrintName\": \"\",\n" +
                "                    \"insuredIdentity\": \"\",\n" +
                "                    \"relateSerialNo\": null,\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"identifyNumber\": \"130283198609068214\",\n" +
                "                    \"unifiedSocialCreditCode\": null,\n" +
                "                    \"creditLevel\": \"\",\n" +
                "                    \"possessNature\": null,\n" +
                "                    \"businessSource\": null,\n" +
                "                    \"businessName\": null,\n" +
                "                    \"businessSort\": null,\n" +
                "                    \"occupationCode\": null,\n" +
                "                    \"occupationName\": null,\n" +
                "                    \"educationCode\": null,\n" +
                "                    \"bank\": null,\n" +
                "                    \"accountName\": null,\n" +
                "                    \"account\": null,\n" +
                "                    \"linkerName\": \"海贼\",\n" +
                "                    \"postAddress\": \"黑龙江省哈尔滨市五常市黑龙江五常市\",\n" +
                "                    \"postCode\": \"\",\n" +
                "                    \"postName\": null,\n" +
                "                    \"phoneNumber\": \"\",\n" +
                "                    \"faxNumber\": \"\",\n" +
                "                    \"mobile\": \"13233322222\",\n" +
                "                    \"netAddress\": \"\",\n" +
                "                    \"email\": \"\",\n" +
                "                    \"dateValid\": null,\n" +
                "                    \"startDate\": \"2019-09-12 00:00:00\",\n" +
                "                    \"endDate\": \"2020-09-10 00:00:00\",\n" +
                "                    \"benefitFlag\": null,\n" +
                "                    \"benefitRate\": null,\n" +
                "                    \"drivingLicenseNo\": null,\n" +
                "                    \"changelessFlag\": null,\n" +
                "                    \"sex\": null,\n" +
                "                    \"age\": null,\n" +
                "                    \"marriage\": null,\n" +
                "                    \"driverAddress\": null,\n" +
                "                    \"peccancy\": null,\n" +
                "                    \"acceptLicenseDate\": null,\n" +
                "                    \"receiveLicenseYear\": null,\n" +
                "                    \"drivingYears\": null,\n" +
                "                    \"causeTroubleTimes\": null,\n" +
                "                    \"awardLicenseOrgan\": null,\n" +
                "                    \"drivingCarType\": null,\n" +
                "                    \"countryCode\": \"\",\n" +
                "                    \"flag\": null,\n" +
                "                    \"warningFlag\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"blackflag\": null,\n" +
                "                    \"insuredSort\": null,\n" +
                "                    \"prpCopyInsuredNatures\": [],\n" +
                "                    \"prpCopyInsuredArtifs\": [],\n" +
                "                    \"prpCopyInsuredCreditInvests\": [],\n" +
                "                    \"groupCode\": null,\n" +
                "                    \"groupName\": null,\n" +
                "                    \"dweller\": null,\n" +
                "                    \"customerLevel\": null,\n" +
                "                    \"insuredPYName\": null,\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"versionNo\": null,\n" +
                "                    \"itemNo\": null,\n" +
                "                    \"importFlag\": null,\n" +
                "                    \"smsFlag\": null,\n" +
                "                    \"emailFlag\": null,\n" +
                "                    \"sendPhone\": null,\n" +
                "                    \"sendEmail\": null,\n" +
                "                    \"subPolicyNo\": null,\n" +
                "                    \"importSerialNo\": null,\n" +
                "                    \"socialSecurityNo\": null,\n" +
                "                    \"electronicflag\": null,\n" +
                "                    \"isHealthSurvey\": null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"language\": \"C\",\n" +
                "                    \"insuredType\": \"1\",\n" +
                "                    \"insuredCode\": \"3200100000025700\",\n" +
                "                    \"insuredName\": \"海贼\",\n" +
                "                    \"insuredEName\": \"\",\n" +
                "                    \"aliasName\": \"\",\n" +
                "                    \"insuredAddress\": \"黑龙江省哈尔滨市五常市黑龙江五常市\",\n" +
                "                    \"insuredNature\": \"\",\n" +
                "                    \"insuredFlag\": \"11000000000000000000000000000A\",\n" +
                "                    \"unitType\": \"\",\n" +
                "                    \"appendPrintName\": \"\",\n" +
                "                    \"insuredIdentity\": \"\",\n" +
                "                    \"relateSerialNo\": null,\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"identifyNumber\": \"130283198609068214\",\n" +
                "                    \"unifiedSocialCreditCode\": null,\n" +
                "                    \"creditLevel\": \"\",\n" +
                "                    \"possessNature\": null,\n" +
                "                    \"businessSource\": null,\n" +
                "                    \"businessName\": null,\n" +
                "                    \"businessSort\": null,\n" +
                "                    \"occupationCode\": null,\n" +
                "                    \"occupationName\": null,\n" +
                "                    \"educationCode\": null,\n" +
                "                    \"bank\": null,\n" +
                "                    \"accountName\": null,\n" +
                "                    \"account\": null,\n" +
                "                    \"linkerName\": \"海贼\",\n" +
                "                    \"postAddress\": \"黑龙江省哈尔滨市五常市黑龙江五常市\",\n" +
                "                    \"postCode\": \"\",\n" +
                "                    \"postName\": null,\n" +
                "                    \"phoneNumber\": \"\",\n" +
                "                    \"faxNumber\": \"\",\n" +
                "                    \"mobile\": \"13233322222\",\n" +
                "                    \"netAddress\": \"\",\n" +
                "                    \"email\": \"\",\n" +
                "                    \"dateValid\": null,\n" +
                "                    \"startDate\": \"2019-09-12 00:00:00\",\n" +
                "                    \"endDate\": \"2020-09-10 00:00:00\",\n" +
                "                    \"benefitFlag\": null,\n" +
                "                    \"benefitRate\": null,\n" +
                "                    \"drivingLicenseNo\": null,\n" +
                "                    \"changelessFlag\": null,\n" +
                "                    \"sex\": null,\n" +
                "                    \"age\": null,\n" +
                "                    \"marriage\": null,\n" +
                "                    \"driverAddress\": null,\n" +
                "                    \"peccancy\": null,\n" +
                "                    \"acceptLicenseDate\": null,\n" +
                "                    \"receiveLicenseYear\": null,\n" +
                "                    \"drivingYears\": null,\n" +
                "                    \"causeTroubleTimes\": null,\n" +
                "                    \"awardLicenseOrgan\": null,\n" +
                "                    \"drivingCarType\": null,\n" +
                "                    \"countryCode\": \"\",\n" +
                "                    \"flag\": null,\n" +
                "                    \"warningFlag\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"blackflag\": null,\n" +
                "                    \"insuredSort\": null,\n" +
                "                    \"prpCopyInsuredNatures\": [],\n" +
                "                    \"prpCopyInsuredArtifs\": [],\n" +
                "                    \"prpCopyInsuredCreditInvests\": [],\n" +
                "                    \"groupCode\": null,\n" +
                "                    \"groupName\": null,\n" +
                "                    \"dweller\": null,\n" +
                "                    \"customerLevel\": null,\n" +
                "                    \"insuredPYName\": null,\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"versionNo\": null,\n" +
                "                    \"itemNo\": null,\n" +
                "                    \"importFlag\": null,\n" +
                "                    \"smsFlag\": null,\n" +
                "                    \"emailFlag\": null,\n" +
                "                    \"sendPhone\": null,\n" +
                "                    \"sendEmail\": null,\n" +
                "                    \"subPolicyNo\": null,\n" +
                "                    \"importSerialNo\": null,\n" +
                "                    \"socialSecurityNo\": null,\n" +
                "                    \"electronicflag\": null,\n" +
                "                    \"isHealthSurvey\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainInvests\": [],\n" +
                "            \"prpCopyNames\": [],\n" +
                "            \"prpCopyItemKinds\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"PYDL201932045000000277\",\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"itemKindNo\": 1\n" +
                "                    },\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"familyNo\": null,\n" +
                "                    \"familyName\": \"\",\n" +
                "                    \"projectCode\": \"\",\n" +
                "                    \"clauseCode\": \"090006\",\n" +
                "                    \"clauseName\": \"国内水路、陆路货物运输保险条款（2009版）\",\n" +
                "                    \"kindCode\": \"090191\",\n" +
                "                    \"kindName\": \"人保国内水路、陆路货运综合险\",\n" +
                "                    \"itemNo\": 1,\n" +
                "                    \"itemCode\": \"520500\",\n" +
                "                    \"itemDetailName\": \"23r23f\",\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"modeCode\": \"\",\n" +
                "                    \"modeName\": \"23r32\",\n" +
                "                    \"startDate\": \"2019-09-12\",\n" +
                "                    \"startHour\": 0,\n" +
                "                    \"endDate\": \"2020-09-10\",\n" +
                "                    \"endHour\": 24,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 1000.00,\n" +
                "                    \"quantity\": 1.00,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 1000.00,\n" +
                "                    \"rate\": 0.80000000000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": null,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": null,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": null,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": null,\n" +
                "                    \"premium\": 0.80,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 0.02,\n" +
                "                    \"taxFee_gb\": 0.02,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"netPremium\": 0.78,\n" +
                "                    \"allTaxFee\": 0.05,\n" +
                "                    \"allNetPremium\": 0.75,\n" +
                "                    \"taxRate\": 6.00,\n" +
                "                    \"taxFlag\": \"2\",\n" +
                "                    \"flag\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"prpCopyProfits\": [],\n" +
                "                    \"prpCopyItemKindDetails\": [],\n" +
                "                    \"prpCopyItemKindTaxFees\": [],\n" +
                "                    \"model\": \"\",\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainProps\": [],\n" +
                "            \"prpCopyAgents\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"roleCode\": \"83242824\",\n" +
                "                        \"payNo\": 1,\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"agreementNo\": \"RULE20173204000000001         \",\n" +
                "                    \"roleType\": \"2\",\n" +
                "                    \"roleName\": \"张爱芳                        \",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"costRate\": 100.0000,\n" +
                "                    \"costFee\": 780.00,\n" +
                "                    \"remark\": \"\",\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyExtendInfos\": [],\n" +
                "            \"prpCopyMainAirLines\": [],\n" +
                "            \"prpCopyRations\": [],\n" +
                "            \"prpCopyCoinsDetails\": [],\n" +
                "            \"prpCopyPlans\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": null,\n" +
                "                    \"payNo\": 1,\n" +
                "                    \"payReason\": \"R21\",\n" +
                "                    \"planDate\": \"2019-09-12\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"subsidyrate\": null,\n" +
                "                    \"planFee\": 0.40,\n" +
                "                    \"delinquentFee\": 0.40,\n" +
                "                    \"flag\": null,\n" +
                "                    \"payDate\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"payType\": null,\n" +
                "                    \"exchangeNo\": null,\n" +
                "                    \"paymentcomplete\": null,\n" +
                "                    \"taxFee\": 0.02,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": null,\n" +
                "                    \"payNo\": 1,\n" +
                "                    \"payReason\": \"R70\",\n" +
                "                    \"planDate\": \"2019-09-12\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"subsidyrate\": null,\n" +
                "                    \"planFee\": 0.08,\n" +
                "                    \"delinquentFee\": 0.08,\n" +
                "                    \"flag\": \" 3\",\n" +
                "                    \"payDate\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"payType\": null,\n" +
                "                    \"exchangeNo\": null,\n" +
                "                    \"paymentcomplete\": null,\n" +
                "                    \"taxFee\": null,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 3,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": null,\n" +
                "                    \"payNo\": 1,\n" +
                "                    \"payReason\": \"R70\",\n" +
                "                    \"planDate\": \"2019-09-12\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"subsidyrate\": null,\n" +
                "                    \"planFee\": 0.32,\n" +
                "                    \"delinquentFee\": 0.32,\n" +
                "                    \"flag\": \" 1\",\n" +
                "                    \"payDate\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"payType\": null,\n" +
                "                    \"exchangeNo\": null,\n" +
                "                    \"paymentcomplete\": null,\n" +
                "                    \"taxFee\": 0.02,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyFees\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"currency\": \"CNY\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"amount\": 1000.00,\n" +
                "                    \"premium\": 0.80,\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"sumTaxFee\": 0.02,\n" +
                "                    \"sumNetPremium\": 0.78,\n" +
                "                    \"sumTaxFee_gb\": 0.02,\n" +
                "                    \"sumTaxFee_lb\": 0.00,\n" +
                "                    \"sumTaxFee_ys\": 0.00,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainChannels\": [],\n" +
                "            \"prpCopySpecialFacs\": [],\n" +
                "            \"prpCopyProfitFactors\": [],\n" +
                "            \"prpCopyAgentDetails\": [],\n" +
                "            \"prpCopyMainLoans\": [],\n" +
                "            \"prpCopyLimits\": [],\n" +
                "            \"prpCopyMainLiabs\": [],\n" +
                "            \"prpCopyCoinses\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"currency\": \"CNY\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"mainPolicyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsCode\": \"32048200\",\n" +
                "                    \"coinsName\": \"金坛支公司\",\n" +
                "                    \"coinsType\": \"1\",\n" +
                "                    \"coinsRate\": 50.0000,\n" +
                "                    \"coinsAmount\": 500.00,\n" +
                "                    \"coinsPremium\": 0.40,\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"coIdentity\": null,\n" +
                "                    \"isSendSms\": null,\n" +
                "                    \"isSendMail\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"mainProposalNo\": \"TYDL201932045000000396\",\n" +
                "                    \"subProposalNo\": \"\",\n" +
                "                    \"subApplyNo\": null,\n" +
                "                    \"mainApplyNo\": null,\n" +
                "                    \"mainEndorseNo\": null,\n" +
                "                    \"payType\": \"2111\",\n" +
                "                    \"repaylb\": null,\n" +
                "                    \"repaygb\": null,\n" +
                "                    \"agencygb\": null,\n" +
                "                    \"isInvoice\": null,\n" +
                "                    \"isPay\": null,\n" +
                "                    \"handler1Code\": \"83242824\",\n" +
                "                    \"handler1Code_uni\": \"1132137471\",\n" +
                "                    \"handlerCode\": \"83242824\",\n" +
                "                    \"handlerCode_uni\": \"1132137471\",\n" +
                "                    \"operatorCode\": null,\n" +
                "                    \"businessNature\": \"1\",\n" +
                "                    \"agentCode\": \"000011000001\",\n" +
                "                    \"basicBankCode\": \"ICBC\",\n" +
                "                    \"basicBankName\": \"中国工商银行\",\n" +
                "                    \"recBankAreaCode\": \"3204\",\n" +
                "                    \"recBankAreaName\": \"江苏省_常州市\",\n" +
                "                    \"bankCode\": \"102304099994\",\n" +
                "                    \"bankName\": \"中国工商银行常州分行\",\n" +
                "                    \"accountNo\": \"132432432222222222221111\",\n" +
                "                    \"accountName\": \"中国人民财产保险股份有限公司常州市分公司\",\n" +
                "                    \"cnaps\": \"102304099994\",\n" +
                "                    \"identifyType\": \"03\",\n" +
                "                    \"identifyNo\": \"11111333\",\n" +
                "                    \"telephone\": \"13233345434\",\n" +
                "                    \"isPrivate\": \"2\",\n" +
                "                    \"purpose\": \"我问问\",\n" +
                "                    \"cardType\": \"2\",\n" +
                "                    \"sendSms\": \"\",\n" +
                "                    \"sendMail\": \"\",\n" +
                "                    \"payeeInfoid\": \"FYAE201932040000000000023\",\n" +
                "                    \"mailAddr\": \"123@qq.com\",\n" +
                "                    \"paymodeflag\": \"P01\",\n" +
                "                    \"comType\": \"21\",\n" +
                "                    \"coinsExtraFlag\": \" 11 1\",\n" +
                "                    \"coinsSchemeCode\": null,\n" +
                "                    \"coinsSchemeName\": null,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"currency\": \"CNY\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"mainPolicyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsCode\": \"32010200\",\n" +
                "                    \"coinsName\": \"中国人民财产保险股份有限公司南京市城东支公司\",\n" +
                "                    \"coinsType\": \"2\",\n" +
                "                    \"coinsRate\": 10.0000,\n" +
                "                    \"coinsAmount\": 100.00,\n" +
                "                    \"coinsPremium\": 0.08,\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"coIdentity\": null,\n" +
                "                    \"isSendSms\": null,\n" +
                "                    \"isSendMail\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"mainProposalNo\": \"TYDL201932045000000396\",\n" +
                "                    \"subProposalNo\": \"\",\n" +
                "                    \"subApplyNo\": null,\n" +
                "                    \"mainApplyNo\": null,\n" +
                "                    \"mainEndorseNo\": null,\n" +
                "                    \"payType\": \"2112\",\n" +
                "                    \"repaylb\": null,\n" +
                "                    \"repaygb\": null,\n" +
                "                    \"agencygb\": null,\n" +
                "                    \"isInvoice\": null,\n" +
                "                    \"isPay\": null,\n" +
                "                    \"handler1Code\": \"83223025\",\n" +
                "                    \"handler1Code_uni\": \"1132030091\",\n" +
                "                    \"handlerCode\": \"凌顾尉\",\n" +
                "                    \"handlerCode_uni\": \"\",\n" +
                "                    \"operatorCode\": null,\n" +
                "                    \"businessNature\": \"1\",\n" +
                "                    \"agentCode\": \"000011000001\",\n" +
                "                    \"basicBankCode\": \"\",\n" +
                "                    \"basicBankName\": \"\",\n" +
                "                    \"recBankAreaCode\": \"\",\n" +
                "                    \"recBankAreaName\": \"\",\n" +
                "                    \"bankCode\": \"\",\n" +
                "                    \"bankName\": \"\",\n" +
                "                    \"accountNo\": \"\",\n" +
                "                    \"accountName\": \"\",\n" +
                "                    \"cnaps\": \"\",\n" +
                "                    \"identifyType\": \"\",\n" +
                "                    \"identifyNo\": \"\",\n" +
                "                    \"telephone\": \"\",\n" +
                "                    \"isPrivate\": \"2\",\n" +
                "                    \"purpose\": \"\",\n" +
                "                    \"cardType\": \"2\",\n" +
                "                    \"sendSms\": \"\",\n" +
                "                    \"sendMail\": \"\",\n" +
                "                    \"payeeInfoid\": \"\",\n" +
                "                    \"mailAddr\": \"123@qq.com\",\n" +
                "                    \"paymodeflag\": \"P01\",\n" +
                "                    \"comType\": \"20\",\n" +
                "                    \"coinsExtraFlag\": \" 0  1\",\n" +
                "                    \"coinsSchemeCode\": null,\n" +
                "                    \"coinsSchemeName\": null,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"serialNo\": 3,\n" +
                "                        \"currency\": \"CNY\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"mainPolicyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsCode\": \"006\",\n" +
                "                    \"coinsName\": \"国寿资产\",\n" +
                "                    \"coinsType\": \"3\",\n" +
                "                    \"coinsRate\": 40.0000,\n" +
                "                    \"coinsAmount\": 400.00,\n" +
                "                    \"coinsPremium\": 0.32,\n" +
                "                    \"flag\": \"\",\n" +
                "                    \"coIdentity\": null,\n" +
                "                    \"isSendSms\": null,\n" +
                "                    \"isSendMail\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"mainProposalNo\": \"\",\n" +
                "                    \"subProposalNo\": \"\",\n" +
                "                    \"subApplyNo\": null,\n" +
                "                    \"mainApplyNo\": null,\n" +
                "                    \"mainEndorseNo\": null,\n" +
                "                    \"payType\": \"2112\",\n" +
                "                    \"repaylb\": null,\n" +
                "                    \"repaygb\": null,\n" +
                "                    \"agencygb\": null,\n" +
                "                    \"isInvoice\": null,\n" +
                "                    \"isPay\": null,\n" +
                "                    \"handler1Code\": \"\",\n" +
                "                    \"handler1Code_uni\": \"\",\n" +
                "                    \"handlerCode\": \"13163171\",\n" +
                "                    \"handlerCode_uni\": \"1232010062\",\n" +
                "                    \"operatorCode\": null,\n" +
                "                    \"businessNature\": \"\",\n" +
                "                    \"agentCode\": \"\",\n" +
                "                    \"basicBankCode\": \"ABC\",\n" +
                "                    \"basicBankName\": \"农业银行\",\n" +
                "                    \"recBankAreaCode\": \"1100\",\n" +
                "                    \"recBankAreaName\": \"北京市_北京市\",\n" +
                "                    \"bankCode\": \"103100015132\",\n" +
                "                    \"bankName\": \"中国农业银行股份有限公司北京兴怀大街分理处\",\n" +
                "                    \"accountNo\": \"2325555222244444\",\n" +
                "                    \"accountName\": \"22222\",\n" +
                "                    \"cnaps\": \"103100015132\",\n" +
                "                    \"identifyType\": \"04\",\n" +
                "                    \"identifyNo\": \"1111\",\n" +
                "                    \"telephone\": \"13244455432\",\n" +
                "                    \"isPrivate\": \"2\",\n" +
                "                    \"purpose\": \"23232\",\n" +
                "                    \"cardType\": \"2\",\n" +
                "                    \"sendSms\": \"\",\n" +
                "                    \"sendMail\": \"\",\n" +
                "                    \"payeeInfoid\": \"FYAE201932040000000000024\",\n" +
                "                    \"mailAddr\": \"123@qq.com\",\n" +
                "                    \"paymodeflag\": \"P01\",\n" +
                "                    \"comType\": \"90\",\n" +
                "                    \"coinsExtraFlag\": \" 0  1\",\n" +
                "                    \"coinsSchemeCode\": null,\n" +
                "                    \"coinsSchemeName\": null,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\"\n" +
                "                }\n" +
                "            ],");
        sb.append("\"prpCopyCargoDetails\": [],\n" +
                "            \"prpCopyShipDrivers\": [],\n" +
                "            \"prpCopyClauseConfines\": [],\n" +
                "            \"prpCopyClauses\": [],\n" +
                "            \"prpCopyContriutions\": [],\n" +
                "            \"prpCopyMainCommons\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"TYDL201932045000000396\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"qualityLevel\": null,\n" +
                "                    \"specialFlag\": \" \",\n" +
                "                    \"ext1\": null,\n" +
                "                    \"ext2\": null,\n" +
                "                    \"ext3\": \"\",\n" +
                "                    \"resourceCode\": null,\n" +
                "                    \"resourceName\": null,\n" +
                "                    \"insertTimeForHis\": \"2019-09-11 16:09:52\",\n" +
                "                    \"operateTimeForHis\": \"2019-09-11 16:09:46\",\n" +
                "                    \"newBusinessNature\": \"110\",\n" +
                "                    \"scmsAuditNotion\": null,\n" +
                "                    \"pay_method\": null,\n" +
                "                    \"platformProjectCode\": null,\n" +
                "                    \"platformProjectName\": null,\n" +
                "                    \"handler1Code_uni\": \"83242824\",\n" +
                "                    \"handlerCode_uni\": \"83242824\",\n" +
                "                    \"commonFlag\": \"0         0\",\n" +
                "                    \"otherPolicyName\": null,\n" +
                "                    \"groupName\": null,\n" +
                "                    \"isHPDriveCus\": \"0\",\n" +
                "                    \"startTime\": \"00:00\",\n" +
                "                    \"endTime\": \"00:00\",\n" +
                "                    \"salesCode\": null,\n" +
                "                    \"electronic\": \"0\",\n" +
                "                    \"electronicTitle\": null,\n" +
                "                    \"electronicPhone\": null,\n" +
                "                    \"socialinsPay\": null,\n" +
                "                    \"socialinsNo\": null,\n" +
                "                    \"projectCode\": null,\n" +
                "                    \"projectName\": null,\n" +
                "                    \"priorityFlag\": null,\n" +
                "                    \"priorityMessage\": null,\n" +
                "                    \"isAccredit\": null,\n" +
                "                    \"accreditType\": null,\n" +
                "                    \"accreditDate\": null,\n" +
                "                    \"othflag\": null,\n" +
                "                    \"bankFlowNo\": null,\n" +
                "                    \"tkey\": \"2019-09-11 16:09:45\",\n" +
                "                    \"sealNum\": null,\n" +
                "                    \"classify\": null,\n" +
                "                    \"overSeas\": \"0\",\n" +
                "                    \"isClaim\": null,\n" +
                "                    \"isCondition\": null,\n" +
                "                    \"unifiedInsurance\": \"\",\n" +
                "                    \"electronicEmail\": null,\n" +
                "                    \"isRenewalTeam\": null,\n" +
                "                    \"keyAccountCode\": null,\n" +
                "                    \"isRenewal\": null,\n" +
                "                    \"isGIvesff\": null,\n" +
                "                    \"isStatistics\": null,\n" +
                "                    \"isInsureRate\": null,\n" +
                "                    \"busiAccountType\": \" \",\n" +
                "                    \"isPStage\": \" \",\n" +
                "                    \"visaCode\": null,\n" +
                "                    \"visaPrintCode\": null,\n" +
                "                    \"visaNo\": null,\n" +
                "                    \"isVisaCancel\": null,\n" +
                "                    \"isPoverty\": null,\n" +
                "                    \"isTargetedPoverty\": null,\n" +
                "                    \"coMakecom\": null,\n" +
                "                    \"coOperatorcode\": null,\n" +
                "                    \"inputType\": null,\n" +
                "                    \"deliverFlag\": null,\n" +
                "                    \"deliverType\": null,\n" +
                "                    \"addressee\": null,\n" +
                "                    \"deliverTel\": null,\n" +
                "                    \"deliverAddr\": null,\n" +
                "                    \"isVsCard\": null,\n" +
                "                    \"subinformation\": null,\n" +
                "                    \"isRapidCalPremium\": null,\n" +
                "                    \"externalPayFlag\": null,\n" +
                "                    \"ownerFlag\": null,\n" +
                "                    \"signTag\": null,\n" +
                "                    \"signState\": null,\n" +
                "                    \"invokeFlag\": null,\n" +
                "                    \"invoiceCode\": null,\n" +
                "                    \"reviewerName\": null,\n" +
                "                    \"receivableFlag\": null,\n" +
                "                    \"internationalFlag\": null,\n" +
                "                    \"recallFlag\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyItemCreditOths\": [],\n" +
                "            \"isAutoePolicy\": null,\n" +
                "            \"handler1Code_uni\": null,\n" +
                "            \"personOri\": null,\n" +
                "            \"salesCode\": null,\n" +
                "            \"actualProduct\": null,\n" +
                "            \"currency\": null,\n" +
                "            \"dmFlag\": null,\n" +
                "            \"insuredCount\": null,\n" +
                "            \"productCode\": null,\n" +
                "            \"productName\": null,\n" +
                "            \"comName\": null,\n" +
                "            \"agentName\": null,\n" +
                "            \"handlerName\": null,\n" +
                "            \"handler1Name\": null,\n" +
                "            \"operatorName\": null,\n" +
                "            \"prpCopyClauseplans\": []\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": {\n" +
                "                \"pkey\": \"PYDL201932045000000277\",\n" +
                "                \"applyNo\": \"DYDL202032045000000076\"\n" +
                "            },\n" +
                "            \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "            \"policyNo\": \"PYDL201932045000000277\",\n" +
                "            \"classCode\": \"09\",\n" +
                "            \"riskCode\": \"YDL\",\n" +
                "            \"projectCode\": \"\",\n" +
                "            \"projectName\": null,\n" +
                "            \"contractNo\": \"合同号\",\n" +
                "            \"policySort\": \"1\",\n" +
                "            \"businessNature\": \"1\",\n" +
                "            \"language\": \"C\",\n" +
                "            \"policyType\": \"19\",\n" +
                "            \"agriFlag\": \"0\",\n" +
                "            \"operateDate\": \"2019-09-11 16:09:46\",\n" +
                "            \"startDate\": \"2019-09-12 00:00:00\",\n" +
                "            \"endDate\": \"2020-09-06 00:00:00\",\n" +
                "            \"startHour\": 0,\n" +
                "            \"endHour\": 24,\n" +
                "            \"sumValue\": 0.00,\n" +
                "            \"sumAmount\": 1000.00,\n" +
                "            \"sumDiscount\": null,\n" +
                "            \"sumPremium\": 0.80,\n" +
                "            \"sumSubPrem\": 0.00,\n" +
                "            \"sumQuantity\": 0,\n" +
                "            \"policyCount\": null,\n" +
                "            \"judicalScope\": \"01\",\n" +
                "            \"argueSolution\": \"1\",\n" +
                "            \"arbitBoardName\": \"\",\n" +
                "            \"payTimes\": 1,\n" +
                "            \"makeCom\": \"32048200\",\n" +
                "            \"operateSite\": \"中国\",\n" +
                "            \"comCode\": \"32048200\",\n" +
                "            \"handlerCode\": \"83242824\",\n" +
                "            \"handler1Code\": \"83242824\",\n" +
                "            \"prePremium\": null,\n" +
                "            \"checkFlag\": \"4\",\n" +
                "            \"checkUpCode\": \"\",\n" +
                "            \"checkOpinion\": \"\",\n" +
                "            \"underWriteCode\": \"UnderWrite\",\n" +
                "            \"underWriteName\": \"自动核保\",\n" +
                "            \"operatorCode\": \"A320000058\",\n" +
                "            \"inputTime\": \"2019-09-11 16:09:07\",\n" +
                "            \"underWriteEndDate\": \"2019-09-11\",\n" +
                "            \"statisticsYM\": \"201909\",\n" +
                "            \"agentCode\": \"000011000001\",\n" +
                "            \"coinsFlag\": \"11\",\n" +
                "            \"crossFlag\": \"0\",\n" +
                "            \"reinsFlag\": \"0000099000\",\n" +
                "            \"isReins\": \"0\",\n" +
                "            \"allinsFlag\": \"0\",\n" +
                "            \"checkCode\": \"\",\n" +
                "            \"underWriteFlag\": \"1\",\n" +
                "            \"jfeeFlag\": \"0\",\n" +
                "            \"payMode\": \"2\",\n" +
                "            \"payCode\": null,\n" +
                "            \"othFlag\": \"000000YY00\",\n" +
                "            \"inputFlag\": \"\",\n" +
                "            \"remark\": \"\",\n" +
                "            \"flag\": \"U\",\n" +
                "            \"endorseNo\": \"0\",\n" +
                "            \"validDate\": \"2020-10-15\",\n" +
                "            \"validHour\": 24,\n" +
                "            \"endorseTimes\": 0,\n" +
                "            \"pureRate\": null,\n" +
                "            \"discount\": null,\n" +
                "            \"disRate\": null,\n" +
                "            \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "            \"operateTimeForHis\": \"2020-03-23 10:20:15\",\n" +
                "            \"batchGroupNo\": null,\n" +
                "            \"projectCodeZ\": null,\n" +
                "            \"sumTaxFee\": 0.02,\n" +
                "            \"sumNetPremium\": 0.78,\n" +
                "            \"inputSumPremium\": null,\n" +
                "            \"dutySumNetPremium\": null,\n" +
                "            \"freeSumNetPremium\": null,\n" +
                "            \"orderNo\": null,\n" +
                "            \"orderFlag\": null,\n" +
                "            \"prpCopyMainAccs\": [],\n" +
                "            \"prpCopyMainExts\": [],\n" +
                "            \"prpCopyMainBonds\": [],\n" +
                "            \"prpCopyItems\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"PYDL201932045000000277\",\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"itemNo\": 1\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"itemCode\": \"852050\",\n" +
                "                    \"itemName\": \"旅行用品、手提包及类似容器\",\n" +
                "                    \"plusRate\": 0.000000,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"itemInfo\": null,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"prpCopyItemPlanes\": [],\n" +
                "                    \"prpCopyItemDevices\": [],\n" +
                "                    \"prpCopyItemProps\": [],\n" +
                "                    \"prpCopyItemHouses\": [],\n" +
                "                    \"prpCopyItemCars\": [],\n" +
                "                    \"prpCopyItemCargos\": [],\n" +
                "                    \"prpCopyItemShips\": [\n" +
                "                        {\n" +
                "                            \"id\": {\n" +
                "                                \"pkey\": \"PYDL201932045000000277\",\n" +
                "                                \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                                \"itemNo\": 1\n" +
                "                            },\n" +
                "                            \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                            \"riskCode\": \"YDL\",\n" +
                "                            \"fleetNo\": \"\",\n" +
                "                            \"ditemno\": null,\n" +
                "                            \"shipCode\": \"\",\n" +
                "                            \"shipCName\": \"\",\n" +
                "                            \"shipEName\": \"\",\n" +
                "                            \"oldShipName\": null,\n" +
                "                            \"shipOwner\": null,\n" +
                "                            \"oldShipOwner\": null,\n" +
                "                            \"conveyManager\": null,\n" +
                "                            \"associate\": \"\",\n" +
                "                            \"makeYearMonth\": \"20190823\",\n" +
                "                            \"countryCode\": \"\",\n" +
                "                            \"makeFactory\": \"\",\n" +
                "                            \"makeDock\": \"2019-05-31\",\n" +
                "                            \"shipWayType\": null,\n" +
                "                            \"applyCriterion\": null,\n" +
                "                            \"makeContractNo\": null,\n" +
                "                            \"makeStartDate\": null,\n" +
                "                            \"makeEndDate\": null,\n" +
                "                            \"preBuildCyc\": null,\n" +
                "                            \"stepHull\": \"无船级\",\n" +
                "                            \"oldStepHull\": null,\n" +
                "                            \"shipFlag\": \"\",\n" +
                "                            \"shipTypeCode\": null,\n" +
                "                            \"useNatureCode\": null,\n" +
                "                            \"shipUsage\": null,\n" +
                "                            \"shipStruct\": null,\n" +
                "                            \"registrySite\": null,\n" +
                "                            \"tonCount\": null,\n" +
                "                            \"netTonCount\": null,\n" +
                "                            \"horsePower\": null,\n" +
                "                            \"powerUnit\": null,\n" +
                "                            \"seatCount\": null,\n" +
                "                            \"loadTon\": null,\n" +
                "                            \"shipLength\": null,\n" +
                "                            \"shipWidth\": null,\n" +
                "                            \"shipDepth\": null,\n" +
                "                            \"trySailPeriod\": null,\n" +
                "                            \"trySailArea\": null,\n" +
                "                            \"shipPort\": null,\n" +
                "                            \"launchDate\": null,\n" +
                "                            \"sailAreaCode\": null,\n" +
                "                            \"sailAreaName\": null,\n" +
                "                            \"sailScope\": null,\n" +
                "                            \"sailModeCode\": null,\n" +
                "                            \"voyage\": null,\n" +
                "                            \"shipValue\": null,\n" +
                "                            \"currency\": null,\n" +
                "                            \"suspendStartDate\": null,\n" +
                "                            \"suspendEndDate\": null,\n" +
                "                            \"mortgageName\": null,\n" +
                "                            \"insurerShipRelation\": null,\n" +
                "                            \"shipCallSign\": null,\n" +
                "                            \"imo\": null,\n" +
                "                            \"shipManagerAddress\": null,\n" +
                "                            \"reconstructionYear\": null,\n" +
                "                            \"shipAssociation\": null,\n" +
                "                            \"fleetTotalScale\": null,\n" +
                "                            \"fleetPICCScale\": null,\n" +
                "                            \"shipConstractionAdrdress\": null,\n" +
                "                            \"launchType\": null,\n" +
                "                            \"govAgencyForShip\": null,\n" +
                "                            \"licStartDateForShip\": null,\n" +
                "                            \"licEndDateForShip\": null,\n" +
                "                            \"govAgencyForHull\": null,\n" +
                "                            \"licStartDateForHull\": null,\n" +
                "                            \"licEndDateForHull\": null,\n" +
                "                            \"govAgencyForTempHull\": null,\n" +
                "                            \"licStartDateForTempHull\": null,\n" +
                "                            \"licenceEndDateForTempHull\": null,\n" +
                "                            \"govAgencyForMachinery\": null,\n" +
                "                            \"licStartDateForMachinery\": null,\n" +
                "                            \"licEndDateForMachinery\": null,\n" +
                "                            \"govAgencyForTempMachinery\": null,\n" +
                "                            \"licStartDateForTempMachinery\": null,\n" +
                "                            \"licEndDateForTempMachinery\": null,\n" +
                "                            \"isLicForIntTon\": null,\n" +
                "                            \"licStartDateForIntTon\": null,\n" +
                "                            \"licEndDateForIntTon\": null,\n" +
                "                            \"isLicForIntLoad\": null,\n" +
                "                            \"licStartDateForIntLoad\": null,\n" +
                "                            \"licEndDateForIntLoad\": null,\n" +
                "                            \"isLicForConstruction\": null,\n" +
                "                            \"licStartDateForConstruction\": null,\n" +
                "                            \"licEndDateForConstruction\": null,\n" +
                "                            \"isLicForEquipment\": null,\n" +
                "                            \"licStartDateForEquipment\": null,\n" +
                "                            \"licEndDateForEquipment\": null,\n" +
                "                            \"isLicForWireLess\": null,\n" +
                "                            \"licStartDateForWireLess\": null,\n" +
                "                            \"licEndDateForWireLess\": null,\n" +
                "                            \"isLicForLift\": null,\n" +
                "                            \"licStartDateForLift\": null,\n" +
                "                            \"licEndDateForLift\": null,\n" +
                "                            \"isLicForFitness\": null,\n" +
                "                            \"licStartDateForFitness\": null,\n" +
                "                            \"licEndDateForFitness\": null,\n" +
                "                            \"isLicForIOPP\": null,\n" +
                "                            \"licStartDateForIOPP\": null,\n" +
                "                            \"licEndDateForIOPP\": null,\n" +
                "                            \"isLicForNavigation\": null,\n" +
                "                            \"licStartDateForNavigation\": null,\n" +
                "                            \"licEndDateForNavigation\": null,\n" +
                "                            \"isLicForSMC\": null,\n" +
                "                            \"licStartDateForSMC\": null,\n" +
                "                            \"licEndDateForSMC\": null,\n" +
                "                            \"isLicForConform\": null,\n" +
                "                            \"licStartDateForConform\": null,\n" +
                "                            \"licEndDateForConform\": null,\n" +
                "                            \"isLicForMSM\": null,\n" +
                "                            \"licStartDateForMSM\": null,\n" +
                "                            \"licEndDateForMSM\": null,\n" +
                "                            \"makeDockAddress\": null,\n" +
                "                            \"projectForBuilder\": null,\n" +
                "                            \"constractNoForBuilder\": null,\n" +
                "                            \"amountForBuilder\": null,\n" +
                "                            \"addressForBuilder\": null,\n" +
                "                            \"projectForSub\": null,\n" +
                "                            \"constractNoForSub\": null,\n" +
                "                            \"addressForSub\": null,\n" +
                "                            \"historyForBuilder\": null,\n" +
                "                            \"lastPrice\": null,\n" +
                "                            \"deliveryPlace\": null,\n" +
                "                            \"beginDate\": null,\n" +
                "                            \"stageDate\": null,\n" +
                "                            \"waterDate\": null,\n" +
                "                            \"sailDate\": null,\n" +
                "                            \"deliveryDate\": null,\n" +
                "                            \"insuredStatus\": null,\n" +
                "                            \"actualShipOwner\": null,\n" +
                "                            \"loadType\": null,\n" +
                "                            \"loadStyle\": null,\n" +
                "                            \"shipRegisterOwner\": null,\n" +
                "                            \"shipRegisterPlace\": null,\n" +
                "                            \"newContractPrice\": null,\n" +
                "                            \"buyerName\": null,\n" +
                "                            \"waybillNumber\": null,\n" +
                "                            \"sellerName\": null,\n" +
                "                            \"onlinePurchaseOrderNumber\": null,\n" +
                "                            \"transportMachine\": null,\n" +
                "                            \"destination\": null,\n" +
                "                            \"insured\": null,\n" +
                "                            \"departurePlace\": null,\n" +
                "                            \"quantity\": null,\n" +
                "                            \"boxSize\": null,\n" +
                "                            \"usage\": null,\n" +
                "                            \"boxNumber\": null,\n" +
                "                            \"carTypre\": null,\n" +
                "                            \"boxType\": null,\n" +
                "                            \"ownFlag\": null,\n" +
                "                            \"deviceNo\": null,\n" +
                "                            \"remark\": null,\n" +
                "                            \"flag\": \"U\",\n" +
                "                            \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                            \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                            \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                            \"voyageNo\": null,\n" +
                "                            \"registryPort\": null,\n" +
                "                            \"shipYears\": null,\n" +
                "                            \"landAgent\": null,\n" +
                "                            \"shipLeval\": null,\n" +
                "                            \"route\": null,\n" +
                "                            \"aquageGrade\": null,\n" +
                "                            \"importFlag\": null,\n" +
                "                            \"engineNo\": null,\n" +
                "                            \"vinNo\": null,\n" +
                "                            \"certificateNo\": null\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"prpCopyItemConstructs\": [],\n" +
                "                    \"prpCopyItemTravelAgencys\": []\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainSubs\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"mainPolicyNo\": \"PYAE201932040000000042\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"mainProposalNo\": \"TYAE201932040000000056\",\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": \"\",\n" +
                "                    \"cargoSerialNo\": \"\",\n" +
                "                    \"balanceTimes\": null,\n" +
                "                    \"balanceFlag\": \"0\",\n" +
                "                    \"payFlag\": \"1\",\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"auditNo\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyCommissions\": [],\n" +
                "            \"prpCopyCommissionDetails\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"agreementNo\": \"RULE20173204000000001         \",\n" +
                "                    \"customerGroupCode\": \"1\",\n" +
                "                    \"costType\": \"01\",\n" +
                "                    \"payNo\": 1,\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"clauseCode\": \"PUB   \",\n" +
                "                    \"kindCode\": \"PUB   \",\n" +
                "                    \"sumPremium\": 0.78,\n" +
                "                    \"costRate\": 20.00,\n" +
                "                    \"costRateUpper\": 20.00,\n" +
                "                    \"adjustFlag\": \"1\",\n" +
                "                    \"upperFlag\": \"1\",\n" +
                "                    \"auditRate\": null,\n" +
                "                    \"auditFlag\": \"1\",\n" +
                "                    \"coinsRate\": 50.00000000,\n" +
                "                    \"coinsDeduct\": \"\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"costFee\": 780.00,\n" +
                "                    \"configCode\": \"1       \",\n" +
                "                    \"amortizeFlag\": \" \",\n" +
                "                    \"clauseKindFlag\": \"      \",\n" +
                "                    \"remark\": null,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"levelRate\": 0.00,\n" +
                "                    \"levelMaxRate\": 0.00,\n" +
                "                    \"isNetFlag\": \"1\",\n" +
                "                    \"queryNo\": null,\n" +
                "                    \"xsfyRateUpper\": null,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"isAmortizeFlag\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyEngages\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"clauseCode\": \"LF002\",\n" +
                "                    \"clauseName\": \"被保险人名称\",\n" +
                "                    \"clauses\": \"浙江省货运险外部客户\",\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"relatedFlag\": null,\n" +
                "                    \"relatedContent\": null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"clauseCode\": \"T0001\",\n" +
                "                    \"clauseName\": \"特别约定\",\n" +
                "                    \"clauses\": \"特别约定\",\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"relatedFlag\": null,\n" +
                "                    \"relatedContent\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainAgris\": [],\n" +
                "            \"prpCopyBatchs\": [],\n" +
                "            \"prpCopyCoeffs\": [],\n" +
                "            \"prpCopyMainCargos\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"PYDL201932045000000277\",\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"priceCondition\": null,\n" +
                "                    \"ladingNo\": \"\",\n" +
                "                    \"invoiceNo\": \"\",\n" +
                "                    \"invoiceCurrency\": \"CNY\",\n" +
                "                    \"invoiceAmount\": 1000.00,\n" +
                "                    \"plusRate\": 0.000000,\n" +
                "                    \"creditNo\": \"000000\",\n" +
                "                    \"shipNoteNo\": null,\n" +
                "                    \"bargainNo\": null,\n" +
                "                    \"conveyance\": \"\",\n" +
                "                    \"conveyancePlace\": null,\n" +
                "                    \"blName\": \"\",\n" +
                "                    \"carryBillNo\": \"\",\n" +
                "                    \"transferConveyance\": null,\n" +
                "                    \"blNo\": \"dfgh\",\n" +
                "                    \"voyageNo\": \"dfgh\",\n" +
                "                    \"preserveInfo\": null,\n" +
                "                    \"tonCount\": null,\n" +
                "                    \"startSiteCode\": null,\n" +
                "                    \"startSiteName\": \"HGD\",\n" +
                "                    \"viaSiteCode\": null,\n" +
                "                    \"viaSiteName\": \"\",\n" +
                "                    \"reshipSiteName\": null,\n" +
                "                    \"endSiteCode\": null,\n" +
                "                    \"endSiteName\": \"FGH\",\n" +
                "                    \"endDetailName\": null,\n" +
                "                    \"checkAgentCode\": null,\n" +
                "                    \"claimSite\": \"\",\n" +
                "                    \"transferBank\": null,\n" +
                "                    \"originalCount\": 2,\n" +
                "                    \"goodsAmount\": null,\n" +
                "                    \"trailerPlate\": null,\n" +
                "                    \"temperatureControl\": null,\n" +
                "                    \"transportType\": \"Y06\",\n" +
                "                    \"carCount\": null,\n" +
                "                    \"machineType\": null,\n" +
                "                    \"registryPort\": null,\n" +
                "                    \"businessScope\": null,\n" +
                "                    \"shipYears\": null,\n" +
                "                    \"engineNumber\": null,\n" +
                "                    \"routeLengthUnit\": null,\n" +
                "                    \"goodsName\": \"23r23f\",\n" +
                "                    \"routeLength\": null,\n" +
                "                    \"startPost\": null,\n" +
                "                    \"endPost\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"recipientName\": null,\n" +
                "                    \"departureTime\": \"2019-09-11\",\n" +
                "                    \"transportScope\": null,\n" +
                "                    \"commitmentArrivalTime\": null,\n" +
                "                    \"distance\": null,\n" +
                "                    \"commitmentPeriod\": null,\n" +
                "                    \"preInsureFlag\": null,\n" +
                "                    \"businessDetail\": null,\n" +
                "                    \"landAgent\": \"\",\n" +
                "                    \"protectedClub\": null,\n" +
                "                    \"checkedTone\": null,\n" +
                "                    \"income\": null,\n" +
                "                    \"quantity\": null,\n" +
                "                    \"shipLeval\": \"无船级\",\n" +
                "                    \"balanceFlag\": \"0\",\n" +
                "                    \"cargoserialNo\": null,\n" +
                "                    \"mainPolicyNo\": null,\n" +
                "                    \"payFlag\": \"1\",\n" +
                "                    \"remark\": \"\",\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"carKindCode\": null,\n" +
                "                    \"carQuantity\": null,\n" +
                "                    \"busCount\": null,\n" +
                "                    \"truckCount\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"shipNo\": null,\n" +
                "                    \"buildYear\": null,\n" +
                "                    \"preSigns\": null,\n" +
                "                    \"loadTranSport\": null,\n" +
                "                    \"vinNo\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainCredits\": [],\n" +
                "            \"prpCopyAddresses\": [],\n" +
                "            \"prpCopyMainConstructs\": [],\n" +
                "            \"prpCopyInsureds\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"language\": \"C\",\n" +
                "                    \"insuredType\": \"1\",\n" +
                "                    \"insuredCode\": \"3200100000025700\",\n" +
                "                    \"insuredName\": \"海贼\",\n" +
                "                    \"insuredEName\": \"\",\n" +
                "                    \"aliasName\": \"\",\n" +
                "                    \"insuredAddress\": \"黑龙江省哈尔滨市五常市黑龙江五常市\",\n" +
                "                    \"insuredNature\": \"\",\n" +
                "                    \"insuredFlag\": \"01000000000000000000000000000A\",\n" +
                "                    \"unitType\": \"\",\n" +
                "                    \"appendPrintName\": \"\",\n" +
                "                    \"insuredIdentity\": \"\",\n" +
                "                    \"relateSerialNo\": null,\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"identifyNumber\": \"130283198609068214\",\n" +
                "                    \"unifiedSocialCreditCode\": null,\n" +
                "                    \"creditLevel\": \"\",\n" +
                "                    \"possessNature\": null,\n" +
                "                    \"businessSource\": null,\n" +
                "                    \"businessName\": null,\n" +
                "                    \"businessSort\": null,\n" +
                "                    \"occupationCode\": \"\",\n" +
                "                    \"occupationName\": null,\n" +
                "                    \"educationCode\": null,\n" +
                "                    \"bank\": \"\",\n" +
                "                    \"accountName\": null,\n" +
                "                    \"account\": null,\n" +
                "                    \"linkerName\": \"海贼\",\n" +
                "                    \"postAddress\": \"黑龙江省哈尔滨市五常市黑龙江五常市\",\n" +
                "                    \"postCode\": \"\",\n" +
                "                    \"postName\": null,\n" +
                "                    \"phoneNumber\": \"\",\n" +
                "                    \"faxNumber\": \"\",\n" +
                "                    \"mobile\": \"13233322222\",\n" +
                "                    \"netAddress\": \"\",\n" +
                "                    \"email\": \"\",\n" +
                "                    \"dateValid\": null,\n" +
                "                    \"startDate\": \"2019-09-12 00:00:00\",\n" +
                "                    \"endDate\": \"2020-09-09 00:00:00\",\n" +
                "                    \"benefitFlag\": null,\n" +
                "                    \"benefitRate\": null,\n" +
                "                    \"drivingLicenseNo\": \"\",\n" +
                "                    \"changelessFlag\": \"\",\n" +
                "                    \"sex\": \"\",\n" +
                "                    \"age\": null,\n" +
                "                    \"marriage\": \"\",\n" +
                "                    \"driverAddress\": \"\",\n" +
                "                    \"peccancy\": null,\n" +
                "                    \"acceptLicenseDate\": null,\n" +
                "                    \"receiveLicenseYear\": null,\n" +
                "                    \"drivingYears\": null,\n" +
                "                    \"causeTroubleTimes\": null,\n" +
                "                    \"awardLicenseOrgan\": \"\",\n" +
                "                    \"drivingCarType\": \"\",\n" +
                "                    \"countryCode\": \"\",\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"warningFlag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"blackflag\": null,\n" +
                "                    \"insuredSort\": null,\n" +
                "                    \"prpCopyInsuredNatures\": [],\n" +
                "                    \"prpCopyInsuredArtifs\": [],\n" +
                "                    \"prpCopyInsuredCreditInvests\": [],\n" +
                "                    \"groupCode\": null,\n" +
                "                    \"groupName\": null,\n" +
                "                    \"dweller\": null,\n" +
                "                    \"customerLevel\": null,\n" +
                "                    \"insuredPYName\": null,\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"versionNo\": null,\n" +
                "                    \"itemNo\": null,\n" +
                "                    \"importFlag\": null,\n" +
                "                    \"smsFlag\": null,\n" +
                "                    \"emailFlag\": null,\n" +
                "                    \"sendPhone\": null,\n" +
                "                    \"sendEmail\": null,\n" +
                "                    \"subPolicyNo\": null,\n" +
                "                    \"importSerialNo\": null,\n" +
                "                    \"socialSecurityNo\": null,\n" +
                "                    \"electronicflag\": null,\n" +
                "                    \"isHealthSurvey\": null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"language\": \"C\",\n" +
                "                    \"insuredType\": \"1\",\n" +
                "                    \"insuredCode\": \"3200100000025700\",\n" +
                "                    \"insuredName\": \"海贼\",\n" +
                "                    \"insuredEName\": \"\",\n" +
                "                    \"aliasName\": \"\",\n" +
                "                    \"insuredAddress\": \"黑龙江省哈尔滨市五常市黑龙江五常市\",\n" +
                "                    \"insuredNature\": \"\",\n" +
                "                    \"insuredFlag\": \"11000000000000000000000000000A\",\n" +
                "                    \"unitType\": \"\",\n" +
                "                    \"appendPrintName\": \"\",\n" +
                "                    \"insuredIdentity\": \"\",\n" +
                "                    \"relateSerialNo\": null,\n" +
                "                    \"identifyType\": \"01\",\n" +
                "                    \"identifyNumber\": \"130283198609068214\",\n" +
                "                    \"unifiedSocialCreditCode\": null,\n" +
                "                    \"creditLevel\": \"\",\n" +
                "                    \"possessNature\": null,\n" +
                "                    \"businessSource\": null,\n" +
                "                    \"businessName\": null,\n" +
                "                    \"businessSort\": null,\n" +
                "                    \"occupationCode\": \"\",\n" +
                "                    \"occupationName\": null,\n" +
                "                    \"educationCode\": null,\n" +
                "                    \"bank\": \"\",\n" +
                "                    \"accountName\": null,\n" +
                "                    \"account\": null,\n" +
                "                    \"linkerName\": \"海贼\",\n" +
                "                    \"postAddress\": \"黑龙江省哈尔滨市五常市黑龙江五常市\",\n" +
                "                    \"postCode\": \"\",\n" +
                "                    \"postName\": null,\n" +
                "                    \"phoneNumber\": \"\",\n" +
                "                    \"faxNumber\": \"\",\n" +
                "                    \"mobile\": \"13233322222\",\n" +
                "                    \"netAddress\": \"\",\n" +
                "                    \"email\": \"\",\n" +
                "                    \"dateValid\": null,\n" +
                "                    \"startDate\": \"2019-09-12 00:00:00\",\n" +
                "                    \"endDate\": \"2020-09-09 00:00:00\",\n" +
                "                    \"benefitFlag\": null,\n" +
                "                    \"benefitRate\": null,\n" +
                "                    \"drivingLicenseNo\": \"\",\n" +
                "                    \"changelessFlag\": \"\",\n" +
                "                    \"sex\": \"\",\n" +
                "                    \"age\": null,\n" +
                "                    \"marriage\": \"\",\n" +
                "                    \"driverAddress\": \"\",\n" +
                "                    \"peccancy\": null,\n" +
                "                    \"acceptLicenseDate\": null,\n" +
                "                    \"receiveLicenseYear\": null,\n" +
                "                    \"drivingYears\": null,\n" +
                "                    \"causeTroubleTimes\": null,\n" +
                "                    \"awardLicenseOrgan\": \"\",\n" +
                "                    \"drivingCarType\": \"\",\n" +
                "                    \"countryCode\": \"\",\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"warningFlag\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"blackflag\": null,\n" +
                "                    \"insuredSort\": null,\n" +
                "                    \"prpCopyInsuredNatures\": [],\n" +
                "                    \"prpCopyInsuredArtifs\": [],\n" +
                "                    \"prpCopyInsuredCreditInvests\": [],\n" +
                "                    \"groupCode\": null,\n" +
                "                    \"groupName\": null,\n" +
                "                    \"dweller\": null,\n" +
                "                    \"customerLevel\": null,\n" +
                "                    \"insuredPYName\": null,\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"versionNo\": null,\n" +
                "                    \"itemNo\": null,\n" +
                "                    \"importFlag\": null,\n" +
                "                    \"smsFlag\": null,\n" +
                "                    \"emailFlag\": null,\n" +
                "                    \"sendPhone\": null,\n" +
                "                    \"sendEmail\": null,\n" +
                "                    \"subPolicyNo\": null,\n" +
                "                    \"importSerialNo\": null,\n" +
                "                    \"socialSecurityNo\": null,\n" +
                "                    \"electronicflag\": null,\n" +
                "                    \"isHealthSurvey\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainInvests\": [],\n" +
                "            \"prpCopyNames\": [],\n" +
                "            \"prpCopyItemKinds\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"pkey\": \"PYDL201932045000000277\",\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"itemKindNo\": 1\n" +
                "                    },\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"familyNo\": null,\n" +
                "                    \"familyName\": \"\",\n" +
                "                    \"projectCode\": \"\",\n" +
                "                    \"clauseCode\": \"090006\",\n" +
                "                    \"clauseName\": \"国内水路、陆路货物运输保险条款（2009版）\",\n" +
                "                    \"kindCode\": \"090191\",\n" +
                "                    \"kindName\": \"人保国内水路、陆路货运综合险\",\n" +
                "                    \"itemNo\": 1,\n" +
                "                    \"itemCode\": \"520500\",\n" +
                "                    \"itemDetailName\": \"23r23f\",\n" +
                "                    \"groupNo\": null,\n" +
                "                    \"modeCode\": \"\",\n" +
                "                    \"modeName\": \"23r32\",\n" +
                "                    \"startDate\": \"2019-09-12\",\n" +
                "                    \"startHour\": null,\n" +
                "                    \"endDate\": \"2020-09-09\",\n" +
                "                    \"endHour\": null,\n" +
                "                    \"addressNo\": null,\n" +
                "                    \"calculateFlag\": \"1\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"unitAmount\": 1000.00,\n" +
                "                    \"quantity\": 1.00,\n" +
                "                    \"unit\": \"1\",\n" +
                "                    \"value\": null,\n" +
                "                    \"amount\": 1000.00,\n" +
                "                    \"rate\": 0.80000000000,\n" +
                "                    \"shortRateFlag\": \"3\",\n" +
                "                    \"shortRate\": 100.0000,\n" +
                "                    \"prePremium\": null,\n" +
                "                    \"calPremium\": 0.80,\n" +
                "                    \"basePremium\": null,\n" +
                "                    \"benchMarkPremium\": null,\n" +
                "                    \"discount\": null,\n" +
                "                    \"adjustRate\": null,\n" +
                "                    \"unitPremium\": null,\n" +
                "                    \"premium\": 0.80,\n" +
                "                    \"deductibleRate\": null,\n" +
                "                    \"deductible\": null,\n" +
                "                    \"taxFee\": 0.02,\n" +
                "                    \"taxFee_gb\": 0.02,\n" +
                "                    \"taxFee_lb\": 0.00,\n" +
                "                    \"taxFee_ys\": null,\n" +
                "                    \"netPremium\": 0.78,\n" +
                "                    \"allTaxFee\": 0.05,\n" +
                "                    \"allNetPremium\": 0.75,\n" +
                "                    \"taxRate\": 6.00,\n" +
                "                    \"taxFlag\": \"2\",\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"prpCopyProfits\": [],\n" +
                "                    \"prpCopyItemKindDetails\": [],\n" +
                "                    \"prpCopyItemKindTaxFees\": [],\n" +
                "                    \"model\": \"\",\n" +
                "                    \"buyDate\": null,\n" +
                "                    \"iscalculateFlag\": null,\n" +
                "                    \"ratePeriod\": null,\n" +
                "                    \"userCount\": null,\n" +
                "                    \"pack\": null,\n" +
                "                    \"firstLevel\": null,\n" +
                "                    \"methodType\": null,\n" +
                "                    \"insuredQuantity\": null,\n" +
                "                    \"clauseFlag\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainProps\": [],\n" +
                "            \"prpCopyAgents\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"roleCode\": \"83242824\",\n" +
                "                        \"payNo\": 1,\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"agreementNo\": \"RULE20173204000000001         \",\n" +
                "                    \"roleType\": \"2\",\n" +
                "                    \"roleName\": \"张爱芳                        \",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"costRate\": 100.0000,\n" +
                "                    \"costFee\": 780.00,\n" +
                "                    \"remark\": \"\",\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyExtendInfos\": [],\n" +
                "            \"prpCopyMainAirLines\": [],\n" +
                "            \"prpCopyRations\": [],\n" +
                "            \"prpCopyCoinsDetails\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"itemKindNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsType\": \"1\",\n" +
                "                    \"coinsCode\": \"32048200\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"coinsRate\": 55.0000,\n" +
                "                    \"coinsAmount\": 550.00,\n" +
                "                    \"coinsPremium\": 0.44,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"itemKindNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsType\": \"2\",\n" +
                "                    \"coinsCode\": \"32010200\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"coinsRate\": 10.0000,\n" +
                "                    \"coinsAmount\": 100.00,\n" +
                "                    \"coinsPremium\": 0.08,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 3,\n" +
                "                        \"itemKindNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsType\": \"3\",\n" +
                "                    \"coinsCode\": \"006\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"coinsRate\": 35.0000,\n" +
                "                    \"coinsAmount\": 350.00,\n" +
                "                    \"coinsPremium\": 0.28,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyPlans\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": null,\n" +
                "                    \"payNo\": 1,\n" +
                "                    \"payReason\": \"R21\",\n" +
                "                    \"planDate\": \"2019-09-12\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"subsidyrate\": null,\n" +
                "                    \"planFee\": 0.40,\n" +
                "                    \"delinquentFee\": 0.40,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"payDate\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"payType\": null,\n" +
                "                    \"exchangeNo\": null,\n" +
                "                    \"paymentcomplete\": null,\n" +
                "                    \"taxFee\": 0.02,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": null,\n" +
                "                    \"payNo\": 1,\n" +
                "                    \"payReason\": \"R70\",\n" +
                "                    \"planDate\": \"2019-09-12\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"subsidyrate\": null,\n" +
                "                    \"planFee\": 0.08,\n" +
                "                    \"delinquentFee\": 0.08,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"payDate\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"payType\": null,\n" +
                "                    \"exchangeNo\": null,\n" +
                "                    \"paymentcomplete\": null,\n" +
                "                    \"taxFee\": null,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 3,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": null,\n" +
                "                    \"payNo\": 1,\n" +
                "                    \"payReason\": \"R70\",\n" +
                "                    \"planDate\": \"2019-09-12\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"subsidyrate\": null,\n" +
                "                    \"planFee\": 0.32,\n" +
                "                    \"delinquentFee\": 0.32,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"payDate\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"payType\": null,\n" +
                "                    \"exchangeNo\": null,\n" +
                "                    \"paymentcomplete\": null,\n" +
                "                    \"taxFee\": 0.02,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 4,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": null,\n" +
                "                    \"payNo\": 0,\n" +
                "                    \"payReason\": \"R30\",\n" +
                "                    \"planDate\": \"2020-03-24\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"subsidyrate\": null,\n" +
                "                    \"planFee\": 0.04,\n" +
                "                    \"delinquentFee\": 0.04,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"payDate\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"payType\": null,\n" +
                "                    \"exchangeNo\": null,\n" +
                "                    \"paymentcomplete\": null,\n" +
                "                    \"taxFee\": 0.00,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 6,\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"endorseNo\": null,\n" +
                "                    \"payNo\": 0,\n" +
                "                    \"payReason\": \"P71\",\n" +
                "                    \"planDate\": \"2020-03-24\",\n" +
                "                    \"currency\": \"CNY\",\n" +
                "                    \"subsidyrate\": null,\n" +
                "                    \"planFee\": -0.04,\n" +
                "                    \"delinquentFee\": -0.04,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"payDate\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"payType\": null,\n" +
                "                    \"exchangeNo\": null,\n" +
                "                    \"paymentcomplete\": null,\n" +
                "                    \"taxFee\": 0.00,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyFees\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"currency\": \"CNY\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"riskCode\": \"YDL\",\n" +
                "                    \"amount\": 1000.00,\n" +
                "                    \"premium\": 0.80,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"sumTaxFee\": 0.02,\n" +
                "                    \"sumNetPremium\": 0.78,\n" +
                "                    \"sumTaxFee_gb\": 0.02,\n" +
                "                    \"sumTaxFee_lb\": 0.00,\n" +
                "                    \"sumTaxFee_ys\": 0.00,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyMainChannels\": [],\n" +
                "            \"prpCopySpecialFacs\": [],\n" +
                "            \"prpCopyProfitFactors\": [],\n" +
                "            \"prpCopyAgentDetails\": [],\n" +
                "            \"prpCopyMainLoans\": [],\n" +
                "            \"prpCopyLimits\": [],\n" +
                "            \"prpCopyMainLiabs\": [],\n" +
                "            \"prpCopyCoinses\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 1,\n" +
                "                        \"currency\": \"CNY\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"mainPolicyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsCode\": \"32048200\",\n" +
                "                    \"coinsName\": \"金坛支公司\",\n" +
                "                    \"coinsType\": \"1\",\n" +
                "                    \"coinsRate\": 55.0000,\n" +
                "                    \"coinsAmount\": 550.00,\n" +
                "                    \"coinsPremium\": 0.44,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"coIdentity\": null,\n" +
                "                    \"isSendSms\": null,\n" +
                "                    \"isSendMail\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-23 10:20:15\",\n" +
                "                    \"mainProposalNo\": \"TYDL201932045000000396\",\n" +
                "                    \"subProposalNo\": \"\",\n" +
                "                    \"subApplyNo\": null,\n" +
                "                    \"mainApplyNo\": null,\n" +
                "                    \"mainEndorseNo\": \"EYDL202032045000000005\",\n" +
                "                    \"payType\": \"2111\",\n" +
                "                    \"repaylb\": null,\n" +
                "                    \"repaygb\": null,\n" +
                "                    \"agencygb\": null,\n" +
                "                    \"isInvoice\": null,\n" +
                "                    \"isPay\": null,\n" +
                "                    \"handler1Code\": \"83242824\",\n" +
                "                    \"handler1Code_uni\": \"1132137471\",\n" +
                "                    \"handlerCode\": \"83242824\",\n" +
                "                    \"handlerCode_uni\": \"1132137471\",\n" +
                "                    \"operatorCode\": null,\n" +
                "                    \"businessNature\": \"1\",\n" +
                "                    \"agentCode\": \"000011000001\",\n" +
                "                    \"basicBankCode\": \"ICBC\",\n" +
                "                    \"basicBankName\": \"中国工商银行\",\n" +
                "                    \"recBankAreaCode\": \"3204\",\n" +
                "                    \"recBankAreaName\": \"江苏省_常州市\",\n" +
                "                    \"bankCode\": \"102304099994\",\n" +
                "                    \"bankName\": \"中国工商银行常州分行\",\n" +
                "                    \"accountNo\": \"132432432222222222221111\",\n" +
                "                    \"accountName\": \"中国人民财产保险股份有限公司常州市分公司\",\n" +
                "                    \"cnaps\": \"102304099994\",\n" +
                "                    \"identifyType\": \"03\",\n" +
                "                    \"identifyNo\": \"11111333\",\n" +
                "                    \"telephone\": \"13233345434\",\n" +
                "                    \"isPrivate\": \"2\",\n" +
                "                    \"purpose\": \"我问问\",\n" +
                "                    \"cardType\": \"2\",\n" +
                "                    \"sendSms\": \"\",\n" +
                "                    \"sendMail\": \"\",\n" +
                "                    \"payeeInfoid\": \"FYAE201932040000000000023\",\n" +
                "                    \"mailAddr\": \"123@qq.com\",\n" +
                "                    \"paymodeflag\": \"P01\",\n" +
                "                    \"comType\": \"21\",\n" +
                "                    \"coinsExtraFlag\": \" 11 1\",\n" +
                "                    \"coinsSchemeCode\": null,\n" +
                "                    \"coinsSchemeName\": null,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 2,\n" +
                "                        \"currency\": \"CNY\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"mainPolicyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsCode\": \"32010200\",\n" +
                "                    \"coinsName\": \"中国人民财产保险股份有限公司南京市城东支公司\",\n" +
                "                    \"coinsType\": \"2\",\n" +
                "                    \"coinsRate\": 10.0000,\n" +
                "                    \"coinsAmount\": 100.00,\n" +
                "                    \"coinsPremium\": 0.08,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"coIdentity\": null,\n" +
                "                    \"isSendSms\": null,\n" +
                "                    \"isSendMail\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-23 10:20:15\",\n" +
                "                    \"mainProposalNo\": \"TYDL201932045000000396\",\n" +
                "                    \"subProposalNo\": \"\",\n" +
                "                    \"subApplyNo\": null,\n" +
                "                    \"mainApplyNo\": null,\n" +
                "                    \"mainEndorseNo\": \"EYDL202032045000000005\",\n" +
                "                    \"payType\": \"2112\",\n" +
                "                    \"repaylb\": null,\n" +
                "                    \"repaygb\": null,\n" +
                "                    \"agencygb\": null,\n" +
                "                    \"isInvoice\": null,\n" +
                "                    \"isPay\": null,\n" +
                "                    \"handler1Code\": \"83223025\",\n" +
                "                    \"handler1Code_uni\": \"1132030091\",\n" +
                "                    \"handlerCode\": \"凌顾尉\",\n" +
                "                    \"handlerCode_uni\": \"\",\n" +
                "                    \"operatorCode\": null,\n" +
                "                    \"businessNature\": \"1\",\n" +
                "                    \"agentCode\": \"000011000001\",\n" +
                "                    \"basicBankCode\": \"\",\n" +
                "                    \"basicBankName\": \"\",\n" +
                "                    \"recBankAreaCode\": \"\",\n" +
                "                    \"recBankAreaName\": \"\",\n" +
                "                    \"bankCode\": \"\",\n" +
                "                    \"bankName\": \"\",\n" +
                "                    \"accountNo\": \"\",\n" +
                "                    \"accountName\": \"\",\n" +
                "                    \"cnaps\": \"\",\n" +
                "                    \"identifyType\": \"\",\n" +
                "                    \"identifyNo\": \"\",\n" +
                "                    \"telephone\": \"\",\n" +
                "                    \"isPrivate\": \"2\",\n" +
                "                    \"purpose\": \"\",\n" +
                "                    \"cardType\": \"2\",\n" +
                "                    \"sendSms\": \"\",\n" +
                "                    \"sendMail\": \"\",\n" +
                "                    \"payeeInfoid\": \"\",\n" +
                "                    \"mailAddr\": \"123@qq.com\",\n" +
                "                    \"paymodeflag\": \"P01\",\n" +
                "                    \"comType\": \"20\",\n" +
                "                    \"coinsExtraFlag\": \" 0  1\",\n" +
                "                    \"coinsSchemeCode\": null,\n" +
                "                    \"coinsSchemeName\": null,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"serialNo\": 3,\n" +
                "                        \"currency\": \"CNY\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"mainPolicyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"coinsCode\": \"006\",\n" +
                "                    \"coinsName\": \"国寿资产\",\n" +
                "                    \"coinsType\": \"3\",\n" +
                "                    \"coinsRate\": 35.0000,\n" +
                "                    \"coinsAmount\": 350.00,\n" +
                "                    \"coinsPremium\": 0.28,\n" +
                "                    \"flag\": \"U\",\n" +
                "                    \"coIdentity\": null,\n" +
                "                    \"isSendSms\": null,\n" +
                "                    \"isSendMail\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-23 10:20:15\",\n" +
                "                    \"mainProposalNo\": \"\",\n" +
                "                    \"subProposalNo\": \"\",\n" +
                "                    \"subApplyNo\": null,\n" +
                "                    \"mainApplyNo\": null,\n" +
                "                    \"mainEndorseNo\": \"EYDL202032045000000005\",\n" +
                "                    \"payType\": \"2112\",\n" +
                "                    \"repaylb\": null,\n" +
                "                    \"repaygb\": null,\n" +
                "                    \"agencygb\": null,\n" +
                "                    \"isInvoice\": null,\n" +
                "                    \"isPay\": null,\n" +
                "                    \"handler1Code\": \"\",\n" +
                "                    \"handler1Code_uni\": \"\",\n" +
                "                    \"handlerCode\": \"13163171\",\n" +
                "                    \"handlerCode_uni\": \"1232010062\",\n" +
                "                    \"operatorCode\": null,\n" +
                "                    \"businessNature\": \"\",\n" +
                "                    \"agentCode\": \"\",\n" +
                "                    \"basicBankCode\": \"ABC\",\n" +
                "                    \"basicBankName\": \"农业银行\",\n" +
                "                    \"recBankAreaCode\": \"1100\",\n" +
                "                    \"recBankAreaName\": \"北京市_北京市\",\n" +
                "                    \"bankCode\": \"103100015132\",\n" +
                "                    \"bankName\": \"中国农业银行股份有限公司北京兴怀大街分理处\",\n" +
                "                    \"accountNo\": \"2325555222244444\",\n" +
                "                    \"accountName\": \"22222\",\n" +
                "                    \"cnaps\": \"103100015132\",\n" +
                "                    \"identifyType\": \"04\",\n" +
                "                    \"identifyNo\": \"1111\",\n" +
                "                    \"telephone\": \"13244455432\",\n" +
                "                    \"isPrivate\": \"2\",\n" +
                "                    \"purpose\": \"23232\",\n" +
                "                    \"cardType\": \"2\",\n" +
                "                    \"sendSms\": \"\",\n" +
                "                    \"sendMail\": \"\",\n" +
                "                    \"payeeInfoid\": \"FYAE201932040000000000024\",\n" +
                "                    \"mailAddr\": \"123@qq.com\",\n" +
                "                    \"paymodeflag\": \"P01\",\n" +
                "                    \"comType\": \"90\",\n" +
                "                    \"coinsExtraFlag\": \" 0  1\",\n" +
                "                    \"coinsSchemeCode\": null,\n" +
                "                    \"coinsSchemeName\": null,\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyCargoDetails\": [],\n" +
                "            \"prpCopyShipDrivers\": [],\n" +
                "            \"prpCopyClauseConfines\": [],\n" +
                "            \"prpCopyClauses\": [],\n" +
                "            \"prpCopyContriutions\": [],\n" +
                "            \"prpCopyMainCommons\": [\n" +
                "                {\n" +
                "                    \"id\": {\n" +
                "                        \"applyNo\": \"DYDL202032045000000076\",\n" +
                "                        \"pkey\": \"PYDL201932045000000277\"\n" +
                "                    },\n" +
                "                    \"policyNo\": \"PYDL201932045000000277\",\n" +
                "                    \"qualityLevel\": null,\n" +
                "                    \"specialFlag\": \" \",\n" +
                "                    \"ext1\": null,\n" +
                "                    \"ext2\": null,\n" +
                "                    \"ext3\": \"\",\n" +
                "                    \"resourceCode\": null,\n" +
                "                    \"resourceName\": null,\n" +
                "                    \"insertTimeForHis\": \"2020-03-22 17:19:49\",\n" +
                "                    \"operateTimeForHis\": \"2020-03-23 10:36:00\",\n" +
                "                    \"newBusinessNature\": \"110\",\n" +
                "                    \"scmsAuditNotion\": null,\n" +
                "                    \"pay_method\": null,\n" +
                "                    \"platformProjectCode\": null,\n" +
                "                    \"platformProjectName\": null,\n" +
                "                    \"handler1Code_uni\": \"83242824\",\n" +
                "                    \"handlerCode_uni\": \"83242824\",\n" +
                "                    \"commonFlag\": \"0         0\",\n" +
                "                    \"otherPolicyName\": null,\n" +
                "                    \"groupName\": null,\n" +
                "                    \"isHPDriveCus\": \"0\",\n" +
                "                    \"startTime\": \"00:00\",\n" +
                "                    \"endTime\": \"00:00\",\n" +
                "                    \"salesCode\": null,\n" +
                "                    \"electronic\": \"0\",\n" +
                "                    \"electronicTitle\": null,\n" +
                "                    \"electronicPhone\": null,\n" +
                "                    \"socialinsPay\": null,\n" +
                "                    \"socialinsNo\": null,\n" +
                "                    \"projectCode\": null,\n" +
                "                    \"projectName\": null,\n" +
                "                    \"priorityFlag\": null,\n" +
                "                    \"priorityMessage\": null,\n" +
                "                    \"isAccredit\": null,\n" +
                "                    \"accreditType\": null,\n" +
                "                    \"accreditDate\": null,\n" +
                "                    \"othflag\": null,\n" +
                "                    \"bankFlowNo\": \"121212\",\n" +
                "                    \"tkey\": \"2020-03-22 17:19:49\",\n" +
                "                    \"sealNum\": null,\n" +
                "                    \"classify\": null,\n" +
                "                    \"overSeas\": \"0\",\n" +
                "                    \"isClaim\": null,\n" +
                "                    \"isCondition\": null,\n" +
                "                    \"unifiedInsurance\": \"\",\n" +
                "                    \"electronicEmail\": null,\n" +
                "                    \"isRenewalTeam\": null,\n" +
                "                    \"keyAccountCode\": null,\n" +
                "                    \"isRenewal\": null,\n" +
                "                    \"isGIvesff\": null,\n" +
                "                    \"isStatistics\": null,\n" +
                "                    \"isInsureRate\": null,\n" +
                "                    \"busiAccountType\": \" \",\n" +
                "                    \"isPStage\": \" \",\n" +
                "                    \"visaCode\": null,\n" +
                "                    \"visaPrintCode\": null,\n" +
                "                    \"visaNo\": null,\n" +
                "                    \"isVisaCancel\": null,\n" +
                "                    \"isPoverty\": null,\n" +
                "                    \"isTargetedPoverty\": null,\n" +
                "                    \"coMakecom\": null,\n" +
                "                    \"coOperatorcode\": null,\n" +
                "                    \"inputType\": null,\n" +
                "                    \"deliverFlag\": null,\n" +
                "                    \"deliverType\": null,\n" +
                "                    \"addressee\": null,\n" +
                "                    \"deliverTel\": null,\n" +
                "                    \"deliverAddr\": null,\n" +
                "                    \"isVsCard\": null,\n" +
                "                    \"subinformation\": null,\n" +
                "                    \"isRapidCalPremium\": null,\n" +
                "                    \"externalPayFlag\": null,\n" +
                "                    \"ownerFlag\": null,\n" +
                "                    \"signTag\": null,\n" +
                "                    \"signState\": null,\n" +
                "                    \"invokeFlag\": null,\n" +
                "                    \"invoiceCode\": null,\n" +
                "                    \"reviewerName\": null,\n" +
                "                    \"receivableFlag\": null,\n" +
                "                    \"internationalFlag\": null,\n" +
                "                    \"recallFlag\": null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"prpCopyItemCreditOths\": [],\n" +
                "            \"isAutoePolicy\": null,\n" +
                "            \"handler1Code_uni\": null,\n" +
                "            \"personOri\": null,\n" +
                "            \"salesCode\": null,\n" +
                "            \"actualProduct\": null,\n" +
                "            \"currency\": \"CNY\",\n" +
                "            \"dmFlag\": \"\",\n" +
                "            \"insuredCount\": null,\n" +
                "            \"productCode\": null,\n" +
                "            \"productName\": null,\n" +
                "            \"comName\": null,\n" +
                "            \"agentName\": null,\n" +
                "            \"handlerName\": null,\n" +
                "            \"handler1Name\": null,\n" +
                "            \"operatorName\": null,\n" +
                "            \"prpCopyClauseplans\": []\n" +
                "        }\n" +
                "    ]\n" +
                "}");
        String jsonresp = sb.toString();
        JSONObject jsonrespObject = JSONObject.parseObject(jsonresp);
        JSONArray ja = new JSONArray();
        getJAByField("data",jsonrespObject,ja);
        System.out.println(ja);

       StringBuffer strBuf = new StringBuffer();
       String value ="[{\"endhour\":24,\"endorseno\":\"0\",\"sumvalue\":0.00,\"sumamount\":1000.00,\"startdate\":1568217600000,\"sumquantity\":0,\"applyno\":\"TYDL201932045000000396\",\"classcode\":\"09\",\"operatetimeforhis\":1568189386000,\"endorsetimes\":0,\"underwritename\":\"自动核保\",\"sumtaxfee\":0.02,\"enddate\":1599408000000,\"inserttimeforhis\":1568189392000,\"riskcode\":\"YDL\",\"checkcode\":\"\",\"handlercode\":\"83242824\",\"reinsflag\":\"0000099000\",\"flag\":\"0   7\",\"underwritecode\":\"UnderWrite\",\"operatedate\":1568189386000,\"operatesite\":\"中国\",\"comcode\":\"32048200\",\"paytimes\":1,\"paymode\":\"2\",\"starthour\":0,\"jfeeflag\":\"0\",\"inputtime\":1568189347000,\"tkey\":1568189385000,\"underwriteenddate\":\"2019-09-11\",\"contractno\":\"合同号\",\"policytype\":\"19\",\"checkopinion\":\"\",\"sumsubprem\":0.00,\"validdate\":\"2020-10-15\",\"arbitboardname\":\"\",\"makecom\":\"32048200\",\"statisticsym\":\"201909\",\"sumpremium\":0.80,\"checkupcode\":\"\",\"projectcode\":\"\",\"language\":\"C\",\"operatorcode\":\"A320000058\",\"arguesolution\":\"1\",\"othflag\":\"000000YY00\",\"judicalscope\":\"01\",\"sumnetpremium\":0.78,\"businessnature\":\"1\",\"coinsflag\":\"11\",\"agriflag\":\"0\",\"checkflag\":\"4\",\"allinsflag\":\"0\",\"underwriteflag\":\"3\",\"remark\":\"\",\"handler1code\":\"83242824\",\"agentcode\":\"000011000001\",\"isreins\":\"0\",\"policysort\":\"1\",\"pkey\":\"PYDL201932045000000277\",\"policyno\":\"PYDL201932045000000277\",\"inputflag\":\"\",\"crossflag\":\"0\"},{\"endhour\":24,\"endorseno\":\"0\",\"sumvalue\":0.00,\"sumamount\":1000.00,\"startdate\":1568217600000,\"sumquantity\":0,\"applyno\":\"DYDL202032045000000076\",\"classcode\":\"09\",\"operatetimeforhis\":1584930015000,\"endorsetimes\":0,\"underwritename\":\"自动核保\",\"sumtaxfee\":0.02,\"enddate\":1599321600000,\"inserttimeforhis\":1584868789000,\"riskcode\":\"YDL\",\"checkcode\":\"\",\"validhour\":24,\"handlercode\":\"83242824\",\"reinsflag\":\"0000099000\",\"flag\":\"U\",\"underwritecode\":\"UnderWrite\",\"operatedate\":1568189386000,\"operatesite\":\"中国\",\"comcode\":\"32048200\",\"paytimes\":1,\"paymode\":\"2\",\"starthour\":0,\"jfeeflag\":\"0\",\"currency\":\"CNY\",\"inputtime\":1568189347000,\"tkey\":1584868789000,\"underwriteenddate\":\"2019-09-11\",\"contractno\":\"合同号\",\"policytype\":\"19\",\"checkopinion\":\"\",\"sumsubprem\":0.00,\"validdate\":\"2020-10-15\",\"arbitboardname\":\"\",\"makecom\":\"32048200\",\"statisticsym\":\"201909\",\"sumpremium\":0.80,\"checkupcode\":\"\",\"projectcode\":\"\",\"language\":\"C\",\"operatorcode\":\"A320000058\",\"arguesolution\":\"1\",\"othflag\":\"000000YY00\",\"dmflag\":\"\",\"judicalscope\":\"01\",\"sumnetpremium\":0.78,\"businessnature\":\"1\",\"coinsflag\":\"11\",\"agriflag\":\"0\",\"checkflag\":\"4\",\"allinsflag\":\"0\",\"underwriteflag\":\"1\",\"remark\":\"\",\"handler1code\":\"83242824\",\"agentcode\":\"000011000001\",\"isreins\":\"0\",\"policysort\":\"1\",\"pkey\":\"PYDL201932045000000277\",\"policyno\":\"PYDL201932045000000277\",\"inputflag\":\"\",\"crossflag\":\"0\"}] ";
        JSONArray dbJa = JSONArray.parseArray(value);
        //System.out.println(dbJa);
        ArrayList<String> objects = new ArrayList<>();
        objects.add("endDate");
        objects.add("startDate");
        listCompare(ja,dbJa,strBuf,"data",objects);
        System.out.println(strBuf);

    }
}

