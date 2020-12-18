package com.cccc.coco.utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class CompareUtil
{
    /**
     *根据返回报文的节点名进行数据库表的组装，以及节点名与表名的映射关系组装
     * @param value     请求或者返回报文
     * @param dbv       组装的数据库表名集合
     * @param rdbv      组装要进行对比的映射关系
     * @param table_field       组装好的映射关系
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




    public static void main(String[] args) throws ParseException
    {
      /* String value ="2020-03-31";
        String date = isDate(value);
        System.out.println(date);*/
        String value ="{\"status\":0,\"statusText\":\"Success\",\"data\":{\"errorMessage\":null,\"errorCode\":null,\"PolicyMain\":{\"proposalNo\":\"\",\"policyNo\":\"\",\"classCode\":\"06\",\"riskCode\":\"EJQ\",\"riskName\":\"营运交通工具乘客意外伤害保险\",\"projectCode\":\"\",\"contractNo\":\"\",\"policySort\":\"1\",\"businessNature\":\"4\",\"language\":\"C\",\"policyType\":\"04\",\"agriFlag\":\"0\",\"operateDate\":\"2020-12-18 15:45:20\",\"startDate\":\"2020-12-19 00:00:00\",\"endDate\":\"2021-12-19 00:00:00\",\"startHour\":0,\"endHour\":24,\"disRate\":null,\"sumValue\":0.00,\"sumAmount\":2000000.00,\"sumDiscount\":null,\"sumPremiumB4Discount\":null,\"couponAmount\":null,\"couponPremium\":null,\"minPremium\":null,\"sumPremium\":60.00,\"sumSubPrem\":0.00,\"sumQuantity\":2,\"policyCount\":1,\"judicalScope\":\"01\",\"argueSolution\":\"1\",\"arbitBoardName\":\"\",\"payTimes\":1,\"makeCom\":\"33010401\",\"operateSite\":null,\"comCode\":\"33010401\",\"handlerCode\":\"1294010382\",\"handler1Code\":\"1294010382\",\"checkFlag\":\"0\",\"checkUpCode\":\"\",\"checkOpinion\":null,\"underWriteCode\":\"UnderWrite\",\"underWriteName\":\"自动核保\",\"operatorCode\":\"1294010382\",\"inputTime\":\"2020-12-18 15:45:20\",\"underWriteEndDate\":\"2020-12-08\",\"statisticsYM\":null,\"agentCode\":\"\",\"coinsFlag\":\"00\",\"reinsFlag\":\"0000000000\",\"allinsFlag\":\"0\",\"underWriteFlag\":\"0\",\"jfeeFlag\":\"0\",\"inputFlag\":\"0\",\"undwrtSubmitDate\":\"2020-12-08\",\"othFlag\":\"100000YY00\",\"remark\":null,\"checkCode\":null,\"flag\":\"          \",\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"payMode\":\"09\",\"payCode\":null,\"crossFlag\":\"0\",\"batchGroupNo\":null,\"sumTaxFee\":3.40,\"sumNetPremium\":56.60,\"prePremium\":null,\"pkey\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"currency\":\"CNY\",\"dmFlag\":\"A\",\"handler1Code_uni\":\"1294010382\",\"handlerCode_uni\":\"1294010382\",\"isAutoePolicy\":\"0\",\"salesCode\":null,\"personOri\":\"4\",\"productCode\":null,\"productName\":null,\"approverCode\":\"1294010382\",\"pureRate\":null,\"discount\":null,\"insuredCount\":null,\"auditNo\":null,\"auditNoToE\":null,\"crossSellType\":\"3\",\"inputSumPremium\":null,\"dutySumNetPremium\":null,\"freeSumNetPremium\":null,\"orderNo\":null,\"orderFlag\":null,\"policyPageVo\":null,\"prpCmainAccs\":[],\"prpCmainExts\":[],\"prpCmainBonds\":[],\"prpCextendInfos\":[],\"prpCmainAirLines\":[],\"prpCcommissions\":[],\"prpCcoeffs\":[],\"prpCmainAgris\":[],\"prpCprojects\":[],\"prpCprofitFactors\":[],\"prpCclauseplans\":[],\"prpCpayeeAccounts\":[],\"actualProduct\":null,\"prpCmainExtraVo\":null,\"Employees\":[],\"Props\":[],\"AgentDetails\":[],\"shipDrivers\":[],\"Drivers\":[],\"Addresses\":[],\"InsuredIdvLists\":[],\"Crosses\":[],\"Subs\":[],\"Agents\":[],\"Credits\":[],\"Constructs\":[],\"Itemkinds\":[{\"id\":{\"pkey\":\"\",\"itemKindNo\":1},\"riskCode\":\"EJQ\",\"familyNo\":null,\"familyName\":null,\"projectCode\":\"暂时写死\",\"clauseCode\":\"060037\",\"clauseName\":\"营运交通工具乘客意外伤害保险条款\",\"kindCode\":\"060006\",\"kindName\":\"在飞机中因意外伤害造成的身故、残疾\",\"itemNo\":1,\"itemCode\":null,\"itemDetailName\":null,\"groupNo\":1,\"modeCode\":\"EJQ330000i\",\"modeName\":\"西部航空航意险001天至007天\",\"startDate\":null,\"startHour\":22,\"endDate\":null,\"endHour\":22,\"model\":null,\"buyDate\":null,\"addressNo\":null,\"calculateFlag\":\"1\",\"currency\":\"CNY\",\"unitAmount\":1000000.00,\"quantity\":2,\"unit\":\"1\",\"value\":null,\"amount\":2000000.00,\"ratePeriod\":null,\"rate\":0.03000000,\"shortRateFlag\":\"3\",\"shortRate\":100.0000,\"prePremium\":null,\"calPremium\":60.00,\"basePremium\":null,\"benchMarkPremium\":null,\"discount\":1.000000,\"adjustRate\":null,\"unitPremium\":30.00,\"premiumB4Discount\":null,\"premium\":60.00,\"deductibleRate\":null,\"deductible\":null,\"taxFee\":3.40,\"taxFee_ys\":null,\"taxFee_gb\":0.00,\"taxFee_lb\":0.00,\"netPremium\":56.60,\"allTaxFee\":3.40,\"allNetPremium\":56.60,\"taxRate\":6.00,\"taxFlag\":\"2\",\"flag\":\"\",\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"policyNo\":\"\",\"proposalNo\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"prpCprofits\":[],\"iscalculateFlag\":null,\"userCount\":null,\"pack\":null,\"firstLevel\":null,\"methodType\":null,\"insuredQuantity\":null,\"clauseFlag\":\"1\",\"itemKindFactorFlag\":null,\"prpCitemKindTaxFees\":[],\"ItemKindDetails\":[]}],\"Limits\":[],\"Loans\":[],\"Engages\":[],\"Insureds\":[{\"id\":{\"pkey\":\"\",\"serialNo\":1},\"serialNo\":null,\"proposalNo\":\"\",\"policyNo\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"riskCode\":\"EJQ\",\"language\":\"C\",\"insuredType\":\"2\",\"insuredCode\":\"8888888888888888\",\"insuredName\":\"某某航空企业\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"\",\"insuredNature\":null,\"insuredFlag\":\"100000000000000000000000000000\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"9\",\"relateSerialNo\":null,\"identifyType\":\"31\",\"identifyNumber\":\"798046824\",\"unifiedSocialCreditCode\":null,\"creditLevel\":null,\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":null,\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":null,\"postCode\":null,\"phoneNumber\":null,\"faxNumber\":null,\"mobile\":\"18300000000\",\"netAddress\":null,\"email\":\"\",\"dateValid\":null,\"startDate\":\"2020-12-10 00:00:00\",\"endDate\":\"2020-12-16 00:00:00\",\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"\",\"age\":null,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":null,\"flag\":null,\"warningFlag\":null,\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"blackFlag\":null,\"importSerialNo\":null,\"prpCinsuredCreditInvests\":[],\"groupCode\":null,\"groupName\":null,\"dweller\":null,\"customerLevel\":null,\"insuredPYName\":null,\"groupNo\":1,\"itemNo\":null,\"importFlag\":null,\"smsFlag\":null,\"emailFlag\":null,\"sendPhone\":null,\"sendEmail\":null,\"subPolicyNo\":null,\"socialSecurityNo\":null,\"electronicflag\":null,\"insuredSort\":null,\"isHealthSurvey\":null,\"InsuredNatures\":[{\"id\":{\"pkey\":\"\",\"serialNo\":1},\"serialNo\":null,\"proposalNo\":\"\",\"policyNo\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"insuredFlag\":\"100000000000000000000000000000\",\"sex\":\"\",\"age\":null,\"birthday\":null,\"health\":null,\"jobTitle\":null,\"localWorkYears\":null,\"education\":null,\"totalWorkYears\":null,\"unit\":null,\"unitPhoneNumber\":null,\"unitAddress\":null,\"unitPostCode\":null,\"unitType\":null,\"dutyLevel\":null,\"dutyType\":null,\"occupationCode\":null,\"houseProperty\":null,\"localPoliceStation\":null,\"roomAddress\":null,\"roomPostCode\":null,\"selfMonthIncome\":null,\"familyMonthIncome\":null,\"incomeSource\":null,\"roomPhone\":null,\"mobile\":null,\"familySumQuantity\":null,\"marriage\":null,\"spouseName\":null,\"spouseBornDate\":null,\"spouseId\":null,\"spouseMobile\":null,\"spouseUnit\":null,\"spouseJobTitle\":null,\"spouseUnitPhone\":null,\"flag\":null,\"carType\":null,\"disablePartAndLevel\":null,\"moreLoanHouseFlag\":null,\"nation\":null,\"poorFlag\":null,\"licenseNo\":null,\"getLicenseDate\":null,\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"educationCode\":null,\"contactNo\":null,\"contactName\":null,\"certificationDate\":null,\"certificationNo\":null,\"addressCount\":null,\"importFlag\":null,\"socialFlag\":null,\"cardAmount\":null,\"usedAmount\":null,\"payAmount\":null,\"isPoverty\":null,\"importSerialNo\":null}],\"InsuredArtifs\":[]},{\"id\":{\"pkey\":\"\",\"serialNo\":2},\"serialNo\":null,\"proposalNo\":\"\",\"policyNo\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"riskCode\":\"EJQ\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"8888888888888888\",\"insuredName\":\"孙海军\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京\",\"insuredNature\":null,\"insuredFlag\":\"010000000000000000000000000000\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"9\",\"relateSerialNo\":1,\"identifyType\":\"01\",\"identifyNumber\":\"420101197407040111\",\"unifiedSocialCreditCode\":null,\"creditLevel\":null,\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":null,\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":null,\"postCode\":null,\"phoneNumber\":null,\"faxNumber\":null,\"mobile\":\"13100000000\",\"netAddress\":null,\"email\":\"\",\"dateValid\":null,\"startDate\":\"2020-12-10 00:00:00\",\"endDate\":\"2020-12-16 00:00:00\",\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"1\",\"age\":46,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":null,\"flag\":null,\"warningFlag\":null,\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"blackFlag\":null,\"importSerialNo\":null,\"prpCinsuredCreditInvests\":[],\"groupCode\":null,\"groupName\":null,\"dweller\":null,\"customerLevel\":null,\"insuredPYName\":null,\"groupNo\":1,\"itemNo\":null,\"importFlag\":null,\"smsFlag\":null,\"emailFlag\":null,\"sendPhone\":null,\"sendEmail\":null,\"subPolicyNo\":null,\"socialSecurityNo\":null,\"electronicflag\":null,\"insuredSort\":null,\"isHealthSurvey\":null,\"InsuredNatures\":[{\"id\":{\"pkey\":\"\",\"serialNo\":2},\"serialNo\":null,\"proposalNo\":\"\",\"policyNo\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"insuredFlag\":\"010000000000000000000000000000\",\"sex\":\"1\",\"age\":46,\"birthday\":\"1974-07-04\",\"health\":null,\"jobTitle\":null,\"localWorkYears\":null,\"education\":null,\"totalWorkYears\":null,\"unit\":null,\"unitPhoneNumber\":null,\"unitAddress\":null,\"unitPostCode\":null,\"unitType\":null,\"dutyLevel\":null,\"dutyType\":null,\"occupationCode\":null,\"houseProperty\":null,\"localPoliceStation\":null,\"roomAddress\":null,\"roomPostCode\":null,\"selfMonthIncome\":null,\"familyMonthIncome\":null,\"incomeSource\":null,\"roomPhone\":null,\"mobile\":null,\"familySumQuantity\":null,\"marriage\":null,\"spouseName\":null,\"spouseBornDate\":null,\"spouseId\":null,\"spouseMobile\":null,\"spouseUnit\":null,\"spouseJobTitle\":null,\"spouseUnitPhone\":\"1\",\"flag\":null,\"carType\":null,\"disablePartAndLevel\":null,\"moreLoanHouseFlag\":null,\"nation\":null,\"poorFlag\":null,\"licenseNo\":null,\"getLicenseDate\":null,\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"educationCode\":null,\"contactNo\":null,\"contactName\":null,\"certificationDate\":null,\"certificationNo\":null,\"addressCount\":null,\"importFlag\":null,\"socialFlag\":null,\"cardAmount\":null,\"usedAmount\":null,\"payAmount\":null,\"isPoverty\":null,\"importSerialNo\":null}],\"InsuredArtifs\":[]},{\"id\":{\"pkey\":\"\",\"serialNo\":3},\"serialNo\":null,\"proposalNo\":\"\",\"policyNo\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"riskCode\":\"EJQ\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"8888888888888888\",\"insuredName\":\"孙靖涵\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京\",\"insuredNature\":null,\"insuredFlag\":\"010000000000000000000000000000\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":\"9\",\"relateSerialNo\":1,\"identifyType\":\"01\",\"identifyNumber\":\"420101200608302901\",\"unifiedSocialCreditCode\":null,\"creditLevel\":null,\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":null,\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":null,\"postCode\":null,\"phoneNumber\":null,\"faxNumber\":null,\"mobile\":\"11800000000\",\"netAddress\":null,\"email\":\"\",\"dateValid\":null,\"startDate\":\"2020-12-10 00:00:00\",\"endDate\":\"2020-12-16 00:00:00\",\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":\"2\",\"age\":14,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":null,\"flag\":null,\"warningFlag\":null,\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"blackFlag\":null,\"importSerialNo\":null,\"prpCinsuredCreditInvests\":[],\"groupCode\":null,\"groupName\":null,\"dweller\":null,\"customerLevel\":null,\"insuredPYName\":null,\"groupNo\":1,\"itemNo\":null,\"importFlag\":null,\"smsFlag\":null,\"emailFlag\":null,\"sendPhone\":null,\"sendEmail\":null,\"subPolicyNo\":null,\"socialSecurityNo\":null,\"electronicflag\":null,\"insuredSort\":null,\"isHealthSurvey\":null,\"InsuredNatures\":[{\"id\":{\"pkey\":\"\",\"serialNo\":3},\"serialNo\":null,\"proposalNo\":\"\",\"policyNo\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"insuredFlag\":\"010000000000000000000000000000\",\"sex\":\"2\",\"age\":14,\"birthday\":\"2006-08-30\",\"health\":null,\"jobTitle\":null,\"localWorkYears\":null,\"education\":null,\"totalWorkYears\":null,\"unit\":null,\"unitPhoneNumber\":null,\"unitAddress\":null,\"unitPostCode\":null,\"unitType\":null,\"dutyLevel\":null,\"dutyType\":null,\"occupationCode\":null,\"houseProperty\":null,\"localPoliceStation\":null,\"roomAddress\":null,\"roomPostCode\":null,\"selfMonthIncome\":null,\"familyMonthIncome\":null,\"incomeSource\":null,\"roomPhone\":null,\"mobile\":null,\"familySumQuantity\":null,\"marriage\":null,\"spouseName\":null,\"spouseBornDate\":null,\"spouseId\":null,\"spouseMobile\":null,\"spouseUnit\":null,\"spouseJobTitle\":null,\"spouseUnitPhone\":\"1\",\"flag\":null,\"carType\":null,\"disablePartAndLevel\":null,\"moreLoanHouseFlag\":null,\"nation\":null,\"poorFlag\":null,\"licenseNo\":null,\"getLicenseDate\":null,\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"educationCode\":null,\"contactNo\":null,\"contactName\":null,\"certificationDate\":null,\"certificationNo\":null,\"addressCount\":null,\"importFlag\":null,\"socialFlag\":null,\"cardAmount\":null,\"usedAmount\":null,\"payAmount\":null,\"isPoverty\":null,\"importSerialNo\":null}],\"InsuredArtifs\":[]}],\"Coins\":[],\"SpecialFacs\":[],\"Batches\":[],\"Commissions\":[],\"Cargos\":[],\"Plans\":[],\"CoinsDetails\":[],\"Liabs\":[],\"Confines\":[],\"Rations\":[{\"id\":{\"modeCode\":\"1\",\"pkey\":\"\"},\"modeName\":\"西部航空航意险001天至007天\",\"planCode\":\"EJQ330000i\",\"serialNo\":null,\"itinerary\":null,\"sex\":null,\"age\":null,\"occupationCode\":null,\"jobTitle\":null,\"quantity\":2,\"rationCount\":1,\"groupDiscount\":null,\"insuredFlag\":null,\"countryCode\":null,\"sickRoomLevel\":null,\"journeyBack\":null,\"journeyEnd\":null,\"journeyStart\":null,\"remark\":null,\"updateFlag\":null,\"flag\":null,\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"tkey\":\"2020-12-08 14:37:37\",\"proposalNo\":\"\",\"policyNo\":\"\",\"discountType\":null,\"discountMode\":null,\"discountValue\":null,\"unitPremium\":null,\"premiumB4Discount\":null,\"premium\":null,\"planTypeCode\":null}],\"Items\":[],\"Fees\":[],\"Renewals\":[{\"proposalNo\":\"\",\"oldPolicyNo\":\"PEJQ202033018000000343\",\"policyNo\":\"\",\"flag\":null,\"insertTimeForHis\":null,\"operateTimeForHis\":null,\"tkey\":null,\"pkey\":\"\"}],\"CargoDetails\":[],\"Cprotocols\":[],\"Clauses\":[],\"Contributions\":[],\"CreditOths\":[],\"Commons\":[{\"pkey\":\"\",\"tkey\":\"2020-12-08 14:37:37\",\"proposalNo\":\"\",\"specialFlag\":\"0 0            \",\"ext1\":null,\"ext2\":null,\"ext3\":null,\"resourceCode\":null,\"resourceName\":null,\"qualityLevel\":null,\"insertTimeForHis\":\"2020-12-08 14:37:37\",\"operateTimeForHis\":\"2020-12-08 14:37:37\",\"newBusinessNature\":\"411\",\"scmsAuditNotion\":null,\"pay_method\":null,\"platformProjectCode\":\"CPI000574\",\"handler1Code_uni\":\"1294010382\",\"handlerCode_uni\":\"1294010382\",\"commonFlag\":\"0   0     0  0\",\"otherPolicyName\":null,\"groupName\":null,\"isHPDriveCus\":\"0\",\"startTime\":\"00:00\",\"endTime\":\"00:00\",\"salesCode\":null,\"electronic\":\"0\",\"electronicTitle\":null,\"electronicPhone\":null,\"socialinsPay\":null,\"socialinsNo\":null,\"projectCode\":\"\",\"projectName\":null,\"priorityFlag\":null,\"priorityMessage\":null,\"isAccredit\":null,\"accreditType\":null,\"accreditDate\":null,\"bankFlowNo\":null,\"sealNum\":null,\"policyNo\":\"\",\"classify\":null,\"overSeas\":\"0\",\"isClaim\":null,\"isCondition\":null,\"unifiedInsurance\":null,\"electronicEmail\":null,\"isRenewalTeam\":null,\"keyAccountCode\":null,\"isRenewal\":\"1\",\"isGIvesff\":null,\"isStatistics\":null,\"isInsureRate\":null,\"busiAccountType\":null,\"isPStage\":null,\"visaCode\":null,\"visaPrintCode\":\"\",\"visaNo\":null,\"isVisaCancel\":null,\"internetCode\":\"220018\",\"isPoverty\":null,\"isTargetedPoverty\":null,\"coMakecom\":\"\",\"coOperatorcode\":null,\"inputType\":null,\"deliverFlag\":null,\"deliverType\":null,\"addressee\":null,\"deliverTel\":null,\"deliverAddr\":null,\"isVsCard\":null,\"subinformation\":null,\"isRapidCalPremium\":null,\"externalPayFlag\":null,\"ownerFlag\":\"2\",\"signTag\":null,\"signState\":null,\"transFlag\":null,\"invokeFlag\":\"S\",\"invoiceCode\":null,\"reviewerName\":null,\"receivableFlag\":null,\"internationalFlag\":null,\"policyFactorFlag\":null,\"recallFlag\":null}],\"Coupon\":null,\"InsuredCataLists\":[]}}}";
        String value1 = "{\"status\":0,\"statusText\":\"Success\",\"data\":{\"proposalNo\":\"TZEW202033018000000166\",\"policyNo\":\"\",\"classCode\":\"10\",\"riskCode\":\"ZEW\",\"riskName\":\"校园足球运动责任保险\",\"projectCode\":\"\",\"contractNo\":\"88888888\",\"policySort\":\"1\",\"businessNature\":\"0\",\"language\":\"C\",\"policyType\":\"01\",\"agriFlag\":\"0\",\"operateDate\":\"2020-12-18 15:22:58\",\"startDate\":\"2020-12-19 15:22:58\",\"endDate\":\"2021-12-18 15:22:58\",\"startHour\":15,\"endHour\":15,\"disRate\":null,\"sumValue\":103.00,\"sumAmount\":6473.00,\"sumDiscount\":null,\"sumPremiumB4Discount\":null,\"couponAmount\":null,\"couponPremium\":null,\"minPremium\":null,\"sumPremium\":6.47,\"sumSubPrem\":0.00,\"sumQuantity\":1,\"policyCount\":1,\"judicalScope\":\"01\",\"argueSolution\":\"2\",\"arbitBoardName\":\"港浆塘醇泼\",\"payTimes\":1,\"makeCom\":\"33010400\",\"operateSite\":\"述男糟川原\",\"comCode\":\"33010400\",\"handlerCode\":\"18251488\",\"handler1Code\":\"18251488\",\"checkFlag\":\"4\",\"checkUpCode\":\"\",\"checkOpinion\":\"\",\"underWriteCode\":\"\",\"underWriteName\":\"\",\"operatorCode\":\"83298873\",\"inputTime\":\"2020-12-18 15:22:58\",\"underWriteEndDate\":\"2020-12-18\",\"statisticsYM\":\"\",\"agentCode\":\"000002000001\",\"coinsFlag\":\"00\",\"reinsFlag\":\"0000000000\",\"allinsFlag\":\"0\",\"underWriteFlag\":\"9\",\"jfeeFlag\":\"1\",\"inputFlag\":\"0\",\"undwrtSubmitDate\":\"2020-12-18\",\"othFlag\":\"000000YY00\",\"remark\":\"接口自动化测试_10001254\",\"checkCode\":\"\",\"flag\":\"         A\",\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:27:55\",\"payMode\":\"01\",\"payCode\":null,\"crossFlag\":\"0\",\"batchGroupNo\":null,\"sumTaxFee\":0.37,\"sumNetPremium\":6.10,\"prePremium\":null,\"pkey\":\"TZEW202033018000000166\",\"tkey\":\"2020-12-18 15:23:36\",\"currency\":\"CNY\",\"dmFlag\":\"A\",\"handler1Code_uni\":\"1233134176\",\"handlerCode_uni\":\"1233134176\",\"isAutoePolicy\":\"0\",\"salesCode\":\"83298873\",\"personOri\":\"0\",\"productCode\":null,\"productName\":null,\"approverCode\":null,\"pureRate\":1.0000,\"discount\":null,\"insuredCount\":null,\"auditNo\":null,\"auditNoToE\":null,\"crossSellType\":\"3\",\"inputSumPremium\":null,\"dutySumNetPremium\":null,\"freeSumNetPremium\":null,\"orderNo\":null,\"orderFlag\":null,\"policyPageVo\":null,\"prpCmainAccs\":[],\"prpCmainExts\":[],\"prpCmainBonds\":[],\"prpCextendInfos\":[],\"prpCmainAirLines\":[],\"prpCcommissions\":[],\"prpCcoeffs\":[],\"prpCmainAgris\":[],\"prpCprojects\":[],\"prpCprofitFactors\":[],\"prpCclauseplans\":[],\"prpCpayeeAccounts\":[],\"actualProduct\":\"\",\"prpCmainExtraVo\":null,\"Employees\":[],\"Props\":[],\"AgentDetails\":[],\"shipDrivers\":[],\"Drivers\":[],\"Addresses\":[{\"id\":{\"pkey\":\"TZEW202033018000000166\",\"addressNo\":1},\"addressNo\":null,\"proposalNo\":\"TZEW202033018000000166\",\"policyNo\":null,\"riskCode\":\"ZEW\",\"postCode\":\"100098\",\"addressCode\":null,\"addressName\":\"2147483647\",\"accountDate\":null,\"businessSource\":null,\"businessClass\":null,\"preFingure\":null,\"preChar\":null,\"flag\":null,\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"tkey\":\"2020-12-18 15:23:36\",\"latitude\":null,\"longitude\":null,\"provCode\":null,\"cityCode\":null,\"countyCode\":null,\"importFlag\":null}],\"InsuredIdvLists\":[],\"Crosses\":[],\"Subs\":[],\"Agents\":[],\"Credits\":[],\"Constructs\":[],\"Itemkinds\":[{\"id\":{\"pkey\":\"TZEW202033018000000166\",\"itemKindNo\":1},\"riskCode\":\"ZEW\",\"familyNo\":null,\"familyName\":null,\"projectCode\":\"\",\"clauseCode\":\"100822\",\"clauseName\":\"校园足球运动责任保险条款\",\"kindCode\":\"101024\",\"kindName\":\"校园足球运动责任\",\"itemNo\":1,\"itemCode\":\"000640\",\"itemDetailName\":\"公共责任\",\"groupNo\":1,\"modeCode\":\"1\",\"modeName\":null,\"startDate\":\"2020-12-19\",\"startHour\":15,\"endDate\":\"2021-12-18\",\"endHour\":15,\"model\":\"13\",\"buyDate\":null,\"addressNo\":1,\"calculateFlag\":\"1\",\"currency\":\"CNY\",\"unitAmount\":6473.00,\"quantity\":1,\"unit\":\"1\",\"value\":null,\"amount\":6473.00,\"ratePeriod\":null,\"rate\":1.00000000,\"shortRateFlag\":\"3\",\"shortRate\":100.0000,\"prePremium\":null,\"calPremium\":6.47,\"basePremium\":null,\"benchMarkPremium\":null,\"discount\":null,\"adjustRate\":1.000000,\"unitPremium\":null,\"premiumB4Discount\":null,\"premium\":6.47,\"deductibleRate\":null,\"deductible\":null,\"taxFee\":0.37,\"taxFee_ys\":null,\"taxFee_gb\":0.00,\"taxFee_lb\":0.00,\"netPremium\":6.10,\"allTaxFee\":0.37,\"allNetPremium\":6.10,\"taxRate\":6.00,\"taxFlag\":\"2\",\"flag\":null,\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"policyNo\":null,\"proposalNo\":\"TZEW202033018000000166\",\"tkey\":\"2020-12-18 15:23:36\",\"prpCprofits\":[],\"iscalculateFlag\":null,\"userCount\":null,\"pack\":null,\"firstLevel\":null,\"methodType\":\"1\",\"insuredQuantity\":1.00,\"clauseFlag\":\"1\",\"itemKindFactorFlag\":null,\"prpCitemKindTaxFees\":[],\"ItemKindDetails\":[]}],\"Limits\":[],\"Loans\":[],\"Engages\":[],\"Insureds\":[{\"id\":{\"pkey\":\"TZEW202033018000000166\",\"serialNo\":1},\"serialNo\":null,\"proposalNo\":\"TZEW202033018000000166\",\"policyNo\":null,\"tkey\":\"2020-12-18 15:23:36\",\"riskCode\":\"ZEW\",\"language\":\"C\",\"insuredType\":\"1\",\"insuredCode\":\"3200100000038747\",\"insuredName\":\"芈月\",\"insuredEName\":null,\"aliasName\":null,\"insuredAddress\":\"北京市市辖区霍营乡资金新干线4区9号楼2单元1340\",\"insuredNature\":\"3\",\"insuredFlag\":\"110000000000000000000000000000\",\"unitType\":null,\"appendPrintName\":null,\"insuredIdentity\":null,\"relateSerialNo\":null,\"identifyType\":\"01\",\"identifyNumber\":\"320301197105171869\",\"unifiedSocialCreditCode\":null,\"creditLevel\":null,\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"occupationCode\":null,\"educationCode\":null,\"bank\":null,\"accountName\":null,\"account\":null,\"linkerName\":null,\"postAddress\":null,\"postCode\":\"100098\",\"phoneNumber\":null,\"faxNumber\":null,\"mobile\":\"15006923049\",\"netAddress\":null,\"email\":\"123123239@qq.com\",\"dateValid\":null,\"startDate\":\"2020-12-19 00:00:00\",\"endDate\":\"2021-12-18 00:00:00\",\"benefitFlag\":null,\"benefitRate\":null,\"drivingLicenseNo\":null,\"changelessFlag\":null,\"sex\":null,\"age\":49,\"marriage\":null,\"driverAddress\":null,\"peccancy\":null,\"acceptLicenseDate\":null,\"receiveLicenseYear\":null,\"drivingYears\":null,\"causeTroubleTimes\":null,\"awardLicenseOrgan\":null,\"drivingCarType\":null,\"countryCode\":\"CHN\",\"versionNo\":null,\"auditstatus\":null,\"flag\":null,\"warningFlag\":null,\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"blackFlag\":null,\"importSerialNo\":null,\"prpCinsuredCreditInvests\":[],\"groupCode\":null,\"groupName\":null,\"dweller\":\"A\",\"customerLevel\":null,\"insuredPYName\":null,\"groupNo\":1,\"itemNo\":null,\"importFlag\":null,\"smsFlag\":null,\"emailFlag\":null,\"sendPhone\":null,\"sendEmail\":null,\"subPolicyNo\":null,\"socialSecurityNo\":null,\"electronicflag\":\"1\",\"insuredSort\":null,\"isHealthSurvey\":null,\"InsuredNatures\":[{\"id\":{\"pkey\":\"TZEW202033018000000166\",\"serialNo\":1},\"serialNo\":null,\"proposalNo\":\"TZEW202033018000000166\",\"policyNo\":null,\"tkey\":\"2020-12-18 15:23:36\",\"insuredFlag\":\"110000000000000000000000000000\",\"sex\":\"2\",\"age\":49,\"birthday\":\"1971-05-17\",\"health\":null,\"jobTitle\":null,\"localWorkYears\":null,\"education\":null,\"totalWorkYears\":null,\"unit\":null,\"unitPhoneNumber\":null,\"unitAddress\":null,\"unitPostCode\":null,\"unitType\":null,\"dutyLevel\":null,\"dutyType\":null,\"occupationCode\":null,\"houseProperty\":null,\"localPoliceStation\":null,\"roomAddress\":null,\"roomPostCode\":null,\"selfMonthIncome\":null,\"familyMonthIncome\":null,\"incomeSource\":null,\"roomPhone\":null,\"mobile\":null,\"familySumQuantity\":null,\"marriage\":null,\"spouseName\":null,\"spouseBornDate\":null,\"spouseId\":null,\"spouseMobile\":null,\"spouseUnit\":null,\"spouseJobTitle\":null,\"spouseUnitPhone\":\"1\",\"flag\":null,\"carType\":null,\"disablePartAndLevel\":null,\"moreLoanHouseFlag\":null,\"nation\":null,\"poorFlag\":null,\"licenseNo\":null,\"getLicenseDate\":null,\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"educationCode\":null,\"contactNo\":null,\"contactName\":null,\"certificationDate\":null,\"certificationNo\":null,\"addressCount\":null,\"importFlag\":null,\"socialFlag\":null,\"cardAmount\":null,\"usedAmount\":null,\"payAmount\":null,\"isPoverty\":null,\"importSerialNo\":null}],\"InsuredArtifs\":[{\"id\":{\"pkey\":\"TZEW202033018000000166\",\"serialNo\":1},\"serialNo\":null,\"proposalNo\":\"TZEW202033018000000166\",\"policyNo\":null,\"tkey\":\"2020-12-18 15:23:36\",\"insuredFlag\":\"110000000000000000000000000000\",\"leaderName\":null,\"leaderId\":null,\"leaderMobile\":null,\"leaderUnitPhone\":null,\"unitAddress\":null,\"phoneNumber\":null,\"postCode\":null,\"businessCode\":null,\"revenueRegistNo\":null,\"carType\":null,\"flag\":null,\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"possessNature\":null,\"businessSource\":null,\"businessSort\":null,\"enterpriseNature\":null,\"qualification\":null,\"project\":null,\"deposit\":null,\"assLiabRate\":null,\"enterpriseScale\":null,\"operationTime\":null,\"lastYinCome\":null,\"registerdCapita\":null,\"proStartDate\":null,\"proEndDate\":null,\"importFlag\":null,\"importSerialNo\":null}]}],\"Coins\":[],\"SpecialFacs\":[],\"Batches\":[],\"Commissions\":[],\"Cargos\":[],\"Plans\":[{\"tkey\":\"2020-12-18 15:23:36\",\"proposalNo\":\"TZEW202033018000000166\",\"policyNo\":null,\"id\":{\"pkey\":\"TZEW202033018000000166\",\"serialNo\":1},\"endorseNo\":null,\"payNo\":1,\"payReason\":\"R21\",\"planDate\":\"2020-12-19\",\"currency\":\"CNY\",\"subsidyrate\":null,\"planFee\":6.47,\"delinquentFee\":6.47,\"flag\":null,\"payDate\":null,\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"payType\":null,\"exchangeNo\":null,\"paymentcomplete\":null,\"taxFee\":0.37}],\"CoinsDetails\":[],\"Liabs\":[{\"proposalNo\":\"TZEW202033018000000166\",\"policyNo\":null,\"riskCode\":\"ZEW\",\"certificateNo\":null,\"certificateDate\":null,\"certificateDepart\":null,\"practiceDate\":null,\"businessDetail\":null,\"businessSite\":null,\"insureAreaCode\":null,\"insureArea\":null,\"saleArea\":\"销售区域范围1579507770790\",\"officeType\":null,\"bkWardStartDate\":null,\"bkWardEndDate\":null,\"staffCount\":4,\"preTurnOver\":100.00,\"nowTurnOver\":null,\"electricPower\":null,\"remark\":null,\"claimBase\":\"2\",\"flag\":null,\"guaranteeArea\":null,\"familyMembers\":null,\"goodsName\":null,\"hazardLevel\":null,\"totleCount\":null,\"quantity\":null,\"itemInfo\":null,\"disputeType\":null,\"court\":null,\"lineType\":null,\"insuredType\":null,\"isSignInsured\":null,\"collectBsae\":null,\"businessClass\":null,\"companyLevel\":null,\"companyType\":null,\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"pkey\":\"TZEW202033018000000166\",\"tkey\":\"2020-12-18 15:23:36\",\"singlePayRate\":null,\"ensureCardNo\":null}],\"Confines\":[],\"Rations\":[],\"Items\":[],\"Fees\":[{\"id\":{\"pkey\":\"TZEW202033018000000166\",\"currency\":\"CNY\"},\"proposalNo\":\"TZEW202033018000000166\",\"policyNo\":null,\"tkey\":\"2020-12-18 15:23:36\",\"riskCode\":\"ZEW\",\"amount\":6473.00,\"premiumB4Discount\":null,\"premium\":6.47,\"flag\":\"\",\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"sumTaxFee\":0.37,\"sumTaxFee_ys\":0.00,\"sumNetPremium\":6.10,\"sumTaxFee_gb\":0.00,\"sumTaxFee_lb\":0.00}],\"Renewals\":[],\"CargoDetails\":[],\"Cprotocols\":[],\"Clauses\":[],\"Contributions\":[],\"CreditOths\":[],\"Commons\":[{\"pkey\":\"TZEW202033018000000166\",\"tkey\":\"2020-12-18 15:23:36\",\"proposalNo\":\"TZEW202033018000000166\",\"specialFlag\":\"   0  0        \",\"ext1\":null,\"ext2\":null,\"ext3\":null,\"resourceCode\":null,\"resourceName\":null,\"qualityLevel\":null,\"insertTimeForHis\":\"2020-12-18 15:23:36\",\"operateTimeForHis\":\"2020-12-18 15:23:36\",\"newBusinessNature\":\"020\",\"scmsAuditNotion\":null,\"pay_method\":null,\"platformProjectCode\":null,\"handler1Code_uni\":\"1233134176\",\"handlerCode_uni\":\"1233134176\",\"commonFlag\":\"0   0     0\",\"otherPolicyName\":\"2147483647\",\"groupName\":null,\"isHPDriveCus\":\"0\",\"startTime\":\"22:58\",\"endTime\":\"22:58\",\"salesCode\":\"83298873\",\"electronic\":\"1\",\"electronicTitle\":\"0\",\"electronicPhone\":null,\"socialinsPay\":null,\"socialinsNo\":null,\"projectCode\":null,\"projectName\":null,\"priorityFlag\":\"1\",\"priorityMessage\":null,\"isAccredit\":null,\"accreditType\":null,\"accreditDate\":null,\"bankFlowNo\":null,\"sealNum\":null,\"policyNo\":null,\"classify\":\"A\",\"overSeas\":\"0\",\"isClaim\":null,\"isCondition\":null,\"unifiedInsurance\":\"0\",\"electronicEmail\":null,\"isRenewalTeam\":null,\"keyAccountCode\":null,\"isRenewal\":null,\"isGIvesff\":null,\"isStatistics\":null,\"isInsureRate\":null,\"busiAccountType\":null,\"isPStage\":null,\"visaCode\":null,\"visaPrintCode\":null,\"visaNo\":null,\"isVisaCancel\":null,\"internetCode\":null,\"isPoverty\":null,\"isTargetedPoverty\":null,\"coMakecom\":null,\"coOperatorcode\":null,\"inputType\":null,\"deliverFlag\":null,\"deliverType\":null,\"addressee\":null,\"deliverTel\":null,\"deliverAddr\":null,\"isVsCard\":null,\"subinformation\":null,\"isRapidCalPremium\":null,\"externalPayFlag\":null,\"ownerFlag\":null,\"signTag\":null,\"signState\":null,\"transFlag\":null,\"invokeFlag\":null,\"invoiceCode\":null,\"reviewerName\":null,\"receivableFlag\":null,\"internationalFlag\":null,\"policyFactorFlag\":null,\"recallFlag\":null}],\"Coupon\":null,\"InsuredCataLists\":[]}}";
        JSONObject jsonObject1 = JSONObject.parseObject(value).getJSONObject("data").getJSONObject("PolicyMain");
        String s = JSONObject.toJSONString(jsonObject1);

        ArrayList strings = new ArrayList();
        HashMap table_field = new HashMap();

        HashMap stringObjectHashMap = new HashMap();
//PrpCmai
        stringObjectHashMap.put("data","prpcmain");
        stringObjectHashMap.put("Employees","prpcname");
        stringObjectHashMap.put("Props","prpcmain_prop");
        stringObjectHashMap.put("AgentDetails","prpcagentdetail");
        stringObjectHashMap.put("shipDrivers","prpcshipdriver");
        stringObjectHashMap.put("Drivers","prpcdriver");
        stringObjectHashMap.put("Addresses","prpcaddress");
        stringObjectHashMap.put("InsuredIdvLists","prpcinsuredidvlist");
        stringObjectHashMap.put("Crosses","prpcmain_channel");
        stringObjectHashMap.put("Subs","prpcmainsub");
        stringObjectHashMap.put("Agents","prpcagent");
        stringObjectHashMap.put("Credits","prpcmain_credit");
        stringObjectHashMap.put("Constructs","prpcmain_construct");
        stringObjectHashMap.put("Itemkinds","prpcitemkind");
        stringObjectHashMap.put("Limits","prpclimit");
        stringObjectHashMap.put("Loans","prpcmain_loan");
        stringObjectHashMap.put("Engages","prpcengage");

        stringObjectHashMap.put("Insureds","prpcinsured");
        stringObjectHashMap.put("InsuredNatures","prpcinsurednature");
        stringObjectHashMap.put("InsuredArtifs","prpcinsuredartif");

        stringObjectHashMap.put("Coins","prpccoins");
        stringObjectHashMap.put("SpecialFacs","prpcspecialfac");
        stringObjectHashMap.put("Batches","prpcbatch");
        stringObjectHashMap.put("Commissions","prpccommissiondetail");
        stringObjectHashMap.put("Cargos","prpcmain_cargo");
        stringObjectHashMap.put("Plans","prpcplan");
        stringObjectHashMap.put("CoinsDetails","prpccoinsdetail");
        stringObjectHashMap.put("Liabs","prpcmain_liab");
        stringObjectHashMap.put("Confines","prpcclauseconfine");
        stringObjectHashMap.put("Rations","prpcration");
        stringObjectHashMap.put("Items","prpcitem");
        stringObjectHashMap.put("Fees","prpcfee");
        stringObjectHashMap.put("Renewals","prpcrenewal");
        stringObjectHashMap.put("CargoDetails","prpccargodetail");
        stringObjectHashMap.put("Cprotocols","prpcprotocol");
        stringObjectHashMap.put("Clauses","prpcclause");
        stringObjectHashMap.put("Contributions","prpccontribution");
        stringObjectHashMap.put("CreditOths","prpcitem_creditoth");
        stringObjectHashMap.put("Commons","prpcmain_common");
        stringObjectHashMap.put("InsuredCataLists","prpcinsuredcatalist");


        getFiledName(s,strings,table_field,stringObjectHashMap);
        System.out.println(strings);
    }
}
