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
     * 从JSON数据里取出对应的节点数据
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
            result = value.substring(0, value.indexOf("."));
        }
        if (value.contains("T")) {
            result = value.replace("T", " ");
        }
        if (value.contains("00:00:00")) {
            result = value.substring(0, 10);
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
     *根据返回报文的节点名进行数据库表的组装，以及节点名与表名的映射关系组装
     * @param value
     * @param dbv
     * @param rdbv
     * @param table_field
     */
    public static void getFiledName(String value, ArrayList<String> dbv, Map<Object, String> rdbv, Map<String, String> table_field)
    {
        JSONObject jsonObject = JSONObject.parseObject(value).getJSONObject("data");
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


    public static void main(String[] args)
    {
        StringBuffer stringBuffer = new StringBuffer();
        String value = "{\"status\":0,\"statusText\":\"Success\",\"data\":{\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"classCode\":\"06\",\"riskCode\":\"EAD\",\"riskName\":\"机动车车上人员补充意外伤害保险 \",\"projectCode\":null,\"contractNo\":null,\"policySort\":\"1\",\"businessNature\":\"0\",\"language\":\"C\",\"policyType\":\"99\",\"agriFlag\":\"0\",\"operateDate\":\"2020-12-13 00:00:00\",\"startDate\":\"2020-12-13 00:00:00\",\"endDate\":\"2021-12-12 00:00:00\",\"startHour\":0,\"endHour\":24,\"disRate\":null,\"sumValue\":0.00,\"sumAmount\":50000.00,\"sumDiscount\":null,\"sumPremiumB4Discount\":null,\"couponAmount\":null,\"couponPremium\":null,\"minPremium\":null,\"sumPremium\":100.00,\"sumSubPrem\":0.00,\"sumQuantity\":null,\"policyCount\":null,\"judicalScope\":\"01\",\"argueSolution\":\"1\",\"arbitBoardName\":\"\",\"payTimes\":1,\"makeCom\":\"33080101\",\"operateSite\":null,\"comCode\":\"33080101\",\"handlerCode\":\"16213921\",\"handler1Code\":\"16213921\",\"checkFlag\":\"4\",\"checkUpCode\":null,\"checkOpinion\":null,\"underWriteCode\":null,\"underWriteName\":null,\"operatorCode\":\"16213921\",\"inputTime\":\"2020-12-11 10:47:08\",\"underWriteEndDate\":null,\"statisticsYM\":null,\"agentCode\":\"000002000001\",\"coinsFlag\":\"00\",\"reinsFlag\":\"0000000000\",\"allinsFlag\":\"0\",\"underWriteFlag\":\"0\",\"jfeeFlag\":\"1\",\"inputFlag\":\"0\",\"undwrtSubmitDate\":null,\"othFlag\":\"000000YY00\",\"remark\":null,\"checkCode\":null,\"flag\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"payMode\":\"1\",\"payCode\":null,\"crossFlag\":\"0\",\"batchGroupNo\":null,\"sumTaxFee\":5.66,\"sumNetPremium\":94.34,\"prePremium\":null,\"pkey\":\"TEAD202033086000000392\",\"tkey\":\"2020-12-11 10:47:11\",\"currency\":\"CNY\",\"dmFlag\":\"A\",\"handler1Code_uni\":\"1233130777\",\"handlerCode_uni\":\"1233130777\",\"isAutoePolicy\":\"1\",\"salesCode\":\"A000002258\",\"personOri\":\"6\",\"productCode\":null,\"productName\":null,\"approverCode\":null,\"pureRate\":null,\"discount\":null,\"insuredCount\":1,\"auditNo\":null,\"auditNoToE\":null,\"crossSellType\":\"3\",\"inputSumPremium\":null,\"dutySumNetPremium\":null,\"freeSumNetPremium\":null,\"orderNo\":null,\"orderFlag\":null,\"policyPageVo\":null,\"prpCmainAccs\":[],\"prpCmainExts\":[],\"prpCmainBonds\":[],\"prpCextendInfos\":[],\"prpCmainAirLines\":[],\"prpCcommissions\":[],\"prpCcoeffs\":[],\"prpCmainAgris\":[],\"prpCprojects\":[],\"prpCprofitFactors\":[],\"prpCclauseplans\":[],\"prpCpayeeAccounts\":[],\"actualProduct\":null,\"prpCmainExtraVo\":null,\"Employees\":[],\"Props\":[],\"AgentDetails\":[],\"shipDrivers\":[],\"Drivers\":[],\"Addresses\":[],\"InsuredIdvLists\":[],\"Crosses\":[],\"Subs\":[],\"Agents\":[],\"Credits\":[],\"Constructs\":[],\"Itemkinds\":[{\"id\":{\"pkey\":\"TEAD202033086000000392\",\"itemKindNo\":1},\"riskCode\":\"EAD\",\"familyNo\":null,\"familyName\":null,\"projectCode\":\"暂时写死\",\"clauseCode\":\"060047\",\"clauseName\":\"意外伤害保险条款\",\"kindCode\":\"060066\",\"kindName\":\"意外身故、残疾给付\",\"itemNo\":1,\"itemCode\":null,\"itemDetailName\":null,\"groupNo\":1,\"modeCode\":\"EAD0000001\",\"modeName\":\"非营业车湖北100元\",\"startDate\":\"2020-12-13\",\"startHour\":0,\"endDate\":\"2021-12-11\",\"endHour\":24,\"model\":null,\"buyDate\":null,\"addressNo\":null,\"calculateFlag\":\"1\",\"currency\":\"CNY\",\"unitAmount\":50000.00,\"quantity\":1,\"unit\":\"1\",\"value\":null,\"amount\":50000.00,\"ratePeriod\":null,\"rate\":2.00000000000,\"shortRateFlag\":\"3\",\"shortRate\":100.0000,\"prePremium\":null,\"calPremium\":100.00,\"basePremium\":null,\"benchMarkPremium\":null,\"discount\":1.000000,\"adjustRate\":null,\"unitPremium\":100.00,\"premiumB4Discount\":null,\"premium\":100.00,\"deductibleRate\":null,\"deductible\":null,\"taxFee\":5.66,\"taxFee_ys\":null,\"taxFee_gb\":0.00,\"taxFee_lb\":0.00,\"netPremium\":94.34,\"allTaxFee\":5.66,\"allNetPremium\":94.34,\"taxRate\":6.00,\"taxFlag\":\"2\",\"flag\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"policyNo\":null,\"proposalNo\":\"TEAD202033086000000392\",\"tkey\":\"2020-12-11 10:47:11\",\"prpCprofits\":[],\"iscalculateFlag\":null,\"userCount\":null,\"pack\":null,\"firstLevel\":null,\"methodType\":null,\"insuredQuantity\":null,\"clauseFlag\":\"1\",\"itemKindFactorFlag\":null,\"prpCitemKindTaxFees\":[],\"ItemKindDetails\":[]}],\"Limits\":[],\"Loans\":[],\"Engages\":[{\"id\":{\"pkey\":\"TEAD202033086000000392\",\"serialNo\":1},\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"tkey\":\"2020-12-11 10:47:11\",\"riskCode\":\"EAD\",\"clauseCode\":\"916204\",\"clauseName\":\"驾意险（仅承保驾驶车辆过程中）\",\"clauses\":\"根据《附加调整承保期间保险条款（2009版）》，本保险合同仅承担被保险人作为驾驶员在车辆行驶过程中或为维护车辆继续运行（包括加油、加水、故障修理、换胎）的临时停放过程中发生的意外伤害事故。\",\"flag\":\"N\",\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"groupNo\":null,\"relatedFlag\":null,\"relatedContent\":null}],\"Insureds\":[{\"id\":{\"pkey\":\"TEAD202033086000000392\",\"serialNo\":1},\"serialNo\":null,\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"tkey\":\"2020-12-11 10:47:11\",\"riskCode\":\"EAD\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"21001004961200\",\"insuredName\":\"赵小杰\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京市市辖区东城区\",\"insuredNature\":null,\"insuredFlag\":\"10000000000000000000000000000A\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"0\",\"relateSerialNo\":null,\"identifyType\":\"01\",\"identifyNumber\":\"130724198803120810\",\"unifiedSocialCreditCode\":null,\"creditLevel\":null,\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":null,\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":\"北京是辖区朝阳区双井\",\"postCode\":null,\"phoneNumber\":\"15600928784\",\"faxNumber\":null,\"mobile\":\"15600928784\",\"netAddress\":null,\"email\":\"11@qqq.com\",\"dateValid\":null,\"startDate\":\"2020-12-13 00:00:00\",\"endDate\":\"2021-12-12 00:00:00\",\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"1\",\"age\":32,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":\"2\",\"flag\":null,\"warningFlag\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"blackFlag\":null,\"importSerialNo\":null,\"prpCinsuredCreditInvests\":[],\"groupCode\":null,\"groupName\":null,\"dweller\":\"A\",\"customerLevel\":null,\"insuredPYName\":null,\"groupNo\":null,\"itemNo\":null,\"importFlag\":null,\"smsFlag\":null,\"emailFlag\":null,\"sendPhone\":null,\"sendEmail\":null,\"subPolicyNo\":null,\"socialSecurityNo\":null,\"electronicflag\":\"0\",\"insuredSort\":null,\"isHealthSurvey\":null,\"InsuredNatures\":[{\"id\":{\"pkey\":\"TEAD202033086000000392\",\"serialNo\":1},\"serialNo\":null,\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"tkey\":\"2020-12-11 10:47:11\",\"insuredFlag\":\"10000000000000000000000000000A\",\"sex\":\"1\",\"age\":32,\"birthday\":\"1988-03-12\",\"health\":null,\"jobTitle\":null,\"localWorkYears\":null,\"education\":null,\"totalWorkYears\":null,\"unit\":null,\"unitPhoneNumber\":null,\"unitAddress\":null,\"unitPostCode\":null,\"unitType\":null,\"dutyLevel\":null,\"dutyType\":null,\"occupationCode\":null,\"houseProperty\":null,\"localPoliceStation\":null,\"roomAddress\":null,\"roomPostCode\":null,\"selfMonthIncome\":null,\"familyMonthIncome\":null,\"incomeSource\":null,\"roomPhone\":null,\"mobile\":null,\"familySumQuantity\":null,\"marriage\":null,\"spouseName\":null,\"spouseBornDate\":null,\"spouseId\":null,\"spouseMobile\":null,\"spouseUnit\":null,\"spouseJobTitle\":null,\"spouseUnitPhone\":null,\"flag\":null,\"carType\":null,\"disablePartAndLevel\":null,\"moreLoanHouseFlag\":null,\"nation\":null,\"poorFlag\":null,\"licenseNo\":null,\"getLicenseDate\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"educationCode\":null,\"contactNo\":null,\"contactName\":null,\"certificationDate\":null,\"certificationNo\":null,\"addressCount\":null,\"importFlag\":null,\"socialFlag\":null,\"cardAmount\":null,\"usedAmount\":null,\"payAmount\":null,\"isPoverty\":null,\"importSerialNo\":null}],\"InsuredArtifs\":[]},{\"id\":{\"pkey\":\"TEAD202033086000000392\",\"serialNo\":2},\"serialNo\":null,\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"tkey\":\"2020-12-11 10:47:11\",\"riskCode\":\"EAD\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"21001004961200\",\"insuredName\":\"赵小杰\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京市市辖区东城区\",\"insuredNature\":null,\"insuredFlag\":\"010000000000000000000000000000\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"0\",\"relateSerialNo\":null,\"identifyType\":\"01\",\"identifyNumber\":\"130724198803120810\",\"unifiedSocialCreditCode\":null,\"creditLevel\":null,\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":null,\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":\"北京是辖区朝阳区双井\",\"postCode\":null,\"phoneNumber\":\"15600928784\",\"faxNumber\":null,\"mobile\":\"15600928784\",\"netAddress\":null,\"email\":\"11@qqq.com\",\"dateValid\":null,\"startDate\":\"2020-12-13 00:00:00\",\"endDate\":\"2021-12-12 00:00:00\",\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"1\",\"age\":32,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":\"2\",\"flag\":null,\"warningFlag\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"blackFlag\":null,\"importSerialNo\":null,\"prpCinsuredCreditInvests\":[],\"groupCode\":null,\"groupName\":null,\"dweller\":\"A\",\"customerLevel\":null,\"insuredPYName\":null,\"groupNo\":null,\"itemNo\":null,\"importFlag\":null,\"smsFlag\":null,\"emailFlag\":null,\"sendPhone\":null,\"sendEmail\":null,\"subPolicyNo\":null,\"socialSecurityNo\":null,\"electronicflag\":\"0\",\"insuredSort\":null,\"isHealthSurvey\":null,\"InsuredNatures\":[{\"id\":{\"pkey\":\"TEAD202033086000000392\",\"serialNo\":2},\"serialNo\":null,\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"tkey\":\"2020-12-11 10:47:11\",\"insuredFlag\":\"010000000000000000000000000000\",\"sex\":\"1\",\"age\":32,\"birthday\":\"1988-03-12\",\"health\":null,\"jobTitle\":null,\"localWorkYears\":null,\"education\":null,\"totalWorkYears\":null,\"unit\":null,\"unitPhoneNumber\":null,\"unitAddress\":null,\"unitPostCode\":null,\"unitType\":null,\"dutyLevel\":null,\"dutyType\":null,\"occupationCode\":null,\"houseProperty\":null,\"localPoliceStation\":null,\"roomAddress\":null,\"roomPostCode\":null,\"selfMonthIncome\":null,\"familyMonthIncome\":null,\"incomeSource\":null,\"roomPhone\":null,\"mobile\":null,\"familySumQuantity\":null,\"marriage\":null,\"spouseName\":null,\"spouseBornDate\":null,\"spouseId\":null,\"spouseMobile\":null,\"spouseUnit\":null,\"spouseJobTitle\":null,\"spouseUnitPhone\":null,\"flag\":null,\"carType\":null,\"disablePartAndLevel\":null,\"moreLoanHouseFlag\":null,\"nation\":null,\"poorFlag\":null,\"licenseNo\":null,\"getLicenseDate\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"educationCode\":null,\"contactNo\":null,\"contactName\":null,\"certificationDate\":null,\"certificationNo\":null,\"addressCount\":null,\"importFlag\":null,\"socialFlag\":null,\"cardAmount\":null,\"usedAmount\":null,\"payAmount\":null,\"isPoverty\":null,\"importSerialNo\":null}],\"InsuredArtifs\":[]}],\"Coins\":[],\"SpecialFacs\":[],\"Batches\":[],\"Commissions\":[],\"Cargos\":[],\"Plans\":[{\"tkey\":\"2020-12-11 10:47:11\",\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"id\":{\"pkey\":\"TEAD202033086000000392\",\"serialNo\":1},\"endorseNo\":null,\"payNo\":1,\"payReason\":\"R21\",\"planDate\":\"2020-12-13\",\"currency\":\"CNY\",\"subsidyrate\":null,\"planFee\":100.00,\"delinquentFee\":100.00,\"flag\":null,\"payDate\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"payType\":null,\"exchangeNo\":null,\"paymentcomplete\":null,\"taxFee\":5.66}],\"CoinsDetails\":[],\"Liabs\":[],\"Confines\":[],\"Rations\":[{\"id\":{\"modeCode\":\"1\",\"pkey\":\"TEAD202033086000000392\"},\"modeName\":\"非营业车湖北100元\",\"planCode\":\"EAD0000001\",\"serialNo\":null,\"itinerary\":null,\"sex\":null,\"age\":null,\"occupationCode\":null,\"jobTitle\":null,\"quantity\":1,\"rationCount\":1,\"groupDiscount\":null,\"insuredFlag\":null,\"countryCode\":null,\"sickRoomLevel\":null,\"journeyBack\":null,\"journeyEnd\":null,\"journeyStart\":null,\"remark\":null,\"updateFlag\":null,\"flag\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"tkey\":\"2020-12-11 10:47:11\",\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"discountType\":null,\"discountMode\":null,\"discountValue\":null,\"unitPremium\":null,\"premiumB4Discount\":null,\"premium\":null,\"planTypeCode\":null}],\"Items\":[],\"Fees\":[{\"id\":{\"pkey\":\"TEAD202033086000000392\",\"currency\":\"CNY\"},\"proposalNo\":\"TEAD202033086000000392\",\"policyNo\":null,\"tkey\":\"2020-12-11 10:47:11\",\"riskCode\":\"EAD\",\"amount\":50000.00,\"premiumB4Discount\":null,\"premium\":100.00,\"flag\":\"\",\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"sumTaxFee\":5.66,\"sumTaxFee_ys\":0.00,\"sumNetPremium\":94.34,\"sumTaxFee_gb\":0.00,\"sumTaxFee_lb\":0.00}],\"Renewals\":[],\"CargoDetails\":[],\"Cprotocols\":[],\"Clauses\":[],\"Contributions\":[],\"CreditOths\":[],\"Commons\":[{\"pkey\":\"TEAD202033086000000392\",\"tkey\":\"2020-12-11 10:47:11\",\"proposalNo\":\"TEAD202033086000000392\",\"specialFlag\":\"               \",\"ext1\":null,\"ext2\":null,\"ext3\":null,\"resourceCode\":null,\"resourceName\":null,\"qualityLevel\":null,\"insertTimeForHis\":\"2020-12-11 10:47:11\",\"operateTimeForHis\":\"2020-12-11 10:47:11\",\"newBusinessNature\":\"020\",\"scmsAuditNotion\":\"无\",\"pay_method\":null,\"platformProjectCode\":\"MST000001\",\"handler1Code_uni\":\"1233130777\",\"handlerCode_uni\":\"1233130777\",\"commonFlag\":\"0 0 0     0\",\"otherPolicyName\":null,\"groupName\":null,\"isHPDriveCus\":\"0\",\"startTime\":\"00:00\",\"endTime\":\"00:00\",\"salesCode\":\"A000002258\",\"electronic\":\"0\",\"electronicTitle\":null,\"electronicPhone\":null,\"socialinsPay\":null,\"socialinsNo\":null,\"projectCode\":null,\"projectName\":null,\"priorityFlag\":null,\"priorityMessage\":null,\"isAccredit\":null,\"accreditType\":null,\"accreditDate\":null,\"bankFlowNo\":null,\"sealNum\":null,\"policyNo\":null,\"classify\":null,\"overSeas\":\"0\",\"isClaim\":null,\"isCondition\":null,\"unifiedInsurance\":null,\"electronicEmail\":null,\"isRenewalTeam\":null,\"keyAccountCode\":null,\"isRenewal\":null,\"isGIvesff\":null,\"isStatistics\":null,\"isInsureRate\":null,\"busiAccountType\":null,\"isPStage\":\"0\",\"visaCode\":null,\"visaPrintCode\":\"EEEADC00181\",\"visaNo\":null,\"isVisaCancel\":null,\"internetCode\":null,\"isPoverty\":null,\"isTargetedPoverty\":null,\"coMakecom\":null,\"coOperatorcode\":null,\"inputType\":null,\"deliverFlag\":null,\"deliverType\":null,\"addressee\":null,\"deliverTel\":null,\"deliverAddr\":null,\"isVsCard\":null,\"subinformation\":null,\"isRapidCalPremium\":null,\"externalPayFlag\":null,\"ownerFlag\":null,\"signTag\":null,\"signState\":null,\"transFlag\":null,\"invokeFlag\":null,\"invoiceCode\":null,\"reviewerName\":null,\"receivableFlag\":null,\"internationalFlag\":null,\"policyFactorFlag\":null,\"recallFlag\":null}],\"Coupon\":null,\"InsuredCataLists\":[]}}";
        JSONObject jsonObject = JSONObject.parseObject(value);
        JSONArray objects1 = new JSONArray();
        getJAByField("Itemkinds",jsonObject,objects1);
        String a = "[{\"netpremium\":94.34,\"endhour\":24,\"modecode\":\"EAD0000001\",\"projectcode\":\"暂时写死\",\"discount\":1.000000,\"startdate\":\"2020-12-13\",\"calpremium\":100.00,\"taxfee_gb\":0.00,\"clauseflag\":\"1\",\"clausecode\":\"060047\",\"operatetimeforhis\":1607654831000,\"taxrate\":6.00,\"clausename\":\"意外伤害保险条款\",\"itemno\":1,\"unit\":\"1\",\"enddate\":\"2021-12-11\",\"taxflag\":\"2\",\"modename\":\"非营业车湖北100元\",\"inserttimeforhis\":1607654831000,\"taxfee_lb\":0.00,\"riskcode\":\"EAD\",\"itemkindno\":1,\"unitamount\":50000.00,\"calculateflag\":\"1\",\"proposalno\":\"TEAD202033086000000392\",\"shortrateflag\":\"3\",\"premium\":100.00,\"alltaxfee\":5.66,\"rate\":2.00000000000,\"allnetpremium\":94.34,\"starthour\":0,\"currency\":\"CNY\",\"pkey\":\"TEAD202033086000000392\",\"tkey\":1607654831000,\"unitpremium\":100.00,\"amount\":50000.00,\"quantity\":1.00,\"kindname\":\"意外身故、残疾给付\",\"shortrate\":100.0000,\"kindcode\":\"060066\",\"taxfee\":5.66,\"groupno\":1}] ";
        JSONArray objects = JSONArray.parseArray(a);
        listCompare(objects1,objects,stringBuffer,"Itemkinds",null);
    }
}
